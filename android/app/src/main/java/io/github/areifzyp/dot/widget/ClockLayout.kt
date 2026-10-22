package io.github.areifzyp.dot.widget

/**
 * Tata letak isi widget jam — STATUS-MAP §4a, UX-3.
 *
 * Kenapa geometri ini dipisah dari composable: UX-3 ("munculnya penanda tidak
 * boleh mengubah posisi atau ukuran digit jam") adalah janji tentang *geometri*,
 * bukan tentang gambar. Janji itu hanya bisa bertahan kalau tinggi kotak digit
 * tidak pernah menjadi fungsi dari status kesegaran. Karena itu fungsi di sini
 * **tidak menerima** [ClockFreshness] sama sekali: slot penanda dipesan untuk
 * kedua status, sehingga tidak ada cabang yang bisa membuat digit mengecil
 * tepat ketika penanda muncul.
 *
 * Sebelumnya aritmetika ini hanya hidup di dalam composable, jadi UX-3 hanya
 * dijaga komentar — satu penyuntingan ceroboh sudah cukup untuk melanggarnya
 * tanpa ada test yang gagal. Diekstrak ke sini supaya [ClockLayoutTest] bisa
 * mengunci aritmetikanya.
 *
 * Semua satuan dp, kecuali disebut lain.
 */
object ClockLayout {

    /** Padding dalam kotak widget. */
    const val PAD_DP = 14f

    /** Ukuran font baris tanggal (JAM-2). */
    const val LABEL_SP = 12f

    /** Tinggi baris tanggal sebagai kelipatan [LABEL_SP] (line-height Doto). */
    const val LABEL_LINE_FACTOR = 1.7f

    /** Jarak antara kotak digit dan baris tanggal. */
    const val LABEL_GAP_DP = 6f

    /** Ukuran font penanda (UX-2). */
    const val MARKER_SP = 10f

    /** Jarak antara baris tanggal dan slot penanda. */
    const val MARKER_GAP_DP = 4f

    /** Tinggi slot penanda sebagai kelipatan [MARKER_SP]. */
    const val MARKER_SLOT_FACTOR = 1.8f

    /** Lantai tinggi kotak digit supaya widget sangat pendek tetap terbaca. */
    const val MIN_MATRIX_DP = 20f

    /** Tinggi slot penanda yang selalu dipesan, terisi maupun kosong. */
    val markerSlotHeightDp: Float
        get() = MARKER_SP * MARKER_SLOT_FACTOR

    /**
     * Total ruang yang dipesan untuk penanda: jarak + slot.
     *
     * Selalu > 0 dan konstan. Test mengunci ini supaya tidak ada yang
     * "menghemat" slot saat `OnTime` dan diam-diam merusak UX-3.
     */
    val reservedMarkerHeightDp: Float
        get() = MARKER_GAP_DP + markerSlotHeightDp

    /** Tinggi baris tanggal. */
    val labelRowHeightDp: Float
        get() = LABEL_SP * LABEL_LINE_FACTOR

    /**
     * Tinggi kotak digit untuk tinggi isi yang tersedia.
     *
     * Sengaja tanpa parameter status kesegaran — lihat doc kelas.
     */
    fun matrixHeightDp(availableHeightDp: Float): Float =
        (availableHeightDp - labelRowHeightDp - LABEL_GAP_DP - reservedMarkerHeightDp)
            .coerceAtLeast(MIN_MATRIX_DP)

    /**
     * Tinggi isi yang tersedia dari ukuran widget mentah, dengan fallback untuk
     * ukuran tak-hingga/terlalu kecil (launcher kadang melaporkan nilai aneh
     * sebelum widget diukur).
     */
    fun availableHeightDp(widgetHeightDp: Float, fallbackHeightDp: Float = 110f): Float =
        if (widgetHeightDp.isFinite() && widgetHeightDp > 60f) {
            widgetHeightDp - PAD_DP * 2
        } else {
            fallbackHeightDp - PAD_DP * 2
        }

    /** Lebar isi yang tersedia; aturan fallback sama dengan [availableHeightDp]. */
    fun availableWidthDp(widgetWidthDp: Float, fallbackWidthDp: Float = 250f): Float =
        if (widgetWidthDp.isFinite() && widgetWidthDp > 60f) {
            widgetWidthDp - PAD_DP * 2
        } else {
            fallbackWidthDp - PAD_DP * 2
        }
}
