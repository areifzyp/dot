# License Decision Record — Dot.

**Status item:** **BLOCKED** · **Dicatat:** 22 Oktober 2026, 22:47 WIB ·
**Pengusul:** doc-writer · **Pemberi keputusan:** tim hukum/kepatuhan + pemilik proyek ·
**Terverifikasi pada commit:** `27d5917` (`main`, VERSION `0.5.0`; fakta lisensi tidak
berubah sampai `HEAD` — `git diff 27d5917..HEAD` pada PRD, `NOTICE`, `plasma/*/metadata.json`,
`SettingsScreen.kt`, `strings.xml`, dan berkas Gradle → kosong) ·
**Kartu pelacak:** `t_6cc84cef` (doc-writer) · **Kartu kembar:** `t_2e27c818` (arsitek) ·
**Paket permintaan (kanonik):** [`LICENSE-DECISION-REQUEST.md`](LICENSE-DECISION-REQUEST.md)

Satu berkas, dua peran — catatan status yang hidup, dan formulir keputusan yang sengaja
masih kosong:

| Bagian | Isi | Status |
|---|---|---|
| §1 | Catatan status BLOCKED + bukti bahwa keputusan belum ada | terisi |
| §2 | Syarat "keputusan dianggap diterima" (agar tidak ambigu) | terisi |
| §3 | **License Decision Record** — formulir keputusan | **kosong, menunggu keputusan** |
| §4 | Pertanyaan eksplisit untuk tim hukum/kepatuhan | terisi |
| §5 | Mekanisme cek berkala + log follow-up | terisi (PIC & kanal: TODO + usulan default, menunggu konfirmasi) |
| §6 | Langkah menutup item setelah keputusan keluar | terisi |
| §7 | Pekerjaan yang diblokir + tautan | terisi |
| §8 | Riwayat berkas ini | terisi |

> **Aturan berkas ini:** §3 hanya diisi dari keputusan **tertulis** pihak yang berwenang.
> Jangan isi dengan dugaan, kesimpulan agen, atau "sepertinya". Bila satu pertanyaan belum
> bisa diputuskan, tulis **"belum diputuskan"** — itu jawaban sah dan mencegah agen menebak.

---

## 1. Catatan status

| Field | Isi |
|---|---|
| Status | **BLOCKED** — menunggu keputusan lisensi dari tim hukum/kepatuhan |
| Alasan | Repo menyatakan lisensi di tiga tempat yang saling bertentangan (PRD §10 `GPL-2.0-or-later`, `NOTICE` `Apache-2.0`, 6 × `metadata.json` `MIT`) dan tidak punya berkas `LICENSE`. Memilih salah satunya adalah keputusan pemilik atas saran hukum, bukan pekerjaan dokumentasi. |
| Dicatat pertama | 22 Oktober 2026, 20:33 WIB (catatan status pertama di kartu `t_6cc84cef`) |
| Dicatat ulang | 22 Oktober 2026, 22:47 WIB (berkas ini dibuat; keputusan tetap belum ada) |
| Diamandemen | 22 Oktober 2026, 23:35 WIB — lingkup penahanan dipersempit (S1–S4), premis "repo private" dicabut karena repo sudah publik sejak 18 Okt 2026 |
| Pengusul | doc-writer |
| Penanggung jawab follow-up | `> TODO:` belum ditunjuk — **usulan default: pemilik repo `@areifzyp`** (peran: pemilik proyek, pihak yang berwenang memutuskan lisensi). Konfirmasi ke pengusul (doc-writer) diperlukan; rincian di §5 |
| Kanal komunikasi ke tim hukum | `> TODO:` belum ditentukan — **usulan default: email resmi ke `areifzyp@gmail.com`** (narahubung pemilik repo, dari `git config user.email`; repo tidak mencatat kontak tim hukum). Setiap follow-up juga ditulis sebagai komentar di kartu `t_6cc84cef`. Rincian di §5 |
| Kadens cek | mingguan, Senin 09:00 WIB — job cron `3d4aef87edac` (§5) |
| Follow-up pertama | **Senin, 26 Oktober 2026, 09:00 WIB** (§5) |
| Kartu pelacak | `t_6cc84cef` (doc-writer) — kartu ini |
| Kartu kembar | `t_2e27c818` (arsitek), judul sama, isi kosong — salah satu harus ditutup agar tidak dua kartu menunggu hal yang sama |

