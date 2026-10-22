# REVIEW — PRD-Dot.md: Fase-State & Spesifikasi Saat Ini

**Ditujukan:** Persetujuan rapat proyek Dot (ip:127.0.0.1)
**Dokumen review:** PRD-Dot.md (status: "Perencanaan" — 18 Oktober 2026)
**Faktor waktu:** 22 Oktober 2026
**Cakupan:** Status fase, peta status per widget, tiga risiko terbuka, dan keselarasan spesifikasi.

> Enam dasawarsa verifikasi dibuat dari sumber primer: seluruh PRD-Dot.md (613 baris), git log, arsitektur folder `android/` + `/plasma/`, CI, dan catatan TODO/PRD di `docs/`. Tiap claim diberi lihat: `PRD:L<baris>` atau `git:<sha>`.

> **Status dokumen: snapshot audit per commit `e1c7f9c` (22 Okt 2026) — bukan dokumen otoritatif.**
> Peta status terkini ada di [`STATUS-MAP.md`](STATUS-MAP.md). Bila keduanya berbeda,
> STATUS-MAP yang berlaku. Khusus Dekoratif di Plasma, keputusan cakupan ada di
> STATUS-MAP §2: **di luar cakupan Plasma v1.0**, bukan pekerjaan tertinggal.

---

## 1. Ringkasan eksekutif

PRD masih berstatus **"Perencanaan"** padahal proyek telah melewati Fase 1–4 dan mengerjakan konten Pasca-Fase 4. Tiga bagian yang saat ini **masih dianggap rencana** secara faktual sudah selesai atau dalam pengerjaan:

| PRD | Konten | Status faktual |
|---|---|---|
| §5 (Ruang Lingkup & Fase) | Fase 1–4 + Pasca-4 (Plasma) | **Dijadwalkan** |
| §11.1 (Rencana Pengerjaan Fase 1) | 9 tugas urutan AI | **Selesai** (Fase 1–4) |
| §17.1 (Definisi Selesai per Fase) | DoD Fase 1–4 | **Hampir semua terpenuhi** |

Usulan ini: **bekukan v1.0 sebagai historis**, ubah PRD menjadi **acuan keputusan** (decision reference), bukan peta progres. Peta progres pindah ke `Obsidian Vault/Hermes/projects/dot/` (`status.md`, `roadmap.md`) yang sudah mencerminkan kenyataan.

---

## 2. Faktanya (dibanding PRD)

### 2.1 Tujuh widget Android selesai
PRD §5 tidak menyebut jumlah tepatnya, PRD §6.1 GAL-5 menulis *"Semua tujuh widget sudah dapat dipasang"*, lalu §12 menganggap Plasma sebagai Pasca-Fase-4. Naik ke atas: di Android ada **7 tipe widget**:

| No | Widget | PRD | Implementasi | Commit |
|---|---|---|---|---|
| 1 | Jam | §6.2 (JAM-1..4) | `widget/ClockWidget.kt` (Glance) | `e6f2498` (v0.2.0) |
| 2 | Cuaca | §6.3 (CUA-1..6) | `widget/WeatherWidget.kt` (Glance) | `f308cc8` (v0.3.0) |
| 3 | Kalender | §6.4 (KAL-1..5) | `widget/CalendarWidget.kt` (Glance) | `f308cc8` (v0.3.0) |
| 4 | Teks | §6.11 (TXT-1..8) | `widget/TextWidgetProvider.kt` (**RemoteViews**) | `f308cc8` (v0.3.0) |
| 5 | Status HP | §6.5 (HP-1..4) | `system/DeviceStatusWidget.kt` (Glance) | `5989c56` (v0.4.0) |
| 6 | Musik | §6.6 (MUS-1..4) | `music/MusicWidget.kt` (Glance) | `5989c56` (v0.4.0) |
| 7 | Dekoratif | §6.12 (DEK-1..5) | `decorative/DecorativeWidget.kt` | `5989c56` (v0.4.0) |

**`/plasma/`** berisi **6 plasmoid QML** (jam, kalender, cuaca, sistem, musik, teks) — ditandai "post-phase 4 target" di commit `097467f`/`1cf5f7d`. Dekoratif tidak ada di `/plasma`; per keputusan PM itu **di luar cakupan Plasma v1.0**, bukan pekerjaan tertinggal — lihat `STATUS-MAP.md` §2.

### 2.2 Fase 4 / Pengaturan Aplikasi selesai
PRD §6.8 (SET-1..5) dipakai sebagai rencana, padahal **sudah rilis v0.5.0** (`e6f2498`: "Settings/About/Release — SET-1..5, update interval, backup, log copy, version, license") dan commit `5989c56` ("Phase 4: finalize widget wiring, lint-clean, Glance 1.1 API alignment").

