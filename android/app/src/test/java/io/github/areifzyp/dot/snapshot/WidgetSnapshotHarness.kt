package io.github.areifzyp.dot.snapshot

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.appwidget.AppWidgetProviderInfo
import android.content.ComponentName
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.os.SystemClock
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.glance.appwidget.AppWidgetId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.work.Configuration
import androidx.work.WorkManager
import com.github.takahirom.roborazzi.captureRoboImage
import io.github.areifzyp.dot.R
import io.github.areifzyp.dot.widget.WidgetClock
import io.github.areifzyp.dot.widget.widgetBackgroundArgb
import kotlinx.coroutines.runBlocking
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowAppWidgetManager
import org.robolectric.shadows.ShadowStatFs
import java.time.Clock
import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.TimeZone
import java.util.concurrent.Executor

/**
 * Harness gate snapshot widget (STATUS-MAP §4b).
 *
 * Proyek ini hybrid: 6 widget Glance + 1 RemoteViews. Kedua jalur dirender
 * lewat `AppWidgetManager` sungguhan (bukan layoutlib), lalu pikselnya
 * ditangkap Roborazzi — jadi yang dibandingkan adalah hasil render yang sama
 * dengan yang dilihat pengguna, termasuk bitmap dari [io.github.areifzyp.dot.widget.DotRender].
 *
 * Kenapa begini, bukan `provideComposable` dari `glance-appwidget-testing`:
 * API itu hanya memberi pohon semantik, bukan piksel. Satu-satunya jalan ke
 * piksel adalah `RemoteViews` hasil translasi Glance, dan `RemoteViews` hanya
 * dibuat oleh `AppWidgetSession` di dalam `GlanceAppWidget.update()`. `update()`
 * menyerahkan sesi ke `SessionWorker` (WorkManager), jadi WorkManager harus
 * hidup dan eksekutornya sinkron agar sesi berjalan di dalam test, bukan
 * menganggur di antrean.
 */
internal object WidgetSnapshotHarness {

    /** Ukuran widget yang dipakai seluruh matriks, dalam dp (setara 4x2 sel). */
    const val WIDGET_WIDTH_DP = 250
    const val WIDGET_HEIGHT_DP = 120

    /**
     * Waktu tetap untuk snapshot: 2026-10-22 09:41 waktu Jakarta.
     *
     * Dikunci supaya digit jam, label tanggal, dan label penanda tidak berubah
     * tiap kali golden direkam — tanpa ini golden akan gagal tiap menit.
     */
    const val FIXED_TIME_ZONE = "Asia/Jakarta"
    const val FIXED_HOUR = 9
    const val FIXED_MINUTE = 41

    /** Batas tunggu komposisi Glance; cukup longgar untuk CI yang lambat. */
    private const val COMPOSE_TIMEOUT_MS = 20_000L

    /** Id app widget sintetis mulai dari sini supaya tidak bentrok dengan id default Robolectric. */
    private const val FIRST_WIDGET_ID = 1000

    private const val GATE_ERROR_TEXT = "Can't show content"

    /**
     * Siapkan lingkungan render: zona waktu tetap, jam tetap, WorkManager sinkron.
     *
     * `WorkManager.initialize` hanya boleh sekali per proses; tiap metode test
     * Robolectric punya Application baru, jadi delegate lama dilepas lebih dulu
     * agar sesi Glance memakai context yang benar.
     */
    fun prepare(context: Context) {
        TimeZone.setDefault(TimeZone.getTimeZone(FIXED_TIME_ZONE))
        WidgetClock.clock = Clock.fixed(
            ZonedDateTime.of(2026, 10, 22, FIXED_HOUR, FIXED_MINUTE, 0, 0, ZoneId.of(FIXED_TIME_ZONE)).toInstant(),
            ZoneId.of(FIXED_TIME_ZONE),
        )
        stubStorage()
        resetWorkManagerDelegate()
        WorkManager.initialize(
            context,
            Configuration.Builder().setExecutor(Executor { it.run() }).build(),
        )
    }

