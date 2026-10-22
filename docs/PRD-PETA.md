# Peta Paragraf PRD & Kontrol Rujukan

Dokumen ini adalah **peta paragraf** `docs/PRD-Dot.md` (versi dokumen 1.0,{' '}
dibekukan pada commit `0ea4bac`) dan alat kontrolnya: sumber kebenaran versi,
audit cross-reference, tabel sifat → prerequisite, dan daftar bacaan wajib
per nomor referensi.

**Kewajiban pemakaian.** Setiap rujukan `PRD x.y` di dokumen non-PRD **harus**
cocok dengan judul paragraf pada §2 dokumen ini. Setiap sifat (trait) baru yang
diusulkan **wajib** punya baris prerequisite pada §5. Jika prerequisite-nya
belum ada di PRD, PRD (atau catatan keputusan) harus diperbarui lebih dulu —
tidak ada penambahan fitur yang lepas dari dokumen.

---

## 1. Status dokumen

| Bidang | Nilai |
|---|---|
| Versi aplikasi (SSOT, lihat §3) | `VERSION` di root repo — saat ini **0.5.0** |
| Versi dokumen PRD | 1.0 (HISTORIS, dibekukan; metadata tetap boleh diperbaiki) |
| Basis peta paragraf | `docs/PRD-Dot.md` pada commit `0ea4bac`, 624 baris |
| Diverifikasi | working tree bersih pada `main` (ahead 14 dari `origin/main`) |

---

## 2. Peta paragraf PRD-Dot.md

Konvensi: `x.y` = sub-bagian bernomor. `§11 butir 3` artinya butir ke-3 dari
daftar tak-bernomor di dalam §11 (PRD tidak memberi nomor pada butir daftar,
kecuali sub-bagian).

| § | Judul paragraf (persis seperti di PRD) | Baris |
|---|---|---|
| 1 | Ringkasan | 15 |
| 2 | Latar Belakang dan Masalah | 21 |
| 3 | Tujuan dan Non-Tujuan | 29 |
| 4 | Target Pengguna | 50 |
| 5 | Ruang Lingkup dan Hasil Rilis | 61 |
| 6 | Persyaratan Fungsional | 83 |
| 6.1 | Galeri Widget | 87 |
| 6.2 | Widget Jam | 104 |
| 6.3 | Widget Cuaca | 120 |
| 6.4 | Widget Kalender | 138 |
| 6.5 | Widget Status HP | 155 |
| 6.6 | Widget Musik | 171 |
| 6.7 | Gaya Nothing | 187 |
| 6.8 | Pengaturan Aplikasi | 206 |
| 6.9 | Konfigurasi, Onboarding, dan Aksi Ketuk | 218 |
| 6.10 | Keadaan Kosong dan Error | 245 |
| 6.11 | Widget Teks | 259 |
| 6.12 | Widget Dekoratif | 282 |
| 7 | Persyaratan Non-Fungsional | 300 |
| 7.1 | Izin Android per Fitur | 310 |
| 8 | Prinsip Desain dan UX | 324 |
| 8.1 | Font: Doto | 332 |
| 8.2 | Ikon dan Ilustrasi | 358 |
| 8.3 | Tema Terang dan Gelap | 366 |
| 8.4 | Peta Layar dan Navigasi | 384 |
| 8.5 | Palet Warna Dasar (wajib) | 402 |
| 9 | Pertimbangan Teknis (Usulan) | 423 |
| 9.1 | Ukuran dan Pembaruan Widget (usulan awal) | 434 |
| 10 | Repo, Lisensi, dan Distribusi | 448 |
| 11 | Pengembangan dengan AI | 459 |
| 11.1 | Rencana Pengerjaan Fase 1 (urutan tugas untuk AI) | 471 |
| 11.2 | Strategi Pengujian | 487 |
| 12 | Rencana Versi KDE Plasma (Pasca-Fase 4) | 494 |
| 13 | Metrik Keberhasilan (Usulan Target) | 523 |
| 14 | Risiko dan Dependensi | 536 |
| 15 | Asumsi | 551 |
| 16 | Pertanyaan Terbuka | 563 |
| 17 | Ringkasan Peta Jalan | 571 |
| 17.1 | Definisi Selesai per Fase | 581 |
| 18 | Backlog Ide Widget Tambahan (Pasca-Fase 4) | 615 |

