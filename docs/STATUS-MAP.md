# Peta Status Dot. — terverifikasi pada commit `e1c7f9c`

**Tanggal verifikasi:** 22 Oktober 2026 · **Commit:** `e1c7f9c` (`fix(android): settings screen correctness and real backup config`) · **VERSION:** `0.5.0`

Dokumen ini adalah **peta progres**: apa yang sudah ada di repo, apa yang belum, dan di mana buktinya. Acuan *keputusan* tetap [`PRD-Dot.md`](PRD-Dot.md) — PRD bersifat historis dan tidak lagi melacak progres.

Setiap baris status di bawah menyebut path berkas yang bisa diperiksa langsung di repo. Klaim yang belum diperiksa ditandai **belum diverifikasi**; tidak ada tebakan di dokumen ini.

---

## 1. Android — 7/7 selesai di tingkat kode

"Seluruhnya di tingkat kode" berarti: berkas implementasi ada di repo dan toolkit-nya terkonfirmasi. Ini **bukan** klaim bahwa widget sudah diuji di perangkat fisik — pengujian pemasangan di perangkat tidak tercakup dokumen ini.

Semua berkas di bawah `android/app/src/main/java/io/github/areifzyp/dot/`.

| Widget | PRD-ID | Berkas implementasi | Status |
|---|---|---|---|
| Jam | JAM-1..4 | [`widget/ClockWidget.kt`](../android/app/src/main/java/io/github/areifzyp/dot/widget/ClockWidget.kt) | Ada · Glance |
| Cuaca | CUA-1..6 | [`weather/WeatherWidget.kt`](../android/app/src/main/java/io/github/areifzyp/dot/weather/WeatherWidget.kt) | Ada · Glance |
| Kalender | KAL-1..5 | [`widget/CalendarWidget.kt`](../android/app/src/main/java/io/github/areifzyp/dot/widget/CalendarWidget.kt) | Ada · Glance |
| Teks | TXT-1..8 | [`widget/TextWidgetProvider.kt`](../android/app/src/main/java/io/github/areifzyp/dot/widget/TextWidgetProvider.kt) | Ada · RemoteViews |
| Status HP | HP-1..4 | [`system/DeviceStatusWidget.kt`](../android/app/src/main/java/io/github/areifzyp/dot/system/DeviceStatusWidget.kt) | Ada · Glance |
| Musik | MUS-1..4 | [`music/MusicWidget.kt`](../android/app/src/main/java/io/github/areifzyp/dot/music/MusicWidget.kt) | Ada · Glance |
| Dekoratif | DEK-1..5 | [`decorative/DecorativeWidget.kt`](../android/app/src/main/java/io/github/areifzyp/dot/decorative/DecorativeWidget.kt) | Ada · Glance |

Komposisi toolkit: **6 Glance + 1 RemoteViews**. Widget Teks adalah satu-satunya yang masih memakai RemoteViews; ini yang membuat setiap perubahan tampilan harus diuji di dua jalur berbeda (lihat §4b).

Unit test yang ada (`android/app/src/test/java/io/github/areifzyp/dot/`): `SmokeTest.kt`, `widget/DotMatrixTest.kt`, `widget/DotRenderTest.kt`, `widget/ClockUpdateSchedulerTest.kt`, `system/SystemStatsReaderTest.kt`, `decorative/DecorPatternsTest.kt`, `gallery/WidgetCatalogTest.kt`.

---

## 2. Plasma (pasca-Fase 4) — dibuat, belum diuji pasang

`plasma/` berisi 6 plasmoid dan satu skrip pemasangan. Setiap plasmoid punya `metadata.json` dengan `KPlugin.Id` yang sesuai nama foldernya.

| Plasmoid | `KPlugin.Name` | Bukti | Status |
|---|---|---|---|
| `org.dot.clock` | Dot Clock | [`metadata.json`](../plasma/org.dot.clock/metadata.json) | Dibuat · **belum diuji pasang** |
| `org.dot.calendar` | Dot Calendar | [`metadata.json`](../plasma/org.dot.calendar/metadata.json) | Dibuat · **belum diuji pasang** |
| `org.dot.weather` | Dot Weather | [`metadata.json`](../plasma/org.dot.weather/metadata.json) | Dibuat · **belum diuji pasang** |
| `org.dot.system` | Dot System Status | [`metadata.json`](../plasma/org.dot.system/metadata.json) | Dibuat · **belum diuji pasang** |
| `org.dot.music` | Dot Music | [`metadata.json`](../plasma/org.dot.music/metadata.json) | Dibuat · **belum diuji pasang** |
| `org.dot.text` | Dot Text Note | [`metadata.json`](../plasma/org.dot.text/metadata.json) | Dibuat · **belum diuji pasang** |

