package io.github.areifzyp.dot.gallery

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Kontrak katalog galeri (PRD §6.1). Mencegah regresi seperti widget yang sudah
 * jadi tetapi masih ditandai "Segera hadir", atau tombol pasang yang menunjuk
 * receiver yang tidak ada.
 */
class WidgetCatalogTest {

    @Test
    fun `semua widget di katalog sudah dapat dipasang`() {
        WidgetEntry.ALL.forEach { entry ->
            assertTrue("${entry.kind} harus available", entry.available)
        }
        assertEquals("katalog harus memuat 7 widget", 7, WidgetEntry.ALL.size)
    }

    @Test
    fun `setiap jenis punya receiver dan pratinjau`() {
        WidgetKind.values().forEach { kind ->
            assertNotNull("receiver untuk $kind tidak boleh null", WidgetPinRequest.receiverFor(kind))
            assertTrue("$kind harus ada di katalog", WidgetEntry.ALL.any { it.kind == kind })
        }
    }

    @Test
    fun `setiap widget glance punya activity konfigurasi`() {
        // Widget Teks berbasis RemoteViews dan tidak punya GlanceId, tetapi tetap
        // punya layar konfigurasi; Device/Music memang tanpa setelan.
        val tanpaSetelan = setOf(WidgetKind.DEVICE, WidgetKind.MUSIC)
        WidgetKind.values().forEach { kind ->
            if (kind in tanpaSetelan) {
                assertEquals("$kind seharusnya tanpa layar setelan", null, WidgetPinRequest.configActivityFor(kind))
            } else {
                assertNotNull("$kind butuh layar setelan", WidgetPinRequest.configActivityFor(kind))
            }
        }
    }

    @Test
    fun `widget teks adalah satu-satunya yang bukan glance`() {
        WidgetKind.values().forEach { kind ->
            val glance = WidgetPinRequest.glanceWidgetFor(kind)
            if (kind == WidgetKind.TEXT) {
                assertEquals("Widget Teks berbasis RemoteViews", null, glance)
            } else {
                assertNotNull("$kind harus punya GlanceAppWidget", glance)
            }
        }
    }
}