### 2.3 `/plasma` sudah ada, seharusnya pasca-Fase 4
PRD §12 menganggap versi Plasma sebagai rekomendasi pasca-Fase-4. Faktanya folder `plasma/` **sudah berisi 6 widget** dan commit `1cf5f7d` menulis *"complete Plasma widget suite — music, text note, install script (all 6 widgets)"*. Jadi materi "Fase 4" + "Plasma" dalam PRD bukan paket yang akan dikerjakan; yang belum selesai hanyalah **uji instal/pertumbuhan Plasma** (sisa v1.0).

---

## 3. Peta status per widget

Faktor waktu: 22 Oktober 2026.

### Android (7 tipe widget)

| Widget | PRD-ID | Status | Bukti |
|---|---|---|---|
| Jam | JAM-1..4 | ✅ **Selesai** | `ClockWidget.kt`, commit `e6f2498` |
| Cuaca | CUA-1..6 | ✅ **Selesai** | `WeatherWidget.kt`, commit `f308cc8` |
| Kalender | KAL-1..5 | ✅ **Selesai** | `CalendarWidget.kt`, commit `f308cc8` |
| Teks | TXT-1..8 | ✅ **Selesai** | `TextWidgetProvider.kt`, commit `f308cc8` |
| Status HP | HP-1..4 | ✅ **Selesai** | `DeviceStatusWidget.kt`, commit `5989c56` |
| Musik | MUS-1..4 | ✅ **Selesai** | `MusicWidget.kt`, commit `5989c56` |
| Dekoratif | DEK-1..5 | ✅ **Selesai** (Android) | `decorative/`, commit `5989c56` |

### Plasma `/plasma` (6 plasmoid — konten Pasca-Fase 4)

| Widget | Status | Bukti |
|---|---|---|
| `org.dot.clock` | ✅ Sudah dibuat, **belum diuji pasang** | `097467f` |
| `org.dot.calendar` | ✅ Sudah dibuat, belum diuji | `789dade` |
| `org.dot.weather` | ✅ Sudah dibuat, belum diuji | `c6c598d` |
| `org.dot.system` | ✅ Sudah dibuat, belum diuji | `f2357ce` |
| `org.dot.music` | ✅ Sudah dibuat, belum diuji | `1cf5f7d` |
| `org.dot.text` | ✅ Sudah dibuat, belum diuji | `1cf5f7d` |
| **Dekoratif** | ⬜ Tidak ada di Plasma — **di luar cakupan v1.0** (keputusan PM, lihat `STATUS-MAP.md` §2) | — |

### Fase & status git terkini (verifikasi dari `roadmap.md` + git log)
- v0.1.0 bootstraps monorepo & pipeline (ci.yml, JDK 21, GitHub Actions) → v0.2.0 Fase 1 (commit `91e00e6`)
- v0.3.0 Fase 2 (Teks, Kalender, Cuaca) `f308cc8`
- v0.4.0 Fase 3 (Status HP, Musik, Dekoratif, Gaya Nothing) `5989c56`
- v0.5.0 **+ Fase 4** (Pengaturan, Tentang, Bantuan, Rilis) `e6f2498` (+ `15ae625`)
- v1.0.0 / in-progress: Plasma suite (6 widget dibuat, instal & uji pending)

---

## 4. Risiko terbuka — analisis & saran

### 4.1 Jam per menit: `USE_EXACT_ALARM` di Android 14+ dapat ditolak (PRD §7.1 meng-document mekanisme, **tidak ada kriteria UX fallback**)

**Saat ini (kode sumber):**
`ClockWidget.kt:199-210` — `scheduleNextUpdate` menggunakan `setExactAndAllowWhileIdle`, dan jatuh ke `am.set(...)` inexact bila `canScheduleExactAlarms()` salah / `SecurityException` dibuang. Jadi **mekanisme fallback ada**, tetapi **tidak ada aturan apa yang ditampilkan pengguna**.

**Bukti PRD:** §7.1 tabel wajib diupdate, baris 311 menulis *"bila dicabut, kode jatuh ke alarm tidak tepat waktu"* — hanya deskripsikan perilaku kode, **tidak ada kriteria layar**.

**Kesenjangan:**
1. Tidak ada kriteria: saat exact alarm ditolak/dicabut, aplikasi **diam** (tidak memberi tahu bahwa jam terlambat/menunggu pembaruan).
2. Tidak ada target batas tertulis yang konsisten dengan §9.1 (Status HP: *"Tiap 15 menit (batas minimum penjadwalan sistem)"*) yang bisa dijadikan minimum cadence untuk Jam.
3. Tidak ada test checklist untuk path `canScheduleExactAlarms() == false` / `SecurityException`.

**Saran:** tetapkan fallback eksplisit, non-diam:
- **Kriteria penolakan ditangkap** → widget tetap menampilkan waktu terakhir + bandingan waktu *"terlambat hingga ±N detik"*.
- **Tier B (tidak bisa exact):** turunkan cadence ke minimum sistem (≤15–30 menit) dan tampilkan *"pembaruan tertunda, akan muncul tepat di jam berikutnya"*.
- **Tidak pernah diam** — tetap tampil layar "data terakhir + waktunya" sesuai §6.10 (offline/empty).
- Tambah test: simulasi `canScheduleExactAlarms() == false` → assert jadwal jatuh ke inexact; simulasi `SecurityException` → assert makespan.