### 1.1 Bukti bahwa keputusan belum ada

Diperiksa 22 Oktober 2026, 22:44 WIB, pada commit `e16eaea`:

| # | Yang diperiksa | Perintah | Hasil |
|---|---|---|---|
| 1 | Berkas `LICENSE`/`COPYING` di akar repo | `git ls-tree --name-only HEAD \| grep -iE "licen\|copying"` | **kosong** — tidak ada |
| 2 | Teks yang dilihat pengguna di aplikasi | `grep -n "Apache License" NOTICE` | `NOTICE:8` → Apache-2.0 (tombol "Lisensi" di aplikasi membuka berkas ini) |
| 3 | Klaim lisensi di PRD | `grep -n "GPL-2.0-or-later" docs/PRD-Dot.md` | `:451` (§10, "diterapkan nanti"), `:568` (§16.4) |
| 4 | Metadata plasmoid | `grep -n '"License"' plasma/*/metadata.json` | 6 berkas, semuanya `MIT` di baris 14 |
| 5 | Komentar keputusan di kartu `t_6cc84cef` | — | tidak ada; thread hanya berisi catatan doc-writer dan `auto-decomposer` |
| 6 | Formulir §3 berkas ini | `bash scripts/license-status.sh --quiet` | `exit 1` → masih kosong (10 field, 11 penanda) |

Ringkas: tidak ada keputusan tertulis di repo, di kartu, maupun di berkas mana pun.
Dua kali kartu di-*unblock* (22 Okt 20:36 dan 22:36 WIB) tanpa jawaban tertulis — unblock
tanpa teks **bukan** keputusan (lihat §2).

### 1.2 Apa yang ditahan, apa yang berjalan

| Ditahan sampai keputusan keluar | Tetap berjalan |
|---|---|
| Membuat `LICENSE`; merge ke `main` yang menyentuh klaim lisensi (**S1**); tag / `VERSION` / GitHub Release (**S2**); CI `main` mendistribusikan APK (**S3**); membagikan APK; pengajuan F-Droid/Play; publikasi plasmoid ke KDE Store; teks lisensi di Tentang Aplikasi | Pengembangan kode, tes, CI non-distribusi, dokumentasi, merge yang tidak menyentuh klaim lisensi |

Rincian alasan per aktivitas: [`LICENSE-DECISION-REQUEST.md` §4](LICENSE-DECISION-REQUEST.md).

> **Amandemen 22 Okt 2026 (S1–S4):** repo sudah publik sejak 18 Okt 2026 (`private:false`,
> `NOTICE` 200, `LICENSE` 404), jadi "membuka repo" bukan lagi aktivitas yang ditahan —
> eksposur sudah berjalan. Blokir merge dipersempit ke berkas yang memuat klaim lisensi.
> Teks keputusan PM ditempel verbatim di [`LICENSE-DECISION-REQUEST.md` §4](LICENSE-DECISION-REQUEST.md).

---

## 2. Kapan keputusan dianggap diterima

Agar tidak ada perdebatan saat jawaban datang, keputusan dianggap **diterima** hanya bila
memenuhi kelima syarat berikut:

1. **Tertulis** — komentar di kartu `t_6cc84cef`, berkas, atau salinan pesan (email/chat)
   yang ditempel ke kartu. Bukan lisan, bukan "sudah di-unblock".
2. **Berwenang** — datang dari orang bernama di tim hukum/kepatuhan, atau dari pemilik
   proyek yang menyatakan bertindak atas nama mereka. Nama + jabatan dicatat di §3.
3. **Menjawab D1, D2, D3** — boleh "belum diputuskan" untuk salah satunya; itu tetap
   jawaban sah. Yang tidak boleh: tidak disebut sama sekali.
4. **Menyebut cakupan** — kode Android, kode Plasma, dan 6 plasmoid (apakah ikut atau
   terpisah).
5. **Bertanggal** — tanggal keputusan dan (bila ada) tanggal berlaku.

**Bukan keputusan:** unblock kartu tanpa teks; perubahan berkas tanpa catatan alasan;
kesimpulan agen dari isi repo; "ikut saja yang sudah ada di NOTICE" tanpa pernyataan
eksplisit bahwa itu keputusan final.

