# Peta Status Dot. — Android terverifikasi di `e1c7f9c`, Plasma diuji 23 Oktober 2026

**Tanggal verifikasi:** Android 22 Oktober 2026 · **Commit Android:** `e1c7f9c` (`fix(android): settings screen correctness and real backup config`) · **Plasma:** 23 Oktober 2026, salinan terpasang = `53c449b` · **VERSION:** `0.5.0`

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

## 2. Plasma (pasca-Fase 4) — terpasang di Plasma 6 hidup; 6/6 muat, 1 kotak kosong

`plasma/` berisi 6 plasmoid dan satu skrip pemasangan. Setiap plasmoid punya `metadata.json` dengan `KPlugin.Id` yang sesuai nama foldernya.

**Terpasang & diuji 23 Oktober 2026** di sesi Plasma 6 hidup (detail metode dan hasil per butir PRD: §3).

| Plasmoid | `KPlugin.Name` | Bukti | Status |
|---|---|---|---|
| `org.dot.clock` | Dot Clock | [`metadata.json`](../plasma/org.dot.clock/metadata.json) | Terpasang · muat bersih · **menyimpang**: kotak hitam kosong, jam tidak tampil (§3) |
| `org.dot.calendar` | Dot Calendar | [`metadata.json`](../plasma/org.dot.calendar/metadata.json) | Terpasang · muat bersih · sebagian: hanya grid bulan, tanpa agenda (§3) |
| `org.dot.weather` | Dot Weather | [`metadata.json`](../plasma/org.dot.weather/metadata.json) | Terpasang · muat bersih · menyimpang: tanpa ikon cuaca, kota hardcode, tanpa perkiraan jam (§3) |
| `org.dot.system` | Dot System Status | [`metadata.json`](../plasma/org.dot.system/metadata.json) | Terpasang · muat bersih · **menyimpang**: CPU/RAM `Math.random()`, DISK tetap 58% (§3) |
| `org.dot.music` | Dot Music | [`metadata.json`](../plasma/org.dot.music/metadata.json) | Terpasang · muat bersih · placeholder: tanpa MPRIS, glyph tidak bisa diklik (§3) |
| `org.dot.text` | Dot Text Note | [`metadata.json`](../plasma/org.dot.text/metadata.json) | Terpasang · muat bersih · sebagian: Doto terpakai, isi & warna hardcode (§3) |

Pemasangan: [`plasma/install-all.sh`](../plasma/install-all.sh) menyalin keenam folder ke `$HOME/.local/share/plasma/plasmoids`. **Terverifikasi 22 Okt 2026 (PM):** skrip berhenti pada galat (`set -e`), dan baris `echo "Refreshing KPackage index..."` **tidak diikuti perintah apa pun** — murni teks, index tidak di-refresh.

**Terverifikasi 23 Okt 2026 (penguji): refresh manual tidak diperlukan.** `kpackagetool6 -t Plasma/Applet --list` mengenali keenam `org.dot.*` tanpa `kbuildsycoca6` maupun restart plasmashell. Baris itu tetap menyesatkan (menyatakan sesuatu yang tidak dikerjakan), tetapi ketiadaannya tidak menghalangi registrasi paket.

Dua hazard lain di skrip yang sama, terverifikasi: `rm -rf "$DEST_DIR/$widget"` dijalankan **sebelum** `cp` — bila `cp` gagal, plasmoid lama sudah terhapus dan tidak ada rollback. Dan `set -e` tidak menolong bila `cp` berhasil sebagian.

Status "belum diuji pasang" sudah **tidak berlaku** sejak 23 Oktober 2026; hasilnya ada di §3. Yang masih terbuka bukan lagi uji pasang, melainkan selisih antara tampilan dan PRD (enam butir P0 gagal) plus satu perbaikan yang belum masuk `main` (lihat §3).

### Dekoratif di Plasma — di luar cakupan v1.0 (keputusan PM, 22 Oktober 2026)

Tidak ada `plasma/org.dot.decorative`. §12 PRD (dibekukan) menyebut urutan Plasma sebagai *"Jam, Cuaca, Kalender, Status Sistem, Musik, lalu gaya yang bisa diatur"* — Dekoratif tidak tercantum.

**Keputusan:** Dekoratif **tidak** termasuk cakupan Plasma v1.0.

**Alasan:**

1. Mengikuti §12 yang sudah dibekukan. Memasukkan Dekoratif berarti memperluas PRD historis secara eksplisit, bukan menyelesaikan pekerjaan yang tertinggal.
2. Dekoratif di Android bersifat statis tanpa sumber data (PRD §9.1 "Ukuran dan Pembaruan Widget", baris Dekoratif: *"statis, tanpa pembaruan berkala"*; §6.12: *"tidak memicu pembaruan latar belakang"*). Di Plasma nilai tambahnya paling kecil dibanding enam widget lain yang punya data hidup — dan Plasma sudah punya kebebasan menempatkan gambar statis.
3. v1.0 tinggal satu item (§3, uji instal). Menambah plasmoid ketujuh menambah satu siklus desain + uji pasang sebelum rilis, tanpa mengubah nilai rilis.