Pemasangan: [`plasma/install-all.sh`](../plasma/install-all.sh) menyalin keenam folder ke `$HOME/.local/share/plasma/plasmoids`. **Terverifikasi 22 Okt 2026 (PM):** skrip berhenti pada galat (`set -e`), dan baris `echo "Refreshing KPackage index..."` **tidak diikuti perintah apa pun** — murni teks, index tidak di-refresh. Apakah Plasma 6 butuh langkah refresh manual setelahnya tetap **belum diverifikasi**; penguji harus memastikannya.

Dua hazard lain di skrip yang sama, terverifikasi: `rm -rf "$DEST_DIR/$widget"` dijalankan **sebelum** `cp` — bila `cp` gagal, plasmoid lama sudah terhapus dan tidak ada rollback. Dan `set -e` tidak menolong bila `cp` berhasil sebagian.

Status "belum diuji pasang" konsisten dengan PRD baris 74: *"6 widget dibuat, instal & uji belum"*.

### Dekoratif di Plasma — di luar cakupan v1.0 (keputusan PM, 22 Oktober 2026)

Tidak ada `plasma/org.dot.decorative`. §12 PRD (dibekukan) menyebut urutan Plasma sebagai *"Jam, Cuaca, Kalender, Status Sistem, Musik, lalu gaya yang bisa diatur"* — Dekoratif tidak tercantum.

**Keputusan:** Dekoratif **tidak** termasuk cakupan Plasma v1.0.

**Alasan:**

1. Mengikuti §12 yang sudah dibekukan. Memasukkan Dekoratif berarti memperluas PRD historis secara eksplisit, bukan menyelesaikan pekerjaan yang tertinggal.
2. Dekoratif di Android bersifat statis tanpa sumber data (PRD §9.1 "Ukuran dan Pembaruan Widget", baris Dekoratif: *"statis, tanpa pembaruan berkala"*; §6.12: *"tidak memicu pembaruan latar belakang"*). Di Plasma nilai tambahnya paling kecil dibanding enam widget lain yang punya data hidup — dan Plasma sudah punya kebebasan menempatkan gambar statis.
3. v1.0 tinggal satu item (§3, uji instal). Menambah plasmoid ketujuh menambah satu siklus desain + uji pasang sebelum rilis, tanpa mengubah nilai rilis.

**Konsekuensi:** Dekoratif Plasma dicatat sebagai **kandidat pasca-v1.0**. Memasukkannya butuh keputusan operator (Arif) untuk memperluas §12; sampai itu ada, jangan dihitung sebagai pekerjaan yang tertinggal.

---

## 3. Increment berikutnya — satu-satunya pekerjaan v1.0 yang masih terbuka

**Uji instal 6 plasmoid di Plasma 6.** Tidak ada pekerjaan lain yang tersisa untuk v1.0; §1 dan §2 menutup sisanya.

Yang harus dicatat per plasmoid:

| Yang dicatat | Contoh isian |
|---|---|
| Terpasang | ya / tidak |
| Galat | pesan galat QML atau KPackage, bila ada |
| Tampilan | sesuai desain / menyimpang (sebutkan bagiannya) |

Kriteria selesai: keenam plasmoid tercatat dengan tiga kolom di atas terisi. Hasilnya masuk ke §2 sebagai pengganti status "belum diuji pasang".

---

## 4. Risiko terbuka

### a) Exact alarm — fallback inexact + penanda "pembaruan tertunda" (tertutup 22 Oktober 2026)