**Catatan kasus khusus.**

- **§3, §11, dan §16 tidak punya sub-bagian bernomor.** `PRD 3.3`,
  `PRD 16.2`, dan `PRD 16.4` tidak ada. Butir daftar di dalam §3 (Tujuan),
  §11 (Pengembangan dengan AI), dan §16 (Pertanyaan Terbuka) dirujuk sebagai
  `§3 butir N` / `§11 butir N` / `§16 butir N`.
- **§9 hanya punya satu sub-bagian bernomor (§9.1).** `PRD 9.2` tidak ada;
  Application ID dibahas di paragraf utama §9.
- **§7.1** adalah satu-satunya sub-bagian bernomor di §7.
- **§8.5** membahas palet warna, bukan rahasia penandatanganan. Keamanan repo
  dan keystore ada di **§10**.

---

## 3. Versi: satu sumber kebenaran

Sebelum perbaikan ini, nomor versi tersebar di empat tempat dan saling
bertentangan:

| Sumber | Nilai | Keterangan |
|---|---|---|
| `docs/PRD-Dot.md` header | "Versi 1.0" | versi **dokumen** PRD, tapi ditulis tanpa label sehingga bisa disalahbaca sebagai versi aplikasi |
| `VERSION` (root repo) | `0.5.0` | diperbarui pada setiap commit rilis (`chore: release v0.5.0`) |
| `android/app/build.gradle.kts` | `versionName = "0.1.0"`, `versionCode = 1` | **stale** — tidak diperbarui sejak rilis v0.2.0; aplikasi terpasang akan menampilkan 0.1.0 |
| Tag git | `v0.2.0`, `v0.3.0`, `v0.5.0` | bukti rilis sebenarnya |

**Keputusan (arsitek, tindak lanjut rapat `m1792672637199` poin 1):**

1. **SSOT versi aplikasi = berkas `VERSION` di root repo.** Satu baris, satu
   nilai. Semua dokumen yang menyebut versi aplikasi **mengutip** berkas ini
   beserta commit verifikasi, tidak menulis ulang angkanya.
2. **Header PRD hanya membawa versi dokumen.** "Versi 1.0" telah diberi label
   menjadi "Versi dokumen: 1.0" agar tidak tertukar dengan versi aplikasi.
3. **`docs/README.md` dan `AGENTS.md` diberi pin versi** yang merujuk ke
   `VERSION` (lihat §4 dokumen-dokumen yang diperbaiki).
4. **`build.gradle.kts` yang stale (0.1.0) menjadi child task terpisah** —
   tetapkan `versionName` membaca berkas `VERSION`; `versionCode` mengikuti
   pemilik rilis. Ini perubahan kode, bukan dokumen, dan di luar lingkup
   perbaikan dokumen ini.

---

## 4. Audit & perbaikan cross-reference

Setiap rujukan `PRD x.y` di dokumen non-PRD diperiksa terhadap peta paragraf
§2, lalu diperbaiki. Hasil audit — disisir ulang 23 Oktober 2026 pada basis
`1f06890` (branch `docs/prd-peta-crossref`); dua baris ditambahkan/dikoreksi
dari putusan awal yang salah:

| Dokumen | Baris | Tertulis | Konteks klaim | Judul PRD sebenarnya | Hasil |
|---|---|---|---|---|---|
| `docs/README.md` | 11 | PRD 9.1 | release cadence | §9.1 "Ukuran dan Pembaruan Widget" | ✅ diperbaiki → **§10** + §5 |
| `AGENTS.md` | 10 | PRD 9.2 | Application ID beku | (§9.2 tidak ada) | ⛔ ditolak tulis (file proteksi) → teks pengganti di §4.1 |
| `AGENTS.md` | 12 | PRD 9.1 | Min/Target SDK | §9.1 "Ukuran dan Pembaruan Widget" | ⛔ ditolak tulis → §4.1 |
| `AGENTS.md` | 17 | PRD 3.3 | satu tugas kecil per sesi | (§3.3 tidak ada) | ⛔ ditolak tulis → §4.1 |
| `AGENTS.md` | 25 | PRD 8.5 | signing secrets dari CI | §8.5 "Palet Warna Dasar" | ⛔ ditolak tulis → §4.1 |
| `.cursor/rules.md` | 17 | PRD 9.2 | Application ID beku | (§9.2 tidak ada) | ✅ diperbaiki → **§9** |
| `.cursor/rules.md` | 35 | PRD 8.5 | signing secrets | §8.5 "Palet Warna Dasar" | ✅ diperbaiki → **§10** |
| `docs/STATUS-MAP.md` | 59 | PRD §6.12 (kutipan "statis, tanpa pembaruan berkala") | sifat dekoratif | kalimat itu ada di **§9.1** (baris Dekoratif), bukan §6.12 | ✅ diperbaiki → **§9.1** |
| `docs/REVIEW-PRD-FASE-STATE.md` | 100 | "baris 311" (§7.1, alarm tidak tepat waktu) | kutipan tabel izin | kalimat itu di baris **322** | ✅ diperbaiki → 322 |
| `docs/REVIEW-PRD-FASE-STATE.md` | 131 | "PRD §9 baris 420" (design token JSON) | kutipan butir §9 | kalimat itu di baris **431** | ✅ diperbaiki → 431 |
| `docs/REVIEW-PRD-FASE-STATE.md` | 8 | "PRD-Dot.md (613 baris)" | penyebutan total baris | total sebenarnya **624** baris | ✅ diperbaiki → 624 |
| `docs/REVIEW-PRD-FASE-STATE.md` | 162 | PRD §16.2 | nilai aksen `#D71921` masih perkiraan | **§16.2 tidak ada** (§16 hanya 5 butir daftar) → yang dimaksud **§16 "Pertanyaan Terbuka" butir 2** (baris 566) | ⛔ **tidak cocok** (luput dari audit awal) → diperbaiki di PR #6 |
| `docs/LICENSE-DECISION-REQUEST.md` | 136, 137, 182, 187 | §10, §16.4, §11.7 | lisensi | §10 cocok. **§16.4 dan §11.7 tidak ada** (§16 hanya butir daftar, §11 dirujuk per butir — lihat §2); maksudnya **§16 "Pertanyaan Terbuka" butir 4** (baris 568) dan **§11 "Pengembangan dengan AI" butir 7** (baris 469) | ⛔ **tidak cocok** — putusan awal "cocok semua ✅ BENAR" **salah**; diperbaiki di PR #6, branch `docs/review-license-prd-refs` |
| `docs/STATUS-MAP.md` | 54, 60, 61, 102 | §12, §9.1, §6.12, §8.3 | status widget & Plasma | §12 "Rencana Versi KDE Plasma", §9.1 "Ukuran dan Pembaruan Widget", §6.12 "Widget Dekoratif", §8.3 "Tema Terang dan Gelap" — keempatnya ada di §2 | ✅ BENAR (diverifikasi per baris; `§6.1`/`§6.8` yang disebut versi awal tabel ini tidak pernah muncul di berkasnya) |

**Pola kegagalan 1 — topik salah.** Enam rujukan salah di pasangan
`AGENTS.md` ↔ `.cursor/rules.md` (4 di `AGENTS.md`, 2 di `.cursor/rules.md`);
lima di antaranya menunjuk **§8.5** atau **§9.x** yang topiknya berbeda dari
klaimnya. Rujukan ditulis ulang dari ingatan, bukan dicek terhadap PRD.

**Pola kegagalan 2 — sub-bagian yang tidak pernah ada.** Tiga token
(`§16.2`, `§16.4`, `§11.7`), lima penyebutan di dua dokumen, menganggap setiap
paragraf punya sub-bagian desimal. Padahal §16 dan §11 hanya berisi butir
daftar; bentuk sahnya `§16 butir 2`, `§16 butir 4`, `§11 butir 7`. Ironisnya
§6 dokumen ini sudah memakai bentuk butir yang benar untuk rujukan yang sama —
jadi kesalahannya ada di baris audit, bukan di peta. Semua sudah diperbaiki di
PR #6 (`docs/review-license-prd-refs`).