**Konsekuensi:** Dekoratif Plasma dicatat sebagai **kandidat pasca-v1.0**. Memasukkannya butuh keputusan operator (Arif) untuk memperluas §12; sampai itu ada, jangan dihitung sebagai pekerjaan yang tertinggal.

---

## 3. Uji instal 6 plasmoid di Plasma 6 — selesai 23 Oktober 2026

Dijalankan di sesi Plasma 6 hidup Arif (Fedora 44 KDE, Plasma 6.11.2, Wayland, 1920×1080) setelah persetujuan operator pada 23 Oktober 2026. Metode: [`plasma/install-all.sh`](../plasma/install-all.sh) menyalin keenam folder ke `$HOME/.local/share/plasma/plasmoids` (salinan terpasang diverifikasi byte-identik dengan `53c449b` lewat `git archive` + `diff -r`); tiap plasmoid ditampilkan lewat `plasmawindowed` lalu difoto layar penuh dengan `spectacle`.

Jalur tangkap divalidasi lebih dulu dengan kontrol positif `org.kde.plasma.digitalclock`, yang tampil benar ("8:12 AM" + "10/23/26") — jadi kotak kosong pada Clock di bawah bukan artefak tangkapan layar. Galat muat `Cannot assign to non-existent property "preferredHeight"` (temuan uji 22 Okt) sudah tidak muncul di keenam plasmoid.

**Bukti tangkapan** — disimpan di luar repo, workspace kartu `t_8122bcbf` pada mesin ini: `evidence/_stock-dot-<plasmoid>.png` (layar penuh 1920×1080, 08:14:41–08:15:16, dibuat `pw-stock.sh`; log `logs3/dot-<plasmoid>.{out,err}`), kontrol positif `evidence/_stock-stock-clock.png` (08:12:38), dan potongan 1100×280 `evidence/crop-<plasmoid>.png` (08:16). Jalankan Clock tercatat di journal sebagai `plasmawindowed[982002]` pukul 08:15:10–08:15:15, cocok dengan stempel waktu `_stock-dot-clock.png` (08:15:16).

**Refresh manual tidak diperlukan** — `kpackagetool6 -t Plasma/Applet --list` mengenali keenam `org.dot.*` tanpa `kbuildsycoca6`/restart plasmashell (§2).

