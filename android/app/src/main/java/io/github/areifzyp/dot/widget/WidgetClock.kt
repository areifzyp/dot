package io.github.areifzyp.dot.widget

import java.time.Clock
import java.time.LocalDate
import java.time.ZoneId

/**
 * Sumber waktu tunggal untuk render widget.
 *
 * Kenapa ada: tampilan widget memuat waktu berjalan (digit jam, baris tanggal,
 * label penanda, tanggal kalender). Snapshot golden tidak boleh berubah tiap
 * menit/hari, jadi waktu render harus bisa dipakukan dari test.
 *
 * Sebelum ini setiap render memanggil `System.currentTimeMillis()` /
 * `LocalDate.now()` langsung. Robolectric **tidak** mem-shadows
 * `System.currentTimeMillis()`, sehingga golden akan gagal begitu menit
 * berganti — gate-nya jadi merah terus dan orang akan mematikannya.
 *
 * Default tetap `Clock.systemDefaultZone()`, jadi perilaku di perangkat tidak
 * berubah sama sekali; hanya test yang mengganti [clock].
 */
internal object WidgetClock {

    @Volatile
    var clock: Clock = Clock.systemDefaultZone()

    fun millis(): Long = clock.millis()

    /** Tanggal "hari ini" menurut zona [clock]; dipakai label tanggal & kalender. */
    fun today(): LocalDate = LocalDate.now(clock)

    /** Zona waktu yang dipakai untuk menurunkan jam dinding dari [millis]. */
    fun zone(): ZoneId = clock.zone
}
