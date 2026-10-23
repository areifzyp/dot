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
2. Dekoratif di Android bersifat statis tanpa sumber data (PRD §6.12: "statis, tanpa pembaruan berkala"). Di Plasma nilai tambahnya paling kecil dibanding enam widget lain yang punya data hidup — dan Plasma sudah punya kebebasan menempatkan gambar statis.
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
| `org.dot.clock` | ya | muat tanpa galat, tetapi **`ReferenceError: timeLabel is not defined`** terlempar tiap detik dari `Timer.onTriggered` ([main.qml:23–26](../plasma/org.dot.clock/contents/ui/main.qml)). Terbukti dari journal, bukan hanya bacaan sumber: `plasmawindowed` mencetaknya sekali per detik pada `…/plasmoids/org.dot.clock/contents/ui/main.qml:24`, di dua jalankan terpisah (08:07 dan 08:15). | **menyimpang** — kotak hitam kosong: tanpa jam, tanpa tanggal. `timeLabel`/`dateLabel` ([main.qml:45](../plasma/org.dot.clock/contents/ui/main.qml), [54](../plasma/org.dot.clock/contents/ui/main.qml)) dideklarasikan di dalam `fullRepresentation` (sebuah `Component`), sedangkan `Timer` di akar menulis ke sana dari luar komponen — id tidak terjangkau, label tidak pernah terisi. JAM-1/JAM-2 (P0) tidak terpenuhi. Representasi ringkas ([main.qml:67](../plasma/org.dot.clock/contents/ui/main.qml)) menghitung waktu sekali saat dibuat tanpa timer, jadi tidak berdetak. |
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
| **Palet hardcode, tidak mengikuti tema** | `Kirigami.Theme` = 0; 3–7 literal `#RRGGBB` per berkas | Sejalan dengan risiko §4c: QML belum membaca `tokens.json`, dan widget tidak menyesuaikan tema terang/gelap Plasma. |
| **Belum terbukti muncul di "Add Widgets"** | uji memakai `plasmawindowed`, bukan panel | Menambah ke panel butuh restart plasmashell di desktop hidup — tidak dilakukan. Registrasi KPackage sudah terbukti, jadi risikonya rendah, tetapi **belum diverifikasi**. |

### Catatan repo: perbaikan `53c449b` belum ada di `main`

Perbaikan galat muat ada di commit `53c449b` (`fix(plasma): pakai Layout.preferredWidth/Height di enam plasmoid`, 6 berkas, branch `feat/snapshot-gate`). Uji di atas dijalankan pada salinan terpasang yang isinya **identik** dengan `53c449b` (diverifikasi `diff`).

`53c449b` **tidak** ada di `main`, `origin/main`, maupun cabang tempat dokumen ini ditulis: `sed -n '10,11p' plasma/org.dot.clock/contents/ui/main.qml` di cabang ini masih menampilkan `preferredWidth:` polos. Artinya isi repo di `main` masih versi yang gagal dimuat. **`53c449b` harus masuk `main` sebelum v1.0 ditandai.**

---

## 4. Risiko terbuka

### a) Exact alarm — fallback inexact tanpa kriteria UX dan tanpa test

| | |
|---|---|
| **Risiko** | Di Android 12+, izin exact alarm bisa dicabut. Widget tetap menjadwalkan alarm inexact, tetapi tidak ada definisi kapan pembaruan dianggap "terlambat" dan tidak ada test untuk cabang penolakan izin. |
| **Bukti di repo** | [`widget/ClockWidget.kt:199–211`](../android/app/src/main/java/io/github/areifzyp/dot/widget/ClockWidget.kt) — `canScheduleExactAlarms()` di baris 199, lalu `am.set(...)` sebagai fallback di baris 210. Test [`ClockUpdateSchedulerTest.kt`](../android/app/src/test/java/io/github/areifzyp/dot/widget/ClockUpdateSchedulerTest.kt) hanya menguji `millisToNextMinute` (2 test), tidak menyentuh cabang izin. |
| **Pemilik** | Kriteria UX: **PM (Sekar)** — ditulis di §4a ini, bukan di PRD (PRD dibekukan). Test cabang izin: **frontend/dev Android**. |
| **Kriteria penerimaan** | (1) Kriteria UX "tidak pernah tampak diam" — dirinci UX-1..6 di bawah. (2) Ada unit test yang mencakup cabang izin ditolak. |

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

### b) Snapshot test — tidak ada gate visual untuk hybrid 6 Glance + 1 RemoteViews