---

## 3. License Decision Record (formulir)

Isi sel kanan. Jangan hapus baris. Ganti setiap `_(belum diisi)_` dengan isi sebenarnya;
selama penanda itu masih ada, `scripts/license-status.sh` menganggap keputusan belum
tercatat.

> **Metadata template** — versi `1.0` · dibuat 22 Oktober 2026 · pemilik template: doc-writer
> (pengusul) · pemilik keputusan: tim hukum/kepatuhan + pemilik proyek. Template siap pakai ada di
> [`templates/LICENSE-DECISION-RECORD.template.md`](templates/LICENSE-DECISION-RECORD.template.md)
> (v1.0, field a–i + petunjuk pengisian) — dipakai bila keputusan menyangkut komponen lain. Untuk
> lisensi Dot. sendiri, §3 di bawah **adalah** formulir kanoniknya, dan record terisi menggantikan
> isi sel, bukan membuat berkas baru.

| # | Field | Isi |
|---|---|---|
| 1 | **Jenis lisensi / komponen yang ditinjau** | _(belum diisi)_ — mis. "kode aplikasi Android (`android/`) + 6 plasmoid Plasma (`plasma/`) + font Doto" |
| 2 | **Pertanyaan hukum yang diajukan** | D1–D3 + Q2–Q8, lihat §4 berkas ini dan §6 [`LICENSE-DECISION-REQUEST.md`](LICENSE-DECISION-REQUEST.md) |
| 3 | **Tanggal pengajuan ke tim hukum/kepatuhan** | _(belum diisi)_ — paket bukti tersedia sejak 22 Okt 2026 (commit `6ef95cc`, diperbarui `e16eaea`) |
| 4 | **Nama/narahubung penanggung jawab di tim tersebut** | _(belum diisi)_ — nama + jabatan + kanal. Bila proyek tidak punya tim hukum formal, tulis "pemilik proyek (nama)" dan jangan mengarang pihak lain |
| 5 | **Keputusan akhir** | _(belum diisi)_ — `disetujui` / `ditolak` / `bersyarat` |
| 6 | **Syarat & batasan** | _(belum diisi)_ — mis. kewajiban atribusi, larangan redistribusi bentuk tertentu, berkas yang wajib diubah, tenggat |
| 7 | **Tanggal berlaku** | _(belum diisi)_ — sekarang / saat APK pertama dibagikan (D2). Opsi "saat repo dibuka" gugur: repo sudah publik 18 Okt 2026 |
| 8 | **Lampiran/referensi** | _(belum diisi)_ — tautan komentar kartu, email, commit yang menerapkan keputusan |

Field pendukung (wajib untuk kelengkapan, bukan untuk membuka blokir):

| # | Field | Isi |
|---|---|---|
| 9 | Keputusan per ID | D1 = _(belum diisi)_ · D2 = _(belum diisi)_ · D3 = _(belum diisi)_ |
| 10 | Tindak lanjut wajib | checklist 10 berkas di [`LICENSE-DECISION-REQUEST.md` §7](LICENSE-DECISION-REQUEST.md) — pemilik & kartu: _(belum diisi)_ |

Format jawaban terpendek yang tetap sah (boleh ditempel sebagai komentar kartu):

```text
D1 = ......   (GPL-2.0-or-later | Apache-2.0 | MIT | lain: ......)
D2 = ......   (sekarang | saat APK pertama dibagikan)
D3 = ......   (plasmoid tetap MIT | disamakan dengan D1)
Keputusan akhir : disetujui | ditolak | bersyarat
Diputuskan oleh : ...... (nama, jabatan/tim)
Tanggal         : ......
```

---

## 4. Pertanyaan yang harus dijawab (eksplisit)

Daftar kanonik ada di [`LICENSE-DECISION-REQUEST.md` §6](LICENSE-DECISION-REQUEST.md)
(lengkap dengan bukti dan alasan). Tabel di bawah adalah versi ringkas + kolom jawaban;
**bila §6 berubah, ubah tabel ini juga** agar kedua dokumen tidak berbeda.