| Plasmoid | (1) Terpasang | (2) Galat QML/KPackage | (3) Tampilan vs desain |
|---|---|---|---|
| `org.dot.clock` | ya | muat tanpa galat, tetapi **`ReferenceError: timeLabel is not defined`** terlempar tiap detik dari `Timer.onTriggered` ([main.qml:23–26](../plasma/org.dot.clock/contents/ui/main.qml)). Terbukti dari journal, bukan hanya bacaan sumber: `plasmawindowed` mencetaknya sekali per detik pada `…/plasmoids/org.dot.clock/contents/ui/main.qml:24`, di tiga jalankan terpisah (08:05, 08:07, 08:15). | **menyimpang** — kotak hitam kosong: tanpa jam, tanpa tanggal. `timeLabel`/`dateLabel` ([main.qml:45](../plasma/org.dot.clock/contents/ui/main.qml), [54](../plasma/org.dot.clock/contents/ui/main.qml)) dideklarasikan di dalam `fullRepresentation` (sebuah `Component`), sedangkan `Timer` di akar menulis ke sana dari luar komponen — id tidak terjangkau, label tidak pernah terisi. JAM-1/JAM-2 (P0) tidak terpenuhi. Representasi ringkas ([main.qml:67](../plasma/org.dot.clock/contents/ui/main.qml)) menghitung waktu sekali saat dibuat tanpa timer, jadi tidak berdetak. |
| `org.dot.weather` | ya | tidak ada | **menyimpang** pada tiga butir P0: **CUA-2** (suhu + kondisi + ikon dot-matrix) separuh — `JAKARTA` / `30°C` / `Clear sky` / `Updated 08:14` tampil dan angkanya dirender Doto (dot-matrix), tetapi **tidak ada ikon cuaca** (matahari/awan/hujan) di layar maupun di sumber; **CUA-1** (kota dipilih manual) tidak ada — `cityName`/`latitude`/`longitude` hardcode Jakarta ([main.qml:13–15](../plasma/org.dot.weather/contents/ui/main.qml)), tanpa `plasmoid.configuration`; **CUA-3** (dua ukuran, ukuran sedang menambah perkiraan jam ke depan) tidak ada — `compactRepresentation` hanya suhu ([main.qml:126–138](../plasma/org.dot.weather/contents/ui/main.qml)) dan `fullRepresentation` berhenti di kondisi saat ini ([main.qml:75–124](../plasma/org.dot.weather/contents/ui/main.qml)), tanpa prakiraan. Terpenuhi: CUA-6 (Open-Meteo tanpa API key, [main.qml:38](../plasma/org.dot.weather/contents/ui/main.qml)), CUA-4 (jam pembaruan terakhir, [main.qml:48–49](../plasma/org.dot.weather/contents/ui/main.qml)). Tidak terpenuhi: CUA-5 (°C/°F — tidak ada pilihan satuan). |
| `org.dot.calendar` | ya | tidak ada | **sebagian** — KAL-1 terpenuhi: `OCTOBER 2026`, minggu mulai Senin, tanggal 23 disorot merah, memakai tanggal sistem sungguhan ([main.qml:13](../plasma/org.dot.calendar/contents/ui/main.qml), [40](../plasma/org.dot.calendar/contents/ui/main.qml)). **KAL-2** (agenda, P0) dan **KAL-3** (dua mode tampilan, P0) tidak ada — hanya grid bulan, dan sumbernya sendiri menyebut "simplified current month view" ([main.qml:67](../plasma/org.dot.calendar/contents/ui/main.qml)). `currentDate` dievaluasi sekali saat muat tanpa timer, jadi sorotan "hari ini" basi setelah tengah malam sampai plasmashell dimuat ulang. |
| `org.dot.system` | ya | tidak ada | **menyimpang dari kriteria penerimaan** — `CPU`/`RAM` adalah `Math.random()`, `DISK` tetap `58%` ([main.qml:28–30](../plasma/org.dot.system/contents/ui/main.qml)); tidak ada pembacaan sensor. §6.5 PRD melarang ini secara eksplisit: *"tidak menampilkan angka palsu"* (klausa sebelumnya menyebut sistem Android dan tidak berlaku di Plasma; larangan angka palsu tetap jadi prinsipnya). **HP-3** (RAM/storage, P0) tampil tetapi palsu; **HP-2** (suhu, P0) tidak ada; **HP-4** (visualisasi dot-matrix, P1) tidak ada — hanya tiga baris teks. |
| `org.dot.music` | ya | tidak ada | **placeholder** — MUS-4 (empty state, P0) terpenuhi: `NO MEDIA PLAYING` + `Nothing OS`. **MUS-1/MUS-2** (P0) tidak: tidak ada MPRIS maupun kendali pemutar — ketiga glyph yang terlihat `⏮ ▶ ⏭` adalah `Text` biasa ([main.qml:58,63,68](../plasma/org.dot.music/contents/ui/main.qml); baris 63 hanya mencetak `⏸` bila `isPlaying`), dan `isPlaying` ([main.qml:15](../plasma/org.dot.music/contents/ui/main.qml)) tidak pernah diubah oleh apa pun. Kriteria "mengendalikan pemutar yang sedang aktif dan memperbarui info lagu" gagal. MUS-3 (P1) tidak ada. |
| `org.dot.text` | ya | tidak ada | **sebagian** — TXT-2 terpenuhi separuh: teks memakai Doto lewat `FontLoader` dengan fallback `Monospace` ([main.qml:39](../plasma/org.dot.text/contents/ui/main.qml)). Menyimpang: warna hardcode `#FFFFFF` (bukan tema/aksen, [main.qml:41](../plasma/org.dot.text/contents/ui/main.qml)) dan isi hardcode `"Stay authentic. Nothing UI."` ([main.qml:13](../plasma/org.dot.text/contents/ui/main.qml)). TXT-1/TXT-3/TXT-4 bergantung pada aplikasi Android + halaman konfigurasi yang belum ada; TXT-5 (fallback non-Doto) tidak ditangani; TXT-6/TXT-7/TXT-8 (P1/P2) tidak ada. |

### Temuan lintas keenam plasmoid

| Temuan | Bukti | Dampak |
|---|---|---|
| **Tidak ada halaman konfigurasi** | tidak ada `contents/config/` di paket mana pun; `plasmoid.configuration` tidak muncul sekali pun di keenam `main.qml` | §12 PRD menetapkan pengaturan widget lewat halaman konfigurasi bawaan Plasma. Tanpa ini tidak satu pun butir "pilih kota / pilih kalender / pilih pemutar / ubah teks / ukuran font" bisa dipenuhi — jadi bukan hanya CUA-1 yang tertahan. |
| **Tidak ada interaksi sama sekali** | `MouseArea`/`TapHandler` = 0 di keenam berkas | Semua widget hanya baca-tampil. MUS-1, KAL-5, dan setiap interaksi di PRD §6 tidak mungkin. |
| **Palet hardcode, tidak mengikuti tema** | `Kirigami.Theme` = 0; 3–9 literal `#RRGGBB` per berkas | Sejalan dengan risiko §4c: QML belum membaca `tokens.json`, dan widget tidak menyesuaikan tema terang/gelap Plasma. |
| **Belum terbukti muncul di "Add Widgets"** | uji memakai `plasmawindowed`, bukan panel | Menambah ke panel butuh restart plasmashell di desktop hidup — tidak dilakukan. Registrasi KPackage sudah terbukti, jadi risikonya rendah, tetapi **belum diverifikasi**. |

