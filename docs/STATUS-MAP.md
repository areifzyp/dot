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

- UX-2/UX-3 tetap meminta **snapshot** `OnTime` vs `Delayed`; itu milik gate §4b. Alat snapshotnya sudah ada sejak 22 Oktober 2026 (Roborazzi, 16 golden — lihat §4b-i); yang dikunci di sini adalah aritmetika geometrinya, bukan pikselnya.
- **Keputusan desain untuk PM:** memesan slot penanda secara permanen membuat kotak digit menyusut 22 dp dibanding sebelum perubahan ini (pada widget bawaan 110 dp: 33,6 dp, sebelumnya 55,6 dp). Itu harga dari UX-3 — satu-satunya alternatif adalah membiarkan digit bergeser saat penanda muncul. Kalau PM lebih memilih digit besar dengan penanda yang mengambil ruang hanya saat `Delayed`, UX-3 harus dilonggarkan lebih dulu. Angka `MARKER_SP`, `MARKER_SLOT_FACTOR`, dan ambang 2 menit bisa diubah di `ClockLayout`/`ClockFreshnessPolicy` tanpa menyentuh PRD.

### b) Snapshot test — tidak ada gate visual untuk hybrid 6 Glance + 1 RemoteViews (gate CI terpasang 22 Oktober 2026; AC-2b branch protection masih terbuka)

| | |
|---|---|
| **Risiko** | Tidak ada yang mencegah regresi tampilan. Enam widget Glance dan satu widget RemoteViews punya jalur render berbeda, jadi regresi bisa lolos tanpa terdeteksi oleh unit test biasa. _(Kondisi sebelum 22 Oktober 2026 — sudah ditutup, lihat baris Status dan §4b-i.)_ |
| **Bukti di repo** | Test: [`snapshot/WidgetSnapshotTest.kt`](../android/app/src/test/java/io/github/areifzyp/dot/snapshot/WidgetSnapshotTest.kt) + [`snapshot/WidgetSnapshotHarness.kt`](../android/app/src/test/java/io/github/areifzyp/dot/snapshot/WidgetSnapshotHarness.kt). Golden: `android/app/src/test/snapshots/` (16 PNG). Seam waktu: [`widget/WidgetClock.kt`](../android/app/src/main/java/io/github/areifzyp/dot/widget/WidgetClock.kt). Gate CI: [`ci.yml`](../.github/workflows/ci.yml) langkah `Verify widget snapshots (blocking gate)`. Konfigurasi: `android/build.gradle.kts` + `android/app/build.gradle.kts` (plugin & `testImplementation` Roborazzi 1.60.0). |
| **Pemilik** | **frontend/dev Android** (gate CI), ditinjau arsitek untuk pemilihan alat snapshot. |
| **Kriteria penerimaan** | Gate snapshot per widget × tema (terang/gelap), dijalankan di CI, dan kegagalan gate memblokir merge. **Wajib mencakup state `Delayed` widget Jam** (UX-2/UX-3 §4a) — kalau tidak, satu-satunya state baru yang ditambahkan untuk risiko §4a lolos tanpa gate visual. |
| **Status** | **AC-1 terpenuhi. AC-2 dipecah: (a) terpenuhi, (b) TERBUKA.** (frontend, 22 Oktober 2026; baris ini dipecah 23 Oktober 2026 setelah tinjauan.) — **(a)** langkah gate menggagalkan job `test`: terpenuhi & terverifikasi. **(b)** job itu menjadi *required status check* di branch protection `main` sehingga kegagalan gate menahan tombol merge: **belum terpasang** — `main` tidak terlindungi (bukti `404 "Branch not protected"` di §4b-i). Karena (b) belum beres, **AC-2 belum boleh disebut terpenuhi**; AC-1 dan AC-2a yang tertutup. Rincian bukti di §4b-i. |

#### b-i) Status implementasi (frontend, 22 Oktober 2026)

