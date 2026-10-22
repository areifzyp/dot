package io.github.areifzyp.dot.widget

import java.time.LocalTime
import java.util.Locale

/**
 * Stempel satu pembaruan jam yang berhasil (STATUS-MAP §4a).
 *
 * @param epochMillis    waktu dinding saat pembaruan terjadi; dipakai menghitung
 *                       selisih tanpa jebakan pergantian tengah malam atau zona waktu.
 * @param minuteOfDay    menit-dalam-hari yang saat itu ditampilkan (0..1439); dipakai
 *                       sebagai label `HH:MM` pada penanda, bukan dihitung ulang dari
 *                       [epochMillis] supaya label tetap benar bila zona waktu berubah.
 */
data class ClockUpdateStamp(
    val epochMillis: Long,
    val minuteOfDay: Int,
)

/**
 * Status kesegaran tampilan widget jam — STATUS-MAP §4a, UX-1 & UX-4.
 *
 * Alasan widget butuh state ini: Glance tidak bisa "mengamati dirinya sendiri".
 * Yang dilihat pengguna adalah hasil render terakhir, jadi jam yang berhenti
 * tampak identik dengan jam yang benar. Satu-satunya momen widget bisa jujur
 * adalah saat render berikutnya: di situ ia tahu berapa lama tampilan
 * sebelumnya sudah basi. Karena itu stempel pembaruan terakhir dibandingkan
 * dengan jam berjalan **saat render**, dan hasilnya dipakai untuk memutuskan
 * penanda (UX-1) — bukan diserahkan ke perilaku AlarmManager.
 *
 * Sengaja murni Kotlin tanpa dependensi Android agar bisa diuji di JVM.
 */
sealed interface ClockFreshness {

    /** Tampilan masih mengikuti menit berjalan; tidak ada penanda. */
    data object OnTime : ClockFreshness

    /** Pembaruan tertunda; widget harus menampilkan penanda + waktu pembaruan terakhir. */
    data class Delayed(val lastUpdate: ClockUpdateStamp) : ClockFreshness
}

/** Aturan ambang "tertunda" (UX-1) dan pembentukan label penanda (UX-2/UX-5). */
object ClockFreshnessPolicy {

    /**
     * UX-1: selisih ≥ 2 menit dianggap tertunda. Di bawahnya tidak ada penanda,
     * supaya render normal tidak berkedip.
     */
    const val DELAY_THRESHOLD_MINUTES = 2

    const val MINUTES_PER_DAY = 24 * 60

    private const val MINUTE_MS = 60_000L

    /**
     * Tentukan status dari baseline terakhir vs jam berjalan.
     *
     * [ClockUpdateStamp.epochMillis] yang berada di masa depan (jam perangkat
     * dimundurkan, atau baseline rusak) menghasilkan selisih negatif → `OnTime`,
     * sehingga widget kembali menggambar jam berjalan alih-alih menandai diri
     * "tertunda" secara keliru.
     */
    fun evaluate(lastUpdate: ClockUpdateStamp, nowEpochMillis: Long): ClockFreshness {
        val elapsedMs = nowEpochMillis - lastUpdate.epochMillis
        val thresholdMs = DELAY_THRESHOLD_MINUTES * MINUTE_MS
        return if (elapsedMs >= thresholdMs) {
            ClockFreshness.Delayed(lastUpdate)
        } else {
            ClockFreshness.OnTime
        }
    }

    /** Menit-dalam-hari (0..1439) untuk [time]. */
    fun minuteOfDay(time: LocalTime): Int = time.hour * 60 + time.minute

    /** Kebalikan [minuteOfDay]; nilai di luar sehari diputar (aman untuk input rusak). */
    fun toLocalTime(minuteOfDay: Int): LocalTime {
        val normalized = ((minuteOfDay % MINUTES_PER_DAY) + MINUTES_PER_DAY) % MINUTES_PER_DAY
        return LocalTime.of(normalized / 60, normalized % 60)
    }

    /** UX-2: label waktu pembaruan terakhir, `HH:MM` 24 jam, selalu dua digit. */
    fun formatLastUpdate(minuteOfDay: Int): String {
        val time = toLocalTime(minuteOfDay)
        return String.format(Locale.ROOT, "%02d:%02d", time.hour, time.minute)
    }

    /**
     * UX-5: status tidak boleh dikomunikasikan lewat warna saja. Teks ini dibaca
     * pembaca layar pada gambar penanda.
     */
    fun markerContentDescription(minuteOfDay: Int): String =
        "Pembaruan tertunda, terakhir ${formatLastUpdate(minuteOfDay)}"

    /**
     * Teks penanda yang terlihat (UX-2), bagian indikator berwarna aksen.
     * Waktu pembaruan terakhir digambar terpisah dengan gaya sekunder
     * ([formatLastUpdate]) supaya keduanya bisa memakai warna berbeda.
     */
    fun markerLabel(): String = "TERTUNDA"
}