| ID | Pertanyaan | Kalau tidak dijawab, yang jadi ambigu | Jawaban |
|---|---|---|---|
| **D1** | Lisensi apa yang berlaku untuk kode Dot. sendiri (Android + Plasma)? | Tidak ada isi sah untuk `LICENSE`; `NOTICE`/PRD/plasmoid tetap bertentangan | _(menunggu jawaban)_ |
| **D2** | Ditetapkan sekarang, atau ditunda sampai APK pertama dibagikan? (opsi "saat repo dibuka" gugur — repo sudah publik 18 Okt 2026) | Tidak jelas kapan kewajiban lisensi mulai berlaku; pemicu "saat repo dibuka" di PRD §10 sudah lewat tanpa keputusan | _(menunggu jawaban)_ |
| **D3** | Enam plasmoid Plasma tetap `MIT`, atau disamakan dengan D1? | Satu repo dua lisensi tanpa berkas yang menjelaskan pembagiannya | _(menunggu jawaban)_ |
| **Q2** | Bila `GPL-2.0-or-later`: apakah "or later" harus eksplisit di `LICENSE` dan/atau header berkas sumber? Cukup satu `LICENSE` di akar? | Bentuk penerapan bisa salah meski pilihan lisensinya benar | _(menunggu jawaban)_ |
| **Q3** | Apakah 101 dependensi Apache-2.0 di APK menimbulkan kewajiban tambahan (mis. teks lisensi pihak ketiga di dalam aplikasi)? Apakah `NOTICE` sekarang sudah memenuhi itu? | Kewajiban atribusi/redistribusi bisa terlewat saat APK dibagikan | _(menunggu jawaban)_ |
| **Q4** | `glance-appwidget-external-protobuf:1.1.0` (BSD-3-Clause) dan `com.google.guava:listenablefuture:1.0` (POM tanpa lisensi) perlu tindakan khusus? | Dua artefak di APK tanpa jawaban tertulis soal kewajibannya | _(menunggu jawaban)_ |
| **Q5** | Bolehkah 6 plasmoid tetap `MIT` di dalam repo yang lisensi utamanya GPL/Apache? Di berkas mana pembagian itu dinyatakan? | `metadata.json` dan `LICENSE` bisa saling bertentangan | _(menunggu jawaban)_ |
| **Q6** | Font Doto (SIL OFL 1.1): apakah ketentuan *Reserved Font Name* terpicu bila kelak dibuat instance statis dari font variabel? | Risiko saat font di-instance ulang (skenario tercatat di PRD `:354`) | _(menunggu jawaban)_ |
| **Q7** | Sama dengan D2, dari sisi hukum: boleh ditunda atau harus sekarang? | Jawaban D2 bisa datang tanpa dasar; ini yang menutup celahnya | _(menunggu jawaban)_ |
| **Q8** | Pemakaian nama "Nothing" dan tagline "Inspired by Nothing" di judul/README/`NOTICE` perlu ditinjau terpisah sebelum publikasi? | Jalur merek bisa tertinggal saat publikasi | _(menunggu jawaban)_ |
| **Q9** | Cakupan penggunaan: internal, komersial (berbayar/beriklan), redistribusi APK di toko — mana yang diizinkan, mana yang tidak? | Rilis bisa melanggar batas yang tidak pernah ditulis | _(menunggu jawaban)_ |
| **Q10** | Bolehkah pihak lain memodifikasi & mendistribusikan ulang kode (fork, build komunitas)? Sublicensing? Source wajib disertakan saat APK dibagikan? | Kewajiban redistribusi source bisa terlewat saat APK keluar | _(menunggu jawaban)_ |
| **Q11** | Bentuk bukti keputusan yang sah: komentar kartu, email, atau commit `LICENSE`? Siapa yang berwenang menyatakan final? | Item bisa ditutup dengan "bukti" yang tidak sah | _(menunggu jawaban)_ |
| **Q12** | Apakah penanda "lisensi belum final" di `README`/`NOTICE` mengurangi risiko atau justru menambah klaim lisensi baru? (`NOTICE` = teks yang dibuka tombol "Lisensi" di aplikasi) | Penanda bisa dibaca sebagai pernyataan lisensi tambahan; perubahan `NOTICE` ditahan S1 sampai Q12 dijawab | _(menunggu jawaban)_ |

### 4.1 Uji ambiguitas sebelum menerima jawaban

Jawaban hanya dipakai bila keempat hal ini bisa dijawab "ya" dari teksnya:

- [ ] Menyebut **ID lisensi yang tepat** (mis. `GPL-2.0-or-later`, bukan "GPL" atau "open source").
- [ ] Menyebut **cakupan**: `android/`, `plasma/`, font, dan berkas tes — mana yang ikut.
- [ ] Menyebut **waktu berlaku**: sekarang / saat APK dibagikan (opsi "saat repo dibuka" sudah gugur — repo publik 18 Okt 2026).
- [ ] Menyebut **nasib tiga sumber yang sekarang bertentangan**: `NOTICE`, PRD §10, 6 × `metadata.json`.

Bila ada yang tidak terjawab, jangan isi §3 — tanyakan ulang butir itu saja.

---

## 5. Mekanisme cek berkala

| Aspek | Isi |
|---|---|
| Kadens | mingguan — **Senin 09:00 WIB** (UTC+7), hari kerja yang sama setiap minggu. Tidak ada cek di antara jadwal kecuali ada pemicu luar (lihat baris "Eskalasi") |
| Tanggal follow-up pertama | **Senin, 26 Oktober 2026, 09:00 WIB**. Terverifikasi dari job `3d4aef87edac`: `schedule = every monday 9am`, `next_run_at = 2026-10-26T09:00:00+07:00`, `last_run_at = 2026-10-22T22:57:38+07:00`, `last_status = ok` (dibaca 23 Okt 2026 00:36 WIB) |
| Pelaksana cek | job cron `3d4aef87edac` ("Dot. — cek mingguan keputusan lisensi", `every monday 9am`, `deliver: local`). Setiap Senin ia menjalankan skrip cek lalu **memposting hasilnya sebagai komentar di kartu `t_6cc84cef`** |
| Penanggung jawab follow-up (PIC) | `> TODO:` **belum ditunjuk.** Usulan default: **pemilik repo `@areifzyp`** — peran: pemilik proyek, pihak yang berwenang memutuskan D1–D3 dan satu-satunya narahubung yang tercatat (`git config user.email` → `areifzyp@gmail.com`; `git ls-tree -r --name-only HEAD \| grep -iE 'CODEOWNERS\|CONTRIBUTING\|GOVERNANCE\|SECURITY\|MAINTAINERS'` → kosong, diperiksa 23 Okt 2026 00:36 WIB pada `a7a6041`). **Konfirmasi ke pengusul (doc-writer) diperlukan** — doc-writer hanya mencatat status dan menjalankan cek otomatis, bukan pihak yang menghubungi tim hukum. Bila pemilik menunjuk orang lain, ganti baris ini **dan** §1; jangan biarkan tetap TODO |
| Kanal komunikasi ke tim hukum | `> TODO:` **belum ditentukan.** Usulan default dua lapis: (1) **pengajuan ke pihak keputusan** — email resmi ke narahubung pemilik repo, `areifzyp@gmail.com` (satu-satunya kontak yang tercatat; repo tidak punya berkas kontak tim hukum); (2) **kanal catatan resmi** — komentar di kartu `t_6cc84cef`, sudah aktif dan terbukti (§5.1). Bila pemilik menyediakan, (1) boleh diganti thread Slack kanal legal atau tiket sistem internal — ganti baris ini saat ditentukan. **Konfirmasi ke pengusul (doc-writer) diperlukan.** Catatan teknis: profil `doc-writer` tidak punya kanal Telegram aktif, jadi keluaran job masuk ke kartu (bukan chat) |
| Yang diperbarui setiap follow-up | (1) Satu baris baru di log §5.1: tanggal + jam WIB, oleh, kanal, **ringkasan respons** (atau `tidak ada respons`), status item. (2) Field "Dicatat ulang" di §1 bila status berubah. (3) §3 **hanya** bila ada keputusan tertulis — bukan dugaan. (4) Hitungan follow-up tanpa respons, dipakai baris "Eskalasi" |
| Eskalasi bila tidak ada respons | 4 follow-up mingguan berturut-turut tanpa jawaban tertulis (≈ 1 bulan: 26 Okt, 2 Nov, 9 Nov, 16 Nov 2026) → tulis baris eskalasi di §5.1 dan ajukan keputusan ke operator board. Kartu tetap BLOCKED; agen tidak boleh menebak lisensi |
| Cek otomatis (tanpa menghubungi siapa pun) | `bash scripts/license-status.sh` — mencetak status LICENSE/NOTICE/PRD/plasmoid + apakah §3 sudah terisi; exit `0` hanya bila LICENSE ada, NOTICE & PRD sejalan, dan §3 terisi |
| Pemicu berhenti | skrip menjadi senyap (exit `0`, tanpa keluaran) saat `LICENSE` ada, `NOTICE` dan PRD §10 sejalan dengan `LICENSE`, dan §3 terisi. Metadata plasmoid hanya dicatat sebagai catatan, tidak menahan — karena D3 boleh memutuskan plasmoid tetap `MIT` di repo yang lisensi utamanya lain |
| Mematikan pengingat | `hermes cron remove 3d4aef87edac` (setelah keputusan diterapkan, atau bila pengingat tidak diperlukan) |