**Alat: Roborazzi 1.60.0**, disetujui arsitek 22 Oktober 2026 dengan tiga syarat yang ketiganya terpenuhi: golden di-commit ke repo, `verifyRoborazziDebug` menjadi gate yang memblokir merge, dan mode rekam hanya manual (`./gradlew recordRoborazziDebug`) — tidak pernah dijalankan CI.

Kenapa bukan Paparazzi: Paparazzi berbasis layoutlib dan tidak mendukung `RemoteViews`, sedangkan proyek ini hybrid. Kenapa 1.60.0 dan bukan versi terbaru: artefak 1.61.0 ke atas dikompilasi dengan metadata Kotlin 2.3.0, sedangkan proyek ini Kotlin 2.0.20 — build gagal dengan *metadata version mismatch*. 1.60.0 adalah versi terakhir dengan metadata Kotlin 2.0.

**Matriks gate — 16 snapshot** (`WidgetSnapshotTest.widgetThemeMatrix`):

| Widget | Jalur render | Snapshot |
|---|---|---|
| Jam | Glance | `clock_{light,dark}_{ontime,delayed}` = 4 |
| Kalender | Glance | `calendar_{light,dark}` = 2 |
| Cuaca | Glance | `weather_{light,dark}` = 2 |
| Status HP | Glance | `device_{light,dark}` = 2 |
| Musik | Glance | `music_{light,dark}` = 2 |
| Dekoratif | Glance | `decorative_{light,dark}` = 2 |
| Teks | **RemoteViews** | `text_{light,dark}` = 2 |

Jumlahnya 16, bukan 14 seperti pada catatan arsitek: hitungan "5 widget lain" hanya mencakup widget Glance. Widget Teks adalah **satu-satunya** yang memakai `RemoteViews` — jalur yang justru paling mudah diregresikan karena tidak melewati Glance sama sekali. Menjatuhkannya berarti jalur RemoteViews tidak punya gate visual, jadi ia dimasukkan.

**Cara render.** Bukan layoutlib, dan bukan `provideComposable` dari `glance-appwidget-testing` (API itu hanya memberi pohon semantik, bukan piksel). Setiap widget dirender lewat `AppWidgetManager` sungguhan: Glance menjalankan sesi komposisi lewat `GlanceAppWidget.update()` (WorkManager diinisialisasi dengan eksekutor sinkron supaya sesi berjalan di dalam test), RemoteViews lewat `ShadowAppWidgetManager`. Hasilnya `RemoteViews` → `View` → bitmap → Roborazzi, sehingga yang dibandingkan memang piksel hasil render — termasuk bitmap `DotRender` di dalamnya.

**Determinisme** — tanpa ini gate akan merah terus-menerus dan orang akan mematikannya:

- Waktu dipakukan lewat seam [`WidgetClock`](../android/app/src/main/java/io/github/areifzyp/dot/widget/WidgetClock.kt) (`Clock.fixed(2026-10-22 09:41 Asia/Jakarta)`). `System.currentTimeMillis()` **tidak** di-shadow Robolectric, jadi sebelum seam ini golden jam berubah tiap menit dan golden kalender tiap hari. Seam ini juga yang dipakai label tanggal jam dan tanggal kalender; defaultnya tetap `Clock.systemDefaultZone()` sehingga perilaku di perangkat tidak berubah.
- `StatFs` (widget Status HP) dipatok lewat `ShadowStatFs`; tanpa itu angka penyimpanan mengikuti disk mesin runner.
- Ukuran widget tetap 250×120 dp, `@Config(sdk = [33], qualifiers = "w320dp-h320dp-xhdpi")`.
- Seluruh matriks dijalankan dalam **satu metode test**. `SessionManager` Glance adalah singleton per proses dan sesi widget tidak pernah selesai selama widget ada; kalau tiap sel dijadikan metode test sendiri, sel kedua menunggu sesi sel pertama tanpa batas (timeout tanpa pesan error). Kegagalan tetap dikumpulkan per sel agar pesannya menyebut snapshot mana yang berubah.
- Diverifikasi: dua kali rekam berturut-turut menghasilkan 16/16 PNG identik byte-per-byte.