Bagian §2 dokumen ini ada untuk menghentikan kedua pola itu: **rujuk ke peta,
bukan ke ingatan** — dan **keberadaan `§x.y` wajib dicek di §2 sebelum
dipakai**.

**Catatan positif.** `docs/README.md` aturan 4 sudah merujuk
`../design/tokens.json` dengan benar (berkasnya ada); klaim STATUS-MAP bahwa
rujukan mati `tokens.md` sudah dihapus terbukti **benar**.

### 4.1 Teks pengganti otoritatif untuk `AGENTS.md` (menunggu persetujuan)

`AGENTS.md` adalah *file proteksi* (agent-instruction); penulisan otomatis
ditolak oleh approval gate yang gagal dalam mode headless. **Empat rujukan di
sana masih salah** dan harus diganti manual oleh operator (Arif). Teks
pengganti otoritatif (kontrol: `.cursor/rules.md` sudah diperbaiki dengan pola
yang sama):

1. Baris 10–11:
   ```
   - Package / Application ID: `io.github.areifzyp.dot` (PRD §9 "Pertimbangan
     Teknis" — immutable once the first build succeeds; do **not** change).
   ```
2. Baris 12:
   ```
   - Min SDK: 26 | Compile/Target SDK: 34 (PRD §7 "Persyaratan Non-Fungsional";
     SDK bump is deliberate).
   ```
3. Baris 17:
   ```
   1. One small task per agent session (PRD §11 butir 3). Commit at the end of
   ```
4. Baris 23–25:
   ```
   4. No plaintext secrets. Signing config is injected by CI from GitHub
      secrets: `SIGNING_STORE_BASE64`, `SIGNING_STORE_PASSWORD`,
      `SIGNING_KEY_ALIAS`, `SIGNING_KEY_PASSWORD` (PRD §10 "Repo, Lisensi, dan
      Distribusi" — Keamanan repo).
   ```
5. Tambahan pin versi di bagian `## Project identity`:
   ```
   - App version: read from `VERSION` at repo root (SSOT); never hardcode the
     number in docs.
   ```

---

## 5. Tabel sifat → prerequisite PRD ("syslog sifat")

Tabel ini memetakan setiap **sifat** (trait perilaku sistem) ke paragraf PRD
yang menjadi **prerequisite (dasar syarat)**-nya. Setiap penambahan sifat baru
wajib menambah baris di sini dulu — kalau prerequisite-nya belum ada di PRD,
PRD-nya yang harus diperbarui. Inilah "syslog" sifat yang dimaksud dalam
rapat: log sistematis yang mengikat setiap sifat ke dokumen.

| Sifat (trait) | Syarat wajib singkat | Prerequisite PRD | Berlaku untuk |
|---|---|---|---|
| Pembaruan per menit | ClockUpdateScheduler/ScheduleExactAlarm, mode hemat | §9.1; §7.1 (izin exact alarm) | Jam |
| Pembaruan periodik hemat baterai | interval menit/jam, WorkManager/AlarmManager | §9.1 | Cuaca, Kalender, Status HP |
| Event-based, tidak polling | media session + receiver | §9.1 | Musik |
| Statis tanpa pembaruan | tanpa pembaruan berkala, tanpa animasi | §9.1; §6.12 | Dekoratif |
| Tahan offline | data sumber lokal-cached | §7 (Ketahanan); §6.10 | Cuaca, Kalender |
| Keadaan kosong & error seragam | placeholder konsisten per jenis galat | §6.10 | semua widget |
| Monokrom + satu aksen | hanya 1 warna aksen (opsional) | §8; §8.5 | semua widget |
| Palet dasar tak terhapus | 4 gaya bawaan minimal | §8.5; §6.7 (GAY-3) | semua widget |
| Tema terang/gelap mengikuti sistem | Material You opsional | §8.3; §6.7 (GAY-1/2) | semua widget |
| Font dot-matrix Doto | variabel, lisensi terbuka | §8.1; §10 (NOTICE aset) | semua widget |
| Gaya langsung tercermin tanpa rekombinasi | perubahan gaya terlihat segera | §6.7 (GAY-4) | semua widget |
| Satu aksi ketuk per widget | tujuan utama eksplisit | §6.9 (KON-4) | semua widget |
| Izin minimal & kontekstual | alasan jelas saat diminta | §7.1; §3 (Non-Tujuan privasi) | semua widget |
| Catatan lisensi aset | NOTICE/LICENSE tiap aset pihak ketiga | §10; §8.1; §8.2 | font, ikon |
| Privasi by default | kontak/kalender lokal, clipboard tanpa simpan | §3 (Non-Tujuan); §6.11 | Teks, HP, Kalender |
| Rilis bertahap | private → GitHub Releases → F-Droid (opsional Play Store) | §10; §17 | proses rilis |
| Robot kamuflase dekoratif | tampilan dipasang, bukan fungsi | §6.12; §18 | Dekoratif |