### 4.2 Hybrid Glance + RemoteViews = dua jalur render Doto — snapshot test per widget × tema **tidak ada** di CI

**Saat ini:**
- 6 widget Glance: Jam, Cuaca, Kalender, Status HP, Musik (`widget/*.kt`, `system/DeviceStatusWidget.kt`).
- 1 widget RemoteViews: **Teks** (`TextWidgetProvider.kt` → `R.layout.widget_text`, `fontFamily="@font/doto"`).

**Bukti PRD:** §8.3 catatan 6–7 mengakui belum simple, tetapi **tidak menyebut snapshot-per-widget×theme dalam CI**. PRD §6.10 (*"Semua widget memakai aturan yang sama"*) justru bertentangan dengan kenyataan hybrid ini.

**Bukti CI:** `ci.yml` hanya menjalankan `testDebugUnitTest` + `assembleDebug`. `DotRenderTest` adalah smoke pixel (clock-1234.png, label-date.png, decor-frame.png) — **bukan snapshot per-widget×theme, bukan golden compare, bukan matriks tema**.

**Saran:**
- Tambah Gradle variant `snapshotTestDebugUnitTest` yang membandingkan bitmap tiap widget × {light, dark} × render mode (Glance + RemoteViews) vs golden PNG tercommit.
- Jadwalkan di CI sebagai job terpisah, berjalan sebelum `assembleDebug` setiap push/PR & rilis.
- Jaga **satu generator** untuk golden validasi (jika ada skrip generate), jangan dua jalur render yang bisa melemahkan konsistensi gaya.

### 4.3 Token: README desain vs kenyataan — `tokens.md` + generate yang di-referensi **tidak ada di repo**

**Saat ini:**
- PRD §9 baris 420: *"design token dalam berkas netral (JSON) di folder /design"* → **JSON benar**.
- `design/tokens.json` ada (single source of truth, color/font/shape/spacing, `$schema`, backup note).
- `design/README.md` baris 5–7 mengakui `tokens.md`, `layout.md`, `icons/` — **ketiganya tidak ada** di `design/`.
- **Tidak ada script generate** di repo (cari `tokens.md|layout.md|*generate*` → 0 hasil).

**Saran:**
- Sinkronkan `design/README.md` dengan pipeline nyata: `tokens.json` sebagai single source, **tanpa tokens.md/layout.md/icons** yang dibaca tetapi tidak ada.
- Jika generator diinginkan, buat **satu** skrip kanonik, mis. `scripts/generate-tokens.sh`, yang menghasilkan `tokens.json` dari palette/font Doto (OFL). Dokumentasikan di README. Jangan biarkan dua jalur (README vs kode) saling bertentangan.

---

## 5. Hal yang harus diperbaiki dalam PRD (jika dipakai sebagai acuan keputusan)

1. **Ubah header status** dari `Status: Perencanaan` menjadi `Status: HISTORIS (v1.0)` + catatan: *"PRD ini menjadi acuan keputusan, bukan peta progres. Progres aktual ada di Obsidian status.md/roadmap.md."*
2. **§5** — bukan daftar fase yang akan dikerjakan, tapi ringkasan **hasil**: Fase 1–4 selesai, Plasma dalam pengerjaan.
3. **§11.1** — dihapus/masukkan ke historis (tugas Fase 1 sudah terlaksana).
4. **§17.1** — DoD yang sudah terpenuhi ditandai selesai; baris yang masih berlaku hanyalah **Pasca-Fase 4 (Plasma)**: *widget jam Plasma, uji instal, lanjut ke cuaca/kalender*.
5. **§7.1** — perbarui tabel, tambah **kriteria UX fallback** untuk exact alarm (bagian 4.1).
6. **§8.3 + §11.2** — tambah **snapshot test per widget × tema di CI** (bagian 4.2).
7. **§9.1** — tabulasi interval pembaruan (Jam per menit, Status HP 15 menit, dll.) — sudah ada, **tetap setia** pada Kriteria fallback di 4.1.

---

## 6. Tindak lanjut yang disarankan

1. **anak** dokumen: `docs/REVIEW-PRD-FASE-STATE.md` (ini) → dipakai saat rapt berikutnya.
2. Periksa `design/README.md` (pos 4.3) + sinkronkan dengan `tokens.json`.
3. MR: tambah snapshot-per-widget×theme → CI job baru (pos 4.2).
4. MR: `ClockUpdateScheduler` → badan fallback UX + test penolakan exact alarm (pos 4.1).
5. File README/PRD: header status diganti historis; prid tersisa sebagai acuan keputusan.

> TODO: nilai aksen `#D71921` (PRD §16.2) masih perkiraan — perlu verifikasi vs tangkapan layar Nothing OS sebelum ada pembukaan publik.
> TODO: belum ada test snapshot; kriteria detail (format golden PNG, tolerance) masih dibuka untuk disepakati.