**UX-3 terbukti oleh gate, bukan komentar.** `clock_{light,dark}_ontime` vs `clock_{light,dark}_delayed` berbeda mulai baris 169 dari 240 (area slot penanda); seluruh piksel di atasnya identik. Artinya kotak digit jam tidak bergeser saat penanda muncul — persis yang diminta UX-3 §4a.

**Gate benar-benar memblokir job CI (diuji, bukan diasumsikan).** Menaikkan `ClockLayout.PAD_DP` 14 → 18 dp membuat `verifyRoborazziDebug` gagal tepat pada 4 sel Jam dengan pesan `… is changed` plus berkas `*_compare.png`; setelah nilai dikembalikan, gate hijau lagi. Tinjauan independen mengulanginya dengan merusak piksel `clock_light_ontime.png`: `62 tests completed, 1 failed`, `GRADLE_EXIT=1`.

#### b-ii) AC-2 dipecah: langkah CI (tertutup) vs. branch protection (terbuka)

AC-2 berbunyi "dijalankan di CI **dan** kegagalan gate memblokir merge". Dua bagian itu punya pemilik berbeda dan tidak boleh ditulis sebagai satu status, karena bagian pertama bisa dibuktikan dari repo sementara bagian kedua tidak.

**(a) Langkah gate menggagalkan job `test` — TERPENUHI, terverifikasi.** `ci.yml` menjalankan `:app:verifyRoborazziDebug` **tanpa** `continue-on-error`, sehingga snapshot yang berubah menggagalkan job `test`. Dibuktikan dua kali dengan mutasi (mutasi layout dan golden rusak, keduanya menghasilkan `GRADLE_EXIT=1`).

**(b) Required status check di `main` — TERBUKA.** Tanpa required check, GitHub tetap menampilkan tombol merge saat check merah, jadi gate hanya *best-effort*. Bukti lapangan, 23 Oktober 2026 (token dengan `permissions.admin=true`):

```
GET /repos/areifzyp/dot/branches/main/protection
→ HTTP 404  {"message": "Branch not protected", "status": "404"}

GET /repos/areifzyp/dot/rulesets
→ HTTP 200  []        # tidak ada ruleset yang menggantikan branch protection
```

`main` **tidak terlindungi oleh jalur mana pun** — baik *classic branch protection* maupun *repository ruleset*. Karena itu, per 23 Oktober 2026, **kegagalan gate belum memblokir merge**, dan AC-2 belum terpenuhi; yang tertutup adalah AC-1 dan AC-2a.

**Nama check yang harus dipilih: `Build & unit test`.** Itu `jobs.test.name` di `ci.yml` — bukan id job `test`, bukan nama berkas workflow. Salah pilih nama berarti tidak ada check dengan nama itu yang pernah melaporkan status, sehingga gate tetap tidak menahan merge.

**Urutan pemasangan penting.** Required check dipasang **setelah** `ci.yml` yang memuat job itu ada di `main`. Kalau dipasang lebih dulu, PR dari cabang tanpa workflow tersebut tidak akan pernah memunculkan check `Build & unit test`, PR menggantung, dan proteksinya akan dimatikan orang. Selain itu, mengaktifkan proteksi `main` mengubah alur merge **semua** kontributor (push langsung ke `main` menjadi lewat PR) — itu keputusan pemilik repo, bukan keputusan worker.

**Tindak lanjut:** kartu `t_4c253582` (pemilik: pm, eksekusi setelan oleh admin repo/operator). AC-2b baru boleh ditandai terpenuhi setelah kartu itu selesai dan respons `200` berisi `required_status_checks.contexts` memuat `Build & unit test` ditempelkan sebagai bukti.

**Risiko yang tercatat, bukan alasan menahan:** gate belum pernah berjalan di runner GitHub sungguhan (`feat/snapshot-gate` masih lokal; kontrak kartu `t_d1afc45d` = local-only). Determinisme baru terbukti di mesin pengembang. Kalau run pertama di `ubuntu-latest` merah karena rendering Robolectric/Skia/font berbeda, rekam ulang golden di container CI — jangan menambah `continue-on-error` atau mematikan gate.


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