### 5.1 Log follow-up

Satu baris per pemeriksaan. Jangan hapus baris lama.

| Tanggal (WIB) | Oleh | Kanal | Hasil | Status item |
|---|---|---|---|---|
| 22 Okt 2026 20:33 | doc-writer | kartu `t_6cc84cef` | Paket keputusan disusun (commit `6ef95cc`); D1–D3 diajukan | BLOCKED |
| 22 Okt 2026 22:36 | doc-writer | kartu `t_6cc84cef` | Pemeriksaan ulang #2: tidak ada `LICENSE`, tidak ada jawaban tertulis; formulir jawaban 3 baris ditambahkan (commit `e16eaea`) | BLOCKED |
| 22 Okt 2026 22:47 | doc-writer | kartu `t_6cc84cef` | Pemeriksaan ulang #3: tetap belum ada keputusan; berkas ini dibuat + skrip cek status | BLOCKED |
| 22 Okt 2026 22:57 | job cron `3d4aef87edac` | kartu `t_6cc84cef` | Uji job pertama: komentar mingguan terpasang di kartu (terverifikasi di thread) — mekanisme pengingat aktif, jadwal berikutnya Senin 26 Okt 2026 09:00 WIB | BLOCKED |
| 23 Okt 2026 00:36 | doc-writer | kartu `t_6cc84cef` | **Mekanisme follow-up ditetapkan** (kartu `t_f62a7c52`): kadens mingguan Senin 09:00 WIB, follow-up pertama **26 Okt 2026**, PIC & kanal diisi usulan default + TODO konfirmasi (§5). Belum ada respons dari pihak keputusan; keputusan lisensi tetap belum ada | BLOCKED |

---

## 6. Setelah keputusan diterima — langkah menutup

1. Isi **§3** (10 field) dan kolom jawaban di **§4** dari teks keputusan, bukan dari tafsiran.
2. Jalankan `bash scripts/license-status.sh` → harus exit `0` dan senyap.
3. Perbarui header + §11 [`LICENSE-DECISION-REQUEST.md`](LICENSE-DECISION-REQUEST.md)
   (status `MENUNGGU KEPUTUSAN` → `DIPUTUSKAN`, tanggal, tautan ke §3 berkas ini) — dua
   dokumen tidak boleh berbeda status.
4. Tandai kartu `t_6cc84cef` **selesai/RESOLVED** dengan ringkasan berisi ID lisensi yang
   diputuskan + commit yang menerapkan.
5. Buat kartu tindak lanjut untuk checklist 10 berkas ([`§7`](LICENSE-DECISION-REQUEST.md))
   — assignee doc-writer, tautkan ke berkas ini. Ini yang benar-benar menerapkan keputusan.
6. Tutup kartu kembar `t_2e27c818` (arsitek) atau arahkan komentarnya ke berkas ini, supaya
   tidak ada dua kartu menunggu hal yang sama.
7. Bila D2 = "ditunda": status item menjadi **DEFERRED**, bukan RESOLVED — catat tanggal
   tinjau ulang di §1 dan jangan hapus pengingat mingguan.

---

## 7. Pekerjaan yang diblokir (tautan)