### Catatan repo: perbaikan `53c449b` belum ada di `main`

Perbaikan galat muat ada di commit `53c449b` (`fix(plasma): pakai Layout.preferredWidth/Height di enam plasmoid`, 6 berkas, branch `feat/snapshot-gate`). Uji di atas dijalankan pada salinan terpasang yang isinya **identik** dengan `53c449b` (diverifikasi `diff`).

`53c449b` **tidak** ada di `main`, `origin/main`, maupun cabang tempat dokumen ini ditulis: `sed -n '10,11p' plasma/org.dot.clock/contents/ui/main.qml` di cabang ini masih menampilkan `preferredWidth:` polos. Artinya isi repo di `main` masih versi yang gagal dimuat. **`53c449b` harus masuk `main` sebelum v1.0 ditandai.**

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
| **Status** | **Sumbu warna DITUTUP** (generator + CI gate, arsitek, 22 Oktober 2026). Sisa: kontrak sisi QML — lane refaktor terpisah. |
| **Risiko awal** | Token dideklarasikan sebagai single source of truth, tetapi tidak ada kode yang membacanya. Nilai ditranskripsi tangan ke Kotlin, sehingga `tokens.json` dan `Color.kt` bisa menyimpang tanpa ada yang mendeteksi. |
| **Pemutusan** | [`design/generate_color_kt.py`](../design/generate_color_kt.py) adalah generator kanonik `tokens.json` → [`Color.kt`](../android/app/src/main/java/io/github/areifzyp/dot/ui/theme/Color.kt) (header berkas menandai diri sebagai hasil generator). CI menjalankannya dengan `git diff --exit-code` pada step *Token drift gate* di [`.github/workflows/ci.yml`](../.github/workflows/ci.yml) — drift (termasuk token baru tanpa nama Kotlin) memblokir build. Nilai kanonik untuk pasangan QML yang menyimpang diputuskan dan ditulis di tabel [`design/README.md`](../design/README.md) (border `#333333`/`#D4D4D4` sebagai peran baru; `#888888` kalah dari `secondary_dark=#999999`; `radius: 4` kalah dari `widget=16` dengan peran baru `item=4`; skala `font.size` 56/42/22/16/14/12/10; `nothing_orange=#F97316` masuk token karena dipakai `Theme.kt`). |
| **Risiko residual** | `font.size`, `shape.radius`, dan `border` sudah kanonik di token tetapi belum punya konsumen mesin: 6 plasmoid QML masih dark-only hard `pixelSize`/warna — terverifikasi 23 Oktober 2026: `Kirigami.Theme` muncul 0 kali dan ada 3–9 literal `#RRGGBB` per berkas (lihat §3). Drift sisi itu tidak tertangkap gate karena belum ada kode yang membaca token — baru ditutup saat lane refaktor QML mengonsumsi `tokens.json`. |
| **Pemilik** | Arsip increment: **arsitek**. Sisa kontrak QML: **frontend** (kartu lane terpisah). |
| **Kriteria penerimaan** | (1) Satu generator kanonik `tokens.json` → `Color.kt` — terpenuhi. (2) Generator dijalankan di CI sehingga drift gagal build — terpenuhi (step *Token drift gate*, pull_request trigger, merge-blocking). (3) Readme menyebut generator — terpenuhi di `design/README.md` (baris di `docs/README.md` yang masih menulis "generator belum ada" menunggu commit WIP pm yang sedang jalan; saat itu mendarat, baris itu wajib diperbarui). Rujukan mati `design/tokens.md` sudah dihapus di commit pembekuan — tidak ditagih ulang. |

---

## 5. Konvensi

**Repo menang atas dokumen.** Bila dokumen dan kode berbeda, dokumen yang salah — perbarui dokumen, jangan mengubah kode agar cocok dengan dokumen. Konsekuensi praktis:

- Setiap klaim status di dokumen ini harus punya path bukti. Klaim tanpa path dihapus atau ditandai **belum diverifikasi**.
- Bila sebuah berkas dipindahkan atau dihapus, perbarui peta ini pada commit yang sama.
- Jangan menautkan berkas yang belum ada di repo, termasuk berkas yang "sedang direncanakan".