| | |
|---|---|
| **Risiko** | Di Android 12+, izin exact alarm bisa dicabut. Widget tetap menjadwalkan alarm inexact, tetapi tidak ada definisi kapan pembaruan dianggap "terlambat" dan tidak ada test untuk cabang penolakan izin. _(Kondisi sebelum 22 Oktober 2026 — sudah ditutup, lihat baris Status dan §4a-i.)_ |
| **Bukti di repo** | Fallback: [`widget/ClockWidget.kt`](../android/app/src/main/java/io/github/areifzyp/dot/widget/ClockWidget.kt) — `canScheduleExactAlarms()`, lalu `am.set(...)` sebagai fallback, plus `try/catch SecurityException` untuk izin yang dicabut di tengah jalan. State kesegaran: [`widget/ClockFreshness.kt`](../android/app/src/main/java/io/github/areifzyp/dot/widget/ClockFreshness.kt) (`OnTime`/`Delayed`, ambang di `ClockFreshnessPolicy`). Geometri: [`widget/ClockLayout.kt`](../android/app/src/main/java/io/github/areifzyp/dot/widget/ClockLayout.kt). Test: `ClockUpdateSchedulerTest` (8), `ClockExactAlarmPermissionTest` (2), `ClockFreshnessTest` (8), `ClockFreshnessStoreTest` (3), `ClockLayoutTest` (6). |
| **Pemilik** | Kriteria UX: **PM (Sekar)** — ditulis di §4a ini, bukan di PRD (PRD dibekukan). Test cabang izin: **frontend/dev Android**. |
| **Kriteria penerimaan** | (1) Kriteria UX "tidak pernah tampak diam" — dirinci UX-1..6 di bawah. (2) Ada unit test yang mencakup cabang izin ditolak. |
| **Status** | **AC-1 & AC-2 terpenuhi** (frontend, 22 Oktober 2026) — rincian bukti di §4a-i. Suite `:app:testDebugUnitTest`: 61 test / 0 gagal / 0 error. |

#### Kriteria UX "tidak pernah tampak diam" (ditetapkan PM, 22 Oktober 2026)

Kegagalan yang dicegah bukan "widget berhenti" — melainkan **widget menampilkan menit yang salah tanpa memberi tahu**. Jam yang diam terlihat identik dengan jam yang benar, jadi pengguna tidak punya cara mendeteksinya. Karena itu acuannya bukan "apakah alarm menyala", tapi "apakah tampilan jujur".

| ID | Kriteria | Cara uji |
|---|---|---|
| **UX-1** | **Ambang.** Widget dianggap *tertunda* bila menit yang ditampilkan berbeda dari menit berjalan **dan** selisihnya ≥ 2 menit. Di bawah 2 menit tidak ada penanda (mencegah kedipan saat render normal). | Unit test state machine dengan waktu disuntik: offset 0/1 menit → `OnTime`; 2/5/60 menit → `Delayed`. |
| **UX-2** | **Penanda.** Saat `Delayed`, widget menampilkan indikator visual (mengikuti gaya §8.3 Nothing, warna aksen) **dan** label waktu pembaruan terakhir berformat `HH:MM`. Label memakai gaya sekunder, bukan menggantikan digit jam. | Snapshot state `Delayed` (lihat §4b). |
| **UX-3** | **Tata letak tidak bergeser.** Munculnya penanda tidak boleh mengubah posisi atau ukuran digit jam. | Bandingkan snapshot `OnTime` vs `Delayed`: kotak digit jam identik. |
| **UX-4** | **Pulih.** Begitu pembaruan berhasil, penanda hilang pada render berikutnya. Tidak ada state "macet tertunda" setelah widget kembali akurat. | Unit test transisi `Delayed` → `OnTime`. |
| **UX-5** | **Tidak bergantung warna.** Penanda membawa `contentDescription` "Pembaruan tertunda, terakhir <HH:MM>"; status tidak boleh dikomunikasikan lewat warna saja. | Pemeriksaan aksesibilitas + tinjauan accessibility-auditor. |
| **UX-6** | **Terbaca tanpa interaksi.** Penanda tampil tanpa pengguna harus membuka aplikasi atau menekan widget. | Tinjauan manual. |

**Konsekuensi & wewenang pembalik:** ambang 2 menit dan bentuk penanda adalah keputusan produk yang bisa dibalik tanpa menyentuh PRD — cukup ubah tabel ini. Yang **tidak** boleh dibalik oleh implementasi: UX-1 (ambang didefinisikan, bukan diserahkan ke perilaku AlarmManager) dan UX-5 (status tidak boleh hanya warna). Keduanya yang membuat AC ini bisa diuji.

#### a-i) Status implementasi (frontend, 22 Oktober 2026)

**AC-1 — penanda "pembaruan tertunda".**