    /**
     * Widget Status HP membaca `StatFs` pada data dir — di Robolectric itu
     * partisi host, jadi angkanya berubah antar mesin. Dipatok supaya golden
     * tidak gagal di CI karena disk runner berbeda.
     */
    private fun stubStorage() {
        try {
            ShadowStatFs.registerStats(
                Environment.getDataDirectory(),
                31_250_000,
                1_500_000,
                29_750_000,
            )
        } catch (_: Throwable) {
            // ShadowStatFs hilang di Robolectric baru → widget Status HP akan
            // memakai angka host; itu akan terlihat sebagai golden yang gagal.
        }
    }

    private fun resetWorkManagerDelegate() {
        try {
            val impl = Class.forName("androidx.work.impl.WorkManagerImpl")
            impl.getMethod("setDelegate", impl).invoke(null, null)
        } catch (_: Throwable) {
            // Bila API internal berubah, initialize() di bawah akan memberi error jelas.
        }
    }

    /**
     * Render widget RemoteViews (jalur lama): provider mengisi `RemoteViews`,
     * Robolectric menyimpannya, lalu view-nya diinflate dan digambar.
     */
    fun renderRemoteViews(
        context: Context,
        providerClass: Class<out AppWidgetProvider>,
        layoutId: Int,
    ): View {
        val shadow = shadowOf(AppWidgetManager.getInstance(context))
        val id = shadow.createWidget(providerClass, layoutId)
        idleMainLooper()
        return shadow.getViewFor(id) ?: error("RemoteViews tidak terbentuk untuk ${providerClass.simpleName}")
    }

    /**
     * Render widget Glance: `update()` menjalankan sesi komposisi, hasilnya
     * `RemoteViews` yang dipasang ke app widget id.
     */
    fun renderGlance(
        context: Context,
        receiverClass: Class<out GlanceAppWidgetReceiver>,
        widget: GlanceAppWidget,
        snapshotName: String,
    ): View {
        val awm = AppWidgetManager.getInstance(context)
        val shadow = shadowOf(awm)
        // Id unik per snapshot: state sesi Glance hidup sepanjang proses test,
        // jadi id yang sama di dua test membuat test kedua menunggu sesi test
        // pertama dan tidak pernah selesai.
        val id = uniqueWidgetId(snapshotName)
        val info = providerInfo(context, receiverClass)
        shadow.addInstalledProvider(info)
        shadow.bindAppWidgetId(id, info.provider)
        shadow.putWidgetInfo(id, info)
        // Tanpa ini Robolectric memakai ulang view lama saat update,
        // sehingga hasil komposisi Glance tidak pernah terlihat.
        shadow.setAlwaysRecreateViewsDuringUpdate(true)
        awm.updateAppWidgetOptions(id, sizeOptions())

        updateOffMainThread(widget, context, id)
        return awaitComposition(shadow, id)
    }

    private fun uniqueWidgetId(snapshotName: String): Int =
        (snapshotName.hashCode() and 0x7FFF) + FIRST_WIDGET_ID

    private fun providerInfo(
        context: Context,
        receiverClass: Class<out GlanceAppWidgetReceiver>,
    ): AppWidgetProviderInfo = AppWidgetProviderInfo().apply {
        provider = ComponentName(context, receiverClass)
        initialLayout = R.layout.widget_clock_loading
        minWidth = WIDGET_WIDTH_DP
        minHeight = WIDGET_HEIGHT_DP
        minResizeWidth = WIDGET_WIDTH_DP
        minResizeHeight = WIDGET_HEIGHT_DP
        resizeMode = AppWidgetProviderInfo.RESIZE_BOTH
        widgetCategory = AppWidgetProviderInfo.WIDGET_CATEGORY_HOME_SCREEN
    }

    private fun sizeOptions(): Bundle = Bundle().apply {
        putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, WIDGET_WIDTH_DP)
        putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, WIDGET_WIDTH_DP)
        putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, WIDGET_HEIGHT_DP)
        putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, WIDGET_HEIGHT_DP)
    }

    /**
     * `update()` dijalankan di thread lain: komposisi Glance butuh main looper
     * yang bebas, sedangkan thread test Robolectric *adalah* main looper.
     */
    private fun updateOffMainThread(widget: GlanceAppWidget, context: Context, id: Int) {
        var done = false
        var failure: Throwable? = null
        val worker = Thread {
            try {
                runBlocking { widget.update(context, AppWidgetId(id)) }
            } catch (t: Throwable) {
                failure = t
            } finally {
                done = true
            }
        }
        worker.isDaemon = true
        worker.start()
        val deadline = System.currentTimeMillis() + COMPOSE_TIMEOUT_MS
        while (!done && System.currentTimeMillis() < deadline) {
            advanceMainLooper(20)
            Thread.sleep(5)
        }
        failure?.let { throw AssertionError("Glance update() gagal untuk ${widget.javaClass.simpleName}", it) }
        check(done) { "Glance update() tidak selesai dalam ${COMPOSE_TIMEOUT_MS}ms" }
    }

    /**
     * Tunggu sampai `RemoteViews` placeholder diganti hasil komposisi.
     *
     * Komposisi Glance berjalan di `AndroidUiDispatcher` (frame Choreographer),
     * jadi looper harus dimajukan dengan `idleFor`, bukan sekadar `idle`.
     */
    private fun awaitComposition(
        shadow: ShadowAppWidgetManager,
        id: Int,
    ): View {
        val deadline = System.currentTimeMillis() + COMPOSE_TIMEOUT_MS
        while (System.currentTimeMillis() < deadline) {
            advanceMainLooper(50)
            val view = shadow.getViewFor(id)
            if (view != null) {
                val error = findText(view, GATE_ERROR_TEXT)
                if (error != null) {
                    throw AssertionError("Glance gagal menyusun widget: \"$GATE_ERROR_TEXT\" muncul di RemoteViews")
                }
                if (countImages(view) > 0) return view
            }
        }
        val logs = org.robolectric.shadows.ShadowLog.getLogs()
            .filter { it.throwable != null }
            .takeLast(3)
            .joinToString(" ;; ") { "${it.tag}: ${it.throwable}" }
        throw AssertionError("Komposisi Glance tidak selesai dalam ${COMPOSE_TIMEOUT_MS}ms; log=$logs")
    }

    private fun advanceMainLooper(millis: Long) {
        shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(millis))
    }

    private fun idleMainLooper() {
        repeat(4) { advanceMainLooper(20) }
    }

    private fun countImages(view: View): Int {
        var count = if (view is ImageView) 1 else 0
        if (view is ViewGroup) for (i in 0 until view.childCount) count += countImages(view.getChildAt(i))
        return count
    }

    private fun findText(view: View, text: String): TextView? {
        if (view is TextView && view.text?.toString() == text) return view
        if (view is ViewGroup) for (i in 0 until view.childCount) {
            findText(view.getChildAt(i), text)?.let { return it }
        }
        return null
    }

    /** Direktori golden, relatif terhadap root modul `:app`. */
    private const val SNAPSHOT_DIR = "src/test/snapshots"

    /**
     * Tangkap golden. Nama berkas = `<widget>_<tema>[_<state>].png`.
     *
     * Path harus relatif terhadap root modul dan sama dengan `roborazzi { outputDir }`
     * di `build.gradle.kts`; `captureRoboImage` menulis relatif ke working dir test.
     */
    fun capture(name: String, view: View) {
        val density = view.context.resources.displayMetrics.density
        val widthPx = (WIDGET_WIDTH_DP * density).toInt()
        val heightPx = (WIDGET_HEIGHT_DP * density).toInt()
        view.measure(
            View.MeasureSpec.makeMeasureSpec(widthPx, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(heightPx, View.MeasureSpec.EXACTLY),
        )
        view.layout(0, 0, widthPx, heightPx)
        // Roborazzi `View.captureRoboImage` menuntut context berupa Activity;
        // view app widget tidak punya Activity, jadi digambar ke bitmap dulu.
        val bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(widgetBackgroundArgb(view.context))
        view.draw(canvas)
        bitmap.captureRoboImage("$SNAPSHOT_DIR/$name.png")
    }
}