---

## 6. Prerequisite per nomor referensi (bacaan wajib)

Setiap rujukan `PRD x.y` yang dipakai di dokumen non-PRD harus disertai daftar
bacaan wajib lebih dulu. Tabel ini adalah daftar kanonik untuk rujukan yang
paling sering dipakai:

| Rujukan | Judul | Baca lebih dulu (urutan) |
|---|---|---|
| §7 | Persyaratan Non-Fungsional | §3 (non-tujuan), §9 (teknis), §7.1 (izin) |
| §7.1 | Izin Android per Fitur | §6.x fitur terkait; §7 (ketahanan); §3 (privasi) |
| §8.1 | Font: Doto | §8 (prinsip); §6.7 (gaya Nothing); §10 (lisensi aset) |
| §8.3 | Tema Terang dan Gelap | §8; §6.7; §8.5 |
| §8.5 | Palet Warna Dasar (wajib) | §8; §8.3; §6.7; **realita:** `design/tokens.json` |
| §9.1 | Ukuran dan Pembaruan Widget | §7 (performa); §6.2 (Jam); §7.1 (exact alarm) |
| §10 | Repo, Lisensi, dan Distribusi | §11 butir 7 (tinjau dependensi); §16 butir 4 (lisensi); §8.1 |
| §11 butir 3 | Satu tugas kecil per sesi | §11.1 (urutan tugas Fase 1); `AGENTS.md` |
| §6.10 | Keadaan Kosong dan Error | §6.9 (onboarding); §7 (ketahanan) |
| §6.12 | Widget Dekoratif | §9.1 (baris Dekoratif); §18 (backlog ide) |
| §12 | Rencana KDE Plasma | §5 (ruang lingkup); §9.1; §17.1 (definisi selesai) |
| §16 butir 2 | Pertanyaan Terbuka (aksen) | §8.5 (palet); §13 (metrik) |

---

## 7. Aturan pemeliharaan

1. **Saat membekukan/memperbarui PRD:** perbarui tabel §2 (nomor baris) dan
   setiap rujukan baris di dokumen lain. Nomor baris akan drift — selalu
   cantumkan commit basis.
2. **Saat menulis rujukan `PRD x.y` baru:** periksa judulnya di §2. Rujukan
   wajib format `PRD §x.y "<Judul>"` pada penyebutan pertama per dokumen.
3. **Saat mengusulkan sifat/fitur baru:** tambah baris di §5. Jika prerequisite
   belum ada di PRD → tulis keputusan ke dokumen keputusan sebelum mengkode.
4. **Saat merilis versi:** perbarui `VERSION` (SSOT) saja; dokumen lain
   memverifikasi ke commit, bukan menulis ulang angka. `build.gradle.kts`
   membaca `VERSION`.
5. **`AGENTS.md` dan `.cursor/rules.md`** adalah pasangan — setiap perbaikan
   rujukan pada salah satu harus dicerminkan ke yang lain.