| Pekerjaan | Berkas / bukti | Ditahan oleh |
|---|---|---|
| Berkas `LICENSE` di akar repo | belum ada ([`§1.1`](#11-bukti-bahwa-keputusan-belum-ada) butir 1) | D1 |
| Teks lisensi yang dilihat pengguna | [`NOTICE:8-12`](../NOTICE), [`SettingsScreen.kt:137`](../android/app/src/main/java/io/github/areifzyp/dot/settings/SettingsScreen.kt), [`strings.xml:86`](../android/app/src/main/res/values/strings.xml) | D1 |
| Klaim lisensi di PRD | [`PRD-Dot.md:451`](PRD-Dot.md) §10, [`:568`](PRD-Dot.md) §16.4, [`:611`](PRD-Dot.md) kriteria Fase 4 | D1, D2 |
| Metadata 6 plasmoid | [`plasma/*/metadata.json:14`](../plasma) | D3 |
| Distribusi APK · F-Droid/Play · KDE Store · tag/`VERSION`/Release (S2) · merge yang menyentuh klaim lisensi (S1) | [`LICENSE-DECISION-REQUEST.md` §4](LICENSE-DECISION-REQUEST.md) | D1, D2, S1–S3 |
| Pembukaan repo | **sudah terjadi** 18 Okt 2026 — bukan lagi pekerjaan yang ditahan ([`§2.2`](LICENSE-DECISION-REQUEST.md)) | — |
| Aturan lisensi untuk dependensi baru + indeks dokumentasi | [`AGENTS.md`](../AGENTS.md) §Conventions, [`docs/README.md`](README.md) | D1 |
| Kartu pelacak | `t_6cc84cef` (doc-writer) | — |
| Kartu kembar | `t_2e27c818` (arsitek) | — |

---

## 8. Riwayat berkas ini

| Waktu (WIB) | Commit | Perubahan |
|---|---|---|
| 22 Okt 2026 22:47 | commit yang menambahkan berkas ini — lihat `git log --follow -- docs/LICENSE-DECISION-RECORD.md` | Berkas dibuat: catatan status BLOCKED, formulir §3 (kosong), daftar pertanyaan §4, mekanisme cek §5, langkah penutupan §6, tautan pekerjaan diblokir §7. Disertai `scripts/license-status.sh` |
| 22 Okt 2026 23:26 | commit ini | Gerbang §3 diperbaiki: `grep -c` (menghitung **baris** di seluruh berkas, termasuk prosa §1.1 dan instruksi §3) diganti hitungan penanda **di dalam tabel §3 saja** via `awk` + `gsub` → `11 penanda` (10 field; baris "Keputusan per ID" memuat D1/D2/D3). Uji dua arah: §3 kosong → `exit 1` + problem "formulir §3 … belum diisi"; salinan dengan §3 terisi + `LICENSE` + `NOTICE`/PRD §10/plasmoid selaras → `exit 0`. Baris 6 §1.1 kini memakai perintah gerbang, bukan `grep -c` |
| 22 Okt 2026 23:35 | commit ini | Amandemen butir #4 (S1–S4): "membuka repo" keluar dari daftar ditahan (sudah terjadi), penahanan dipersempit ke merge berkas klaim lisensi (S1), tag/`VERSION`/Release (S2), CI distribusi APK (S3). Premis D2 diperbaiki di §1, §3 field 7, format jawaban, §4, §4.1. Q12 ditambahkan ke §4. §7 diperbarui |
| 23 Okt 2026 00:27 | commit ini | Template berdiri sendiri ditambahkan: `docs/templates/LICENSE-DECISION-RECORD.template.md` (v1.0, metadata + field a–i, tiap field dengan placeholder dan satu baris petunjuk). Catatan §3 diperbarui: template kini punya berkas, §3 tetap formulir kanonik untuk lisensi Dot. |
| 23 Okt 2026 00:36 | commit ini | Mekanisme follow-up §5 dilengkapi (kartu `t_f62a7c52`): tanggal follow-up pertama 26 Okt 2026, PIC & kanal diisi usulan default + `TODO` konfirmasi, baris "Yang diperbarui setiap follow-up" dan "Eskalasi bila tidak ada respons" (4 minggu). §1 dan tabel ringkasan atas diselaraskan; log §5.1 ditambah satu baris |

---

*Berkas ini mencatat, bukan memutuskan. Bila ada baris yang tidak cocok lagi dengan repo,
perbaiki baris itu beserta buktinya — jangan menambah klaim baru tanpa perintah verifikasi
di §1.1 atau `scripts/license-status.sh`.*
