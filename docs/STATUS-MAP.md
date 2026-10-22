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
2. Dekoratif di Android bersifat statis tanpa sumber data (PRD §6.12: "statis, tanpa pembaruan berkala"). Di Plasma nilai tambahnya paling kecil dibanding enam widget lain yang punya data hidup — dan Plasma sudah punya kebebasan menempatkan gambar statis.
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
| **Risiko residual** | `font.size`, `shape.radius`, dan `border` sudah kanonik di token tetapi belum punya konsumen mesin: 6 plasmoid QML masih dark-only hard `pixelSize`/warna (lihat §2). Drift sisi itu tidak tertangkap gate karena belum ada kode yang membaca token — baru ditutup saat lane refaktor QML mengonsumsi `tokens.json`. |
| **Pemilik** | Arsip increment: **arsitek**. Sisa kontrak QML: **frontend** (kartu lane terpisah). |
| **Kriteria penerimaan** | (1) Satu generator kanonik `tokens.json` → `Color.kt` — terpenuhi. (2) Generator dijalankan di CI sehingga drift gagal build — terpenuhi (step *Token drift gate*, pull_request trigger, merge-blocking). (3) Readme menyebut generator — terpenuhi di `design/README.md` (baris di `docs/README.md` yang masih menulis "generator belum ada" menunggu commit WIP pm yang sedang jalan; saat itu mendarat, baris itu wajib diperbarui). Rujukan mati `design/tokens.md` sudah dihapus di commit pembekuan — tidak ditagih ulang. |

### d) Lisensi — tiga klaim bertentangan di satu repo, menunggu keputusan hukum/kepatuhan

| | |
|---|---|
| **Status** | **waiting legal review** (terdaftar di sini 22 Oktober 2026 oleh arsitek; pelacak kanonik: kartu `t_6cc84cef` + cron mingguan Senin 09:00 WIB, lihat [`LICENSE-DECISION-RECORD.md`](LICENSE-DECISION-RECORD.md) §5). |
| **Risiko** | Repo menyatakan tiga lisensi berbeda untuk kode yang sama: PRD §10 `GPL-2.0-or-later`, `NOTICE` `Apache-2.0` (inilah yang dilihat pengguna lewat tombol "Lisensi"), dan `metadata.json` keenam plasmoid Plasma `MIT`. Tidak ada berkas `LICENSE` di akar repo. Memilih salah satunya adalah keputusan pemilik proyek atas saran hukum — bukan pekerjaan dev, oleh karena itu terdaftar sebagai risiko dengan status `waiting legal review`. |
| **Bukti di repo** | Paket permintaan keputusan: [`LICENSE-DECISION-REQUEST.md`](LICENSE-DECISION-REQUEST.md) (fakta terverifikasi §2, inventaris dependensi §3, opsi + konsekuensi teknis §5, pertanyaan Q1–Q11 §6, formulir jawaban §0). Catatan status hidup: [`LICENSE-DECISION-RECORD.md`](LICENSE-DECISION-RECORD.md) (BLOCKED; formulir keputusan §3 sengaja kosong sampai ada keputusan tertulis; cek konsistensi [`scripts/license-status.sh`](../scripts/license-status.sh)). Inventaris mentah: [`license-inventory/`](license-inventory/README.md) — 103 artefak runtime (101 Apache-2.0, 1 BSD-3-Clause, 1 tanpa pernyataan di POM), 141 artefak tes. |
| **Pemilik** | **Keputusan D1–D3: pemilik proyek + tim hukum/kepatuhan.** Pelaksanaan checklist §7 `LICENSE-DECISION-REQUEST.md` setelah keputusan: arsitek + doc-writer. |
| **Kriteria penerimaan** | (1) Formulir jawaban (REQUEST §0 / RECORD §3) terisi tertulis — D1–D3 minimal; "belum diputuskan" adalah jawaban sah. (2) Satu sumber kebenaran lisensi ditunjuk dan `LICENSE`/`NOTICE`/PRD §10/`plasma/*/metadata.json` diselaraskan. (3) `scripts/license-status.sh` keluar senyap (exit 0). |

