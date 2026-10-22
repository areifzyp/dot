package io.github.areifzyp.dot.system

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import java.io.File

/**
 * Pembaca metrik sistem nyata untuk Widget Status HP (PRD §6.5).
 *
 * Semua fungsi mengembalikan [Metric] — bukan angka mentah — supaya metrik
 * yang tidak tersedia bisa ditandai "tidak tersedia" alih-alih 0 (PRD §6.10,
 * kriteria penerimaan §6.5: tanpa angka palsu).
 */
sealed class Metric {
    /** [value] 0..100 (persen), [label] teks absolut opsional (mis. "6,2 GB / 128 GB"). */
    data class Percent(val value: Int, val label: String? = null) : Metric()

    /** Metrik tidak tersedia di perangkat ini. */
    data object Unavailable : Metric()
}

/** Snapshot seluruh metrik Status HP (HP-1..HP-3). */
data class SystemStats(
    val battery: Metric,
    val charging: ChargingState,
    val temperatureC: Double?,
    val ram: Metric,
    val storage: Metric,
    val cpu: Metric,
)

enum class ChargingState { UNPLUGGED, CHARGING, FULL, UNKNOWN }

object SystemStatsReader {

    /**
     * HP-2: persen baterai + status pengisian dari sticky broadcast
     * ACTION_BATTERY_CHANGED (tidak butuh izin, selalu tersedia).
     */
    fun readBattery(context: Context): Pair<Metric, ChargingState> {
        val intent: Intent? = try {
            context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        } catch (_: Exception) {
            null
        }
        if (intent == null) return Metric.Unavailable to ChargingState.UNKNOWN

        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        if (level < 0 || scale <= 0) return Metric.Unavailable to ChargingState.UNKNOWN

        val percent = (level * 100f / scale).toInt().coerceIn(0, 100)
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN)
        val state = when (status) {
            BatteryManager.BATTERY_STATUS_CHARGING,
            BatteryManager.BATTERY_STATUS_DISCHARGING,
            BatteryManager.BATTERY_STATUS_NOT_CHARGING,
            -> ChargingState.UNPLUGGED
            BatteryManager.BATTERY_STATUS_FULL -> ChargingState.FULL
            else -> ChargingState.UNKNOWN
        }
        // BATTERY_STATUS_CHARGING terpisah dari NOT_CHARGING di atas
        val chargingNow = status == BatteryManager.BATTERY_STATUS_CHARGING ||
            status == BatteryManager.BATTERY_STATUS_FULL
        return Metric.Percent(percent) to when {
            status == BatteryManager.BATTERY_STATUS_CHARGING -> ChargingState.CHARGING
            chargingNow && state == ChargingState.FULL -> ChargingState.FULL
            else -> state
        }
    }

    /** Suhu baterai dalam °C (satu desimal), atau null bila sensor tidak tersedia. */
    fun readTemperature(context: Context): Double? {
        val intent = try {
            context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        } catch (_: Exception) {
            null
        } ?: return null
        val raw = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, Int.MIN_VALUE)
        if (raw == Int.MIN_VALUE || raw <= 0) return null
        return raw / 10.0
    }

    /** HP-3a: pemakaian RAM = (total - tersedia) / total. */
    fun readRam(context: Context): Metric {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            ?: return Metric.Unavailable
        val info = ActivityManager.MemoryInfo()
        am.getMemoryInfo(info)
        val total = info.totalMem
        if (total <= 0) return Metric.Unavailable
        val used = total - info.availMem
        val percent = (used * 100f / total).toInt().coerceIn(0, 100)
        return Metric.Percent(percent, "${formatGb(total - info.availMem)} / ${formatGb(total)}")
    }

    /** HP-3b: pemakaian penyimpanan internal via StatFs. */
    fun readStorage(): Metric {
        return try {
            val path = Environment.getDataDirectory()
            val stat = StatFs(path.path)
            val total = stat.blockCountLong * stat.blockSizeLong
            if (total <= 0) return Metric.Unavailable
            val free = stat.availableBlocksLong * stat.blockSizeLong
            val used = total - free
            val percent = (used * 100f / total).toInt().coerceIn(0, 100)
            Metric.Percent(percent, "${formatGb(used)} / ${formatGb(total)}")
        } catch (_: Exception) {
            Metric.Unavailable
        }
    }

    /**
     * HP-1 (P1, best-effort): beban CPU rata-rata dari delta /proc/stat antar sampel.
     * Membaca sekali selalu mengembalikan [Metric.Unavailable] karena tidak ada baseline;
     * pemanggil (DeviceStatsCollector) menyimpan sampel sebelumnya di [CpuSampler].
     */
    fun readCpu(sampler: CpuSampler): Metric = sampler.next()

    /** Format byte → GB dengan satu desimal (mis. "6,2 GB"). */
    fun formatGb(bytes: Long): String {
        val gb = bytes / 1024.0 / 1024.0 / 1024.0
        return String.format(java.util.Locale.getDefault(), "%.1f GB", gb)
    }

    fun readAll(context: Context, sampler: CpuSampler): SystemStats {
        val (battery, charging) = readBattery(context)
        return SystemStats(
            battery = battery,
            charging = charging,
            temperatureC = readTemperature(context),
            ram = readRam(context),
            storage = readStorage(),
            cpu = readCpu(sampler),
        )
    }
}

/**
 * Sampel beban CPU dari baris agregat `/proc/stat`.
 *
 * Baris: `cpu user nice system idle iowait irq softirq steal guest guest_nice`
 * Utilisasi = (total - idle-ish) / total, dihitung antar dua pembacaan.
 * Bila file tidak terbaca (kernel/sandbox membatasi), seluruh hasil = Unavailable.
 */
class CpuSampler(private val statPath: String = "/proc/stat") {

    internal data class Snapshot(val busy: Long, val total: Long)

    private var previous: Snapshot? = null

    /** @return persen utilisasi CPU sejak panggilan sebelumnya, atau Unavailable. */
    fun next(): Metric {
        val current = readSnapshot() ?: run { previous = null; return Metric.Unavailable }
        val prev = previous
        previous = current
        if (prev == null) return Metric.Unavailable
        val dTotal = current.total - prev.total
        val dBusy = current.busy - prev.busy
        if (dTotal <= 0) return Metric.Unavailable
        val percent = (dBusy * 100f / dTotal).toInt().coerceIn(0, 100)
        return Metric.Percent(percent)
    }

    /** Parse satu baris `cpu ` (agregat). Diekspos untuk unit test. */
    internal fun parseCpuLine(line: String): Snapshot? = parseSnapshot(line)

    private fun readSnapshot(): Snapshot? = try {
        File(statPath).bufferedReader().use { reader ->
            reader.lineSequence()
                .firstOrNull { it.startsWith("cpu ") }
                ?.let { parseSnapshot(it) }
        }
    } catch (_: Exception) {
        null
    }

    companion object {
        internal fun parseSnapshot(line: String): Snapshot? {
            val parts = line.trim().split(Regex("\\s+"))
            // parts[0] == "cpu"
            if (parts.size < 5) return null
            val values = parts.drop(1).mapNotNull { it.toLongOrNull() }
            if (values.size < 4) return null
            // idle = values[3] (index 3), iowait = values[4] bila ada
            val idle = values[3] + values.getOrElse(4) { 0L }
            val total = values.sum()
            if (total <= 0L) return null
            return Snapshot(busy = total - idle, total = total)
        }
    }
}