package io.github.areifzyp.dot.snapshot

import android.content.Context
import android.view.View
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.test.core.app.ApplicationProvider
import io.github.areifzyp.dot.R
import io.github.areifzyp.dot.decorative.DecorativeWidget
import io.github.areifzyp.dot.decorative.DecorativeWidgetReceiver
import io.github.areifzyp.dot.music.MusicWidget
import io.github.areifzyp.dot.music.MusicWidgetReceiver
import io.github.areifzyp.dot.style.DotStyleSettings
import io.github.areifzyp.dot.style.DotStyleSettings.ThemeMode
import io.github.areifzyp.dot.system.DeviceStatusWidget
import io.github.areifzyp.dot.system.DeviceStatusWidgetReceiver
import io.github.areifzyp.dot.weather.WeatherWidget
import io.github.areifzyp.dot.weather.WeatherWidgetReceiver
import io.github.areifzyp.dot.widget.CalendarWidget
import io.github.areifzyp.dot.widget.CalendarWidgetReceiver
import io.github.areifzyp.dot.widget.ClockFreshnessStore
import io.github.areifzyp.dot.widget.ClockUpdateStamp
import io.github.areifzyp.dot.widget.ClockWidget
import io.github.areifzyp.dot.widget.ClockWidgetReceiver
import io.github.areifzyp.dot.widget.TextWidgetProvider
import io.github.areifzyp.dot.widget.WidgetClock
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Gate snapshot widget × tema — STATUS-MAP §4b.
 *
 * Setiap sel matriks merender widget sungguhan lewat `AppWidgetManager`
 * (Glance → `RemoteViews`, atau `RemoteViews` langsung) lalu menangkap pikselnya
 * dengan Roborazzi. Golden disimpan di `android/app/src/test/snapshots` dan
 * **diverifikasi** oleh `verifyRoborazziDebug` di CI — kegagalan gate memblokir
 * merge, bukan best-effort.
 *
 * Matriks (16 snapshot):
 *  - Jam: {terang, gelap} × {`OnTime`, `Delayed`} = 4
 *  - Kalender, Cuaca, Status HP, Musik, Dekoratif: {terang, gelap} = 10
 *  - Teks (satu-satunya RemoteViews): {terang, gelap} = 2
 *
 * Kenapa satu metode test, bukan 16: `SessionManager` Glance adalah singleton
 * per proses dan sesi widget tidak pernah "selesai" selama widget masih ada.
 * Kalau tiap sel jadi metode test sendiri, sesi sel pertama membuat sel
 * berikutnya menunggu sesi yang sama tanpa batas (timeout, tanpa error).
 * Karena itu seluruh matriks dijalankan berurutan dalam satu proses test, dan
 * kegagalan dikumpulkan agar pesannya tetap menyebut sel mana yang berubah.
 *
 * Rekam ulang golden hanya manual: `./gradlew recordRoborazziDebug`
 * (syarat arsitek: mode rekam tidak boleh jalan otomatis di CI).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], qualifiers = "w320dp-h320dp-xhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class WidgetSnapshotTest {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val failures = mutableListOf<String>()

    @Before
    fun setUp() {
        WidgetSnapshotHarness.prepare(context)
    }

    @Test
    fun widgetThemeMatrix() {
        // Jam: {terang, gelap} x {OnTime, Delayed}
        cell("clock_light_ontime", ThemeMode.LIGHT) { n ->
            glance(n, ClockWidgetReceiver::class.java, ClockWidget())
        }
        cell("clock_dark_ontime", ThemeMode.DARK) { n ->
            glance(n, ClockWidgetReceiver::class.java, ClockWidget())
        }
        cell("clock_light_delayed", ThemeMode.LIGHT, seed = ::seedDelayed) { n ->
            glance(n, ClockWidgetReceiver::class.java, ClockWidget())
        }
        cell("clock_dark_delayed", ThemeMode.DARK, seed = ::seedDelayed) { n ->
            glance(n, ClockWidgetReceiver::class.java, ClockWidget())
        }

        // Kalender
        cell("calendar_light", ThemeMode.LIGHT) { n ->
            glance(n, CalendarWidgetReceiver::class.java, CalendarWidget())
        }
        cell("calendar_dark", ThemeMode.DARK) { n ->
            glance(n, CalendarWidgetReceiver::class.java, CalendarWidget())
        }

        // Cuaca
        cell("weather_light", ThemeMode.LIGHT) { n ->
            glance(n, WeatherWidgetReceiver::class.java, WeatherWidget())
        }
        cell("weather_dark", ThemeMode.DARK) { n ->
            glance(n, WeatherWidgetReceiver::class.java, WeatherWidget())
        }

        // Status HP
        cell("device_light", ThemeMode.LIGHT) { n ->
            glance(n, DeviceStatusWidgetReceiver::class.java, DeviceStatusWidget())
        }
        cell("device_dark", ThemeMode.DARK) { n ->
            glance(n, DeviceStatusWidgetReceiver::class.java, DeviceStatusWidget())
        }

        // Musik
        cell("music_light", ThemeMode.LIGHT) { n ->
            glance(n, MusicWidgetReceiver::class.java, MusicWidget())
        }
        cell("music_dark", ThemeMode.DARK) { n ->
            glance(n, MusicWidgetReceiver::class.java, MusicWidget())
        }

        // Dekoratif
        cell("decorative_light", ThemeMode.LIGHT) { n ->
            glance(n, DecorativeWidgetReceiver::class.java, DecorativeWidget())
        }
        cell("decorative_dark", ThemeMode.DARK) { n ->
            glance(n, DecorativeWidgetReceiver::class.java, DecorativeWidget())
        }

        // Teks: satu-satunya jalur RemoteViews
        cell("text_light", ThemeMode.LIGHT) {
            WidgetSnapshotHarness.renderRemoteViews(context, TextWidgetProvider::class.java, R.layout.widget_text)
        }
        cell("text_dark", ThemeMode.DARK) {
            WidgetSnapshotHarness.renderRemoteViews(context, TextWidgetProvider::class.java, R.layout.widget_text)
        }

        if (failures.isNotEmpty()) {
            fail("Gate snapshot gagal pada ${failures.size} sel:\n" + failures.joinToString("\n"))
        }
    }

    /**
     * Satu sel matriks: set tema, siapkan state, render, tangkap.
     *
     * Kegagalan dikumpulkan (bukan langsung dilempar) supaya satu run
     * melaporkan semua golden yang berubah, bukan hanya yang pertama.
     */
    private fun cell(
        snapshot: String,
        mode: ThemeMode,
        seed: (() -> Unit)? = null,
        render: (String) -> View,
    ) {
        try {
            DotStyleSettings.setTheme(context, mode)
            seed?.invoke()
            WidgetSnapshotHarness.capture(snapshot, render(snapshot))
        } catch (t: Throwable) {
            failures += "$snapshot: ${t.javaClass.simpleName}: ${t.message}"
        }
    }

    private fun glance(
        snapshot: String,
        receiver: Class<out GlanceAppWidgetReceiver>,
        widget: GlanceAppWidget,
    ): View = WidgetSnapshotHarness.renderGlance(context, receiver, widget, snapshot)

    /**
     * UX-1/UX-4: stempel pembaruan terakhir 5 menit lalu → selisih ≥ 2 menit,
     * jadi widget wajib menampilkan penanda `TERTUNDA 09:36`.
     */
    private fun seedDelayed() {
        ClockFreshnessStore.save(
            context,
            ClockUpdateStamp(
                epochMillis = WidgetClock.millis() - DELAYED_BY_MS,
                minuteOfDay = 9 * 60 + 36,
            ),
        )
    }

    private companion object {
        /** UX-1: ambang tertunda ≥ 2 menit; dipakai 5 menit agar tidak ambigu. */
        const val DELAYED_BY_MS = 5 * 60_000L
    }
}