| | |
|---|---|
| **Risiko** | Tidak ada yang mencegah regresi tampilan. Enam widget Glance dan satu widget RemoteViews punya jalur render berbeda, jadi regresi bisa lolos tanpa terdeteksi oleh unit test biasa. |
| **Bukti di repo** | [`.github/workflows/ci.yml`](../.github/workflows/ci.yml) hanya menjalankan `:app:testDebugUnitTest` dan `:app:assembleDebug`. Tidak ada dependensi snapshot di `android/app/build.gradle.kts` (tidak ada Paparazzi/Roborazzi). |
| **Pemilik** | **frontend/dev Android** (gate CI), ditinjau arsitek untuk pemilihan alat snapshot. |
| **Kriteria penerimaan** | Gate snapshot per widget × tema (terang/gelap), dijalankan di CI, dan kegagalan gate memblokir merge. **Wajib mencakup state `Delayed` widget Jam** (UX-2/UX-3 §4a) — kalau tidak, satu-satunya state baru yang ditambahkan untuk risiko §4a lolos tanpa gate visual. |

### c) Token — sumbu warna ditutup oleh generator kanonik; font/radius menunggu lane QML

| | |
|---|---|
| **Status** | **Sumbu warna DITUTUP** (generator + CI gate, arsitek, 22 Oktober 2026). Sisa: kontrak sisi QML — lane refaktor terpisah. |
| **Risiko awal** | Token dideklarasikan sebagai single source of truth, tetapi tidak ada kode yang membacanya. Nilai ditranskripsi tangan ke Kotlin, sehingga `tokens.json` dan `Color.kt` bisa menyimpang tanpa ada yang mendeteksi. |
| **Pemutusan** | [`design/generate_color_kt.py`](../design/generate_color_kt.py) adalah generator kanonik `tokens.json` → [`Color.kt`](../android/app/src/main/java/io/github/areifzyp/dot/ui/theme/Color.kt) (header berkas menandai diri sebagai hasil generator). CI menjalankannya dengan `git diff --exit-code` pada step *Token drift gate* di [`.github/workflows/ci.yml`](../.github/workflows/ci.yml) — drift (termasuk token baru tanpa nama Kotlin) memblokir build. Nilai kanonik untuk pasangan QML yang menyimpang diputuskan dan ditulis di tabel [`design/README.md`](../design/README.md) (border `#333333`/`#D4D4D4` sebagai peran baru; `#888888` kalah dari `secondary_dark=#999999`; `radius: 4` kalah dari `widget=16` dengan peran baru `item=4`; skala `font.size` 56/42/22/16/14/12/10; `nothing_orange=#F97316` masuk token karena dipakai `Theme.kt`). |
| **Risiko residual** | `font.size`, `shape.radius`, dan `border` sudah kanonik di token tetapi belum punya konsumen mesin: 6 plasmoid QML masih dark-only hard `pixelSize`/warna — terverifikasi 23 Oktober 2026: `Kirigami.Theme` muncul 0 kali dan ada 3–7 literal `#RRGGBB` per berkas (lihat §3). Drift sisi itu tidak tertangkap gate karena belum ada kode yang membaca token — baru ditutup saat lane refaktor QML mengonsumsi `tokens.json`. |
| **Pemilik** | Arsip increment: **arsitek**. Sisa kontrak QML: **frontend** (kartu lane terpisah). |
| **Kriteria penerimaan** | (1) Satu generator kanonik `tokens.json` → `Color.kt` — terpenuhi. (2) Generator dijalankan di CI sehingga drift gagal build — terpenuhi (step *Token drift gate*, pull_request trigger, merge-blocking). (3) Readme menyebut generator — terpenuhi di `design/README.md` (baris di `docs/README.md` yang masih menulis "generator belum ada" menunggu commit WIP pm yang sedang jalan; saat itu mendarat, baris itu wajib diperbarui). Rujukan mati `design/tokens.md` sudah dihapus di commit pembekuan — tidak ditagih ulang. |

---

## 5. Konvensi

**Repo menang atas dokumen.** Bila dokumen dan kode berbeda, dokumen yang salah — perbarui dokumen, jangan mengubah kode agar cocok dengan dokumen. Konsekuensi praktis:

- Setiap klaim status di dokumen ini harus punya path bukti. Klaim tanpa path dihapus atau ditandai **belum diverifikasi**.
- Bila sebuah berkas dipindahkan atau dihapus, perbarui peta ini pada commit yang sama.
- Jangan menautkan berkas yang belum ada di repo, termasuk berkas yang "sedang direncanakan".
- [`Color.kt`](../android/app/src/main/java/io/github/areifzyp/dot/ui/theme/Color.kt) adalah **berkas hasil generator** (`design/generate_color_kt.py`, SSOT `design/tokens.json`). Edit token lalu regenerasi — jangan edit tangan; CI menolak drift.