- **UX-1 (ambang di kode).** [`ClockFreshnessPolicy.DELAY_THRESHOLD_MINUTES`](../android/app/src/main/java/io/github/areifzyp/dot/widget/ClockFreshness.kt) = 2. `evaluate()` membandingkan stempel pembaruan sebelumnya dengan jam berjalan **saat render**; selisih ≥ 2 menit → `Delayed`, di bawahnya → `OnTime`. Perilaku `AlarmManager` tidak pernah menjadi sumber keputusan.
- **UX-2/UX-6 (penanda terlihat tanpa interaksi).** `ClockWidgetContent` menggambar baris tetap berisi indikator `TERTUNDA` (warna aksen) + label `HH:MM` (gaya sekunder). Keduanya digambar pada render yang sama, tanpa aksi pengguna.
- **UX-3 (digit tidak bergeser).** Slot penanda dipesan **selalu**, termasuk saat `OnTime`: `ClockLayout.reservedMarkerHeightDp` = `MARKER_GAP_DP` + `MARKER_SP × 1.8` = 22 dp, dan `ClockLayout` sengaja **tidak menerima** parameter status. Jadi tidak ada cabang yang bisa membuat kotak digit mengecil tepat ketika penanda muncul. Dikunci `ClockLayoutTest` (`markerSlotIsReservedEvenWhenOnTime`, `digitBoxIsIdenticalForOnTimeAndDelayed`, `rowsAndDigitsNeverOverflowAvailableHeight`).
- **UX-4 (pulih).** `provideGlance` mengevaluasi status dari stempel **sebelumnya**, lalu langsung menimpanya dengan stempel render ini (`ClockFreshnessStore.save`). Render berikutnya selalu membandingkan dengan stempel baru, sehingga tidak ada state "macet tertunda". Diuji `ClockFreshnessTest` (transisi `Delayed` → `OnTime`).
- **UX-5 (bukan warna saja).** `ClockFreshnessPolicy.markerContentDescription()` menghasilkan `"Pembaruan tertunda, terakhir HH:MM"`; kalimat lengkap dibawa satu `Image`, gambar label waktu memakai `contentDescription = null` supaya pembaca layar tidak mengumumkan dua kali.

**AC-2 — unit test cabang izin exact alarm ditolak.** 10 test, semuanya menyuntik `sdkInt` dan status izin sehingga tidak butuh perangkat Android 12+:

| Test | Cabang yang dijaga |
|---|---|
| `ClockUpdateSchedulerTest.beforeAndroid12ExactAlarmIsAlwaysAllowed` | pra-Android 12 → `EXACT` tanpa izin |
| `android12AndAboveWithPermissionUsesExactAlarm` | izin ada → `setExactAndAllowWhileIdle` |
| `android12AndAboveWithoutPermissionFallsBackToInexact` | izin tidak ada → `set` inexact |
| `deniedPermissionNeverCallsExactAlarm` | izin ditolak → exact tidak pernah dipanggil |
| `allowedPermissionCallsExactAlarmOnly` | izin ada → tidak ada fallback inexact |
| `securityExceptionFromRevokedPermissionFallsBackToInexact` | izin dicabut di tengah jalan → `SecurityException` ditangkap, jatuh ke inexact |
| `ClockExactAlarmPermissionTest.permissionDeniedStillSchedulesAnAlarm` | end-to-end: izin ditolak tetap menghasilkan alarm terjadwal |
| `permissionGrantedSchedulesExactAlarm` | end-to-end: izin ada → alarm exact |
| `exactlyOnMinuteBoundarySchedulesFullMinute`, `midMinuteSchedulesRemainder` | aritmetika `millisToNextMinute` (test lama, tetap hijau) |

Test-test ini dibuktikan **menggigit** lewat mutation testing, bukan sekadar hijau:

- `alarmMode()` dipaksa selalu `EXACT` → `permissionDeniedStillSchedulesAnAlarm` + `android12AndAboveWithoutPermissionFallsBackToInexact` gagal.
- `try/catch SecurityException` dihapus dari `schedule()` → `securityExceptionFromRevokedPermissionFallsBackToInexact` gagal.
- `- reservedMarkerHeightDp` dihapus dari `ClockLayout.matrixHeightDp()` → `ClockLayoutTest` gagal 2 test.

**Yang belum tertutup dan sengaja ditinggalkan terbuka:**