> **Yang ditahan sampai keputusan** (REQUEST §4 + keputusan PM 22 Okt 2026, S1–S3):
>
> - **S1 — merge ke `main` yang menyentuh klaim lisensi ditahan:** `LICENSE`, `NOTICE`, `SettingsScreen.kt:140`, label lisensi di `strings.xml`, field `License` di `plasma/*/metadata.json`, PRD §10/§16.4, metadata F-Droid/fastlane.
> - **S2 — rilis ditahan:** tag, perubahan `VERSION` (kini `0.5.0`), GitHub Release, pembagian APK, pengajuan F-Droid/Play Store, publikasi plasmoid ke KDE Store, penulisan ulang teks lisensi di Tentang Aplikasi (SET-2), pembuatan `LICENSE`.
> - **S3 — CI `main` tidak boleh mendistribusikan APK:** step `Upload debug APK` (`actions/upload-artifact@v4`) sudah dicabut dari `.github/workflows/ci.yml` — di repo publik langkah itu mendistribusikan APK pada setiap push ke `main` *dan* setiap PR. `assembleDebug` tetap jalan sebagai bukti build.
>
> Pengembangan kode, tes, CI, dan dokumentasi **tidak** ditahan.
>
> Tabel eksposur publik (apa yang sudah terlihat) ada di `LICENSE-DECISION-REQUEST.md` §2.2. **Sisa eksposur belum dicabut:** tepat satu artefak `dot-debug` (id `12437095872`, 11.087.580 byte, kedaluwarsa 20 Jan 2027) masih dapat diunduh oleh akun GitHub yang login (bukan anonim, tidak seperti raw file) — penghapusannya menunggu keputusan operator (kanban `t_40fc5231`). Perlu dicatat: **Q12** (penanda "lisensi belum final" di README/NOTICE) belum dijawab — sampai dijawab, S1 menahan juga perubahan `NOTICE`, jadi penanda itu jangan ditulis dulu.
>
> **Repo sudah publik sejak 18 Oktober 2026** (`api.github.com/repos/areifzyp/dot` → `private:false`; `NOTICE` → 200, `LICENSE` → 404 — klaim Apache-2.0 sudah terlihat publik tanpa berkas `LICENSE`). Karena eksposur tidak bisa ditarik kembali dengan menahan merge, dan risiko bersumber dari klaim (bukan dari kode fitur), blokir semua merge ke `main` tidak menahan risiko sebenarnya.
>
> **Keputusan PM (22 Okt 2026) — amandemen butir #4 rapat:** penahanan "tidak merge ke main" dipersempit menjadi penahanan aktivitas rilis/distribusi + merge yang menyentuh klaim lisensi (REQUEST §4, S1–S3). Alasan: repo sudah publik sejak 18 Okt 2026 (`private:false`, `NOTICE` 200, `LICENSE` 404), sehingga blokir merge tidak menahan eksposur; risiko bersumber dari klaim, bukan dari kode fitur. Wewenang membalik: operator.

---

## 5. Konvensi

**Repo menang atas dokumen.** Bila dokumen dan kode berbeda, dokumen yang salah — perbarui dokumen, jangan mengubah kode agar cocok dengan dokumen. Konsekuensi praktis:

- Setiap klaim status di dokumen ini harus punya path bukti. Klaim tanpa path dihapus atau ditandai **belum diverifikasi**.
- Bila sebuah berkas dipindahkan atau dihapus, perbarui peta ini pada commit yang sama.
- Jangan menautkan berkas yang belum ada di repo, termasuk berkas yang "sedang direncanakan".
- [`Color.kt`](../android/app/src/main/java/io/github/areifzyp/dot/ui/theme/Color.kt) adalah **berkas hasil generator** (`design/generate_color_kt.py`, SSOT `design/tokens.json`). Edit token lalu regenerasi — jangan edit tangan; CI menolak drift.