- UX-2/UX-3 tetap meminta **snapshot** `OnTime` vs `Delayed`; itu milik gate §4b, dan alat snapshot belum ada di repo. Yang dikunci di sini adalah aritmetika geometrinya, bukan pikselnya.
- **Keputusan desain untuk PM:** memesan slot penanda secara permanen membuat kotak digit menyusut 22 dp dibanding sebelum perubahan ini (pada widget bawaan 110 dp: 33,6 dp, sebelumnya 55,6 dp). Itu harga dari UX-3 — satu-satunya alternatif adalah membiarkan digit bergeser saat penanda muncul. Kalau PM lebih memilih digit besar dengan penanda yang mengambil ruang hanya saat `Delayed`, UX-3 harus dilonggarkan lebih dulu. Angka `MARKER_SP`, `MARKER_SLOT_FACTOR`, dan ambang 2 menit bisa diubah di `ClockLayout`/`ClockFreshnessPolicy` tanpa menyentuh PRD.

### b) Snapshot test — tidak ada gate visual untuk hybrid 6 Glance + 1 RemoteViews

| | |
|---|---|
| **Risiko** | Tidak ada yang mencegah regresi tampilan. Enam widget Glance dan satu widget RemoteViews punya jalur render berbeda, jadi regresi bisa lolos tanpa terdeteksi oleh unit test biasa. |
| **Bukti di repo** | [`.github/workflows/ci.yml`](../.github/workflows/ci.yml) hanya menjalankan `:app:testDebugUnitTest` dan `:app:assembleDebug`. Tidak ada dependensi snapshot di `android/app/build.gradle.kts` (tidak ada Paparazzi/Roborazzi). |
| **Pemilik** | **frontend/dev Android** (gate CI), ditinjau arsitek untuk pemilihan alat snapshot. |
| **Kriteria penerimaan** | Gate snapshot per widget × tema (terang/gelap), dijalankan di CI, dan kegagalan gate memblokir merge. **Wajib mencakup state `Delayed` widget Jam** (UX-2/UX-3 §4a) — kalau tidak, satu-satunya state baru yang ditambahkan untuk risiko §4a lolos tanpa gate visual. |

### c) Token — `design/tokens.json` disebut SSOT tetapi nol konsumen mesin

| | |
|---|---|
| **Risiko** | Token dideklarasikan sebagai single source of truth, tetapi tidak ada kode yang membacanya. Nilai ditranskripsi tangan ke Kotlin, sehingga `tokens.json` dan `Color.kt` bisa menyimpang tanpa ada yang mendeteksi. |
| **Bukti di repo** | [`design/tokens.json`](../design/tokens.json) hanya dirujuk sebagai komentar di [`widget/ClockWidget.kt:72`](../android/app/src/main/java/io/github/areifzyp/dot/widget/ClockWidget.kt) dan [`ui/theme/Color.kt:5`](../android/app/src/main/java/io/github/areifzyp/dot/ui/theme/Color.kt). Tidak ada skrip generator di repo. Catatan tambahan: rujukan mati ke `design/tokens.md` (yang **tidak ada** di repo) sudah dihapus dari `docs/README.md`, `design/README.md`, dan `.cursor/rules.md` di commit pembekuan. |
| **Pemilik** | **Arsitek** (generator kanonik + step CI — bagian dari increment ekstraksi kontrak token yang sudah approve-with-amendments). |
| **Kriteria penerimaan** | (1) Satu generator kanonik `tokens.json` → `Color.kt`. (2) Generator dijalankan di CI sehingga drift gagal build. (3) PRD dan README menyebut generator tersebut. Rujukan mati ke `design/tokens.md` sudah dihapus di `docs/README.md` dan `design/README.md` (commit pembekuan); sisa `layout.md`/`icons/` juga sudah dibersihkan dari `design/README.md`. |

---

## 5. Konvensi

**Repo menang atas dokumen.** Bila dokumen dan kode berbeda, dokumen yang salah — perbarui dokumen, jangan mengubah kode agar cocok dengan dokumen. Konsekuensi praktis:

- Setiap klaim status di dokumen ini harus punya path bukti. Klaim tanpa path dihapus atau ditandai **belum diverifikasi**.
- Bila sebuah berkas dipindahkan atau dihapus, perbarui peta ini pada commit yang sama.
- Jangan menautkan berkas yang belum ada di repo, termasuk berkas yang "sedang direncanakan".
