# Keputusan Lisensi Dot. — Permintaan untuk Tim Hukum/Kepatuhan

**Status:** MENUNGGU KEPUTUSAN · **Memblokir rilis/distribusi:** ya — lingkup dipersempit
22 Oktober 2026, lihat §4 (S1–S4) ·
**Terverifikasi pada commit:** `27d5917` · **Pemeriksaan ulang terakhir:** 23 Oktober 2026,
00:33 WIB · **Riwayat pemeriksaan:** §11 ·
**Penyusun:** doc-writer · **Pemberi keputusan:** tim hukum/kepatuhan + pemilik proyek ·
**Catatan & formulir keputusan:** [`LICENSE-DECISION-RECORD.md`](LICENSE-DECISION-RECORD.md)

Repo Dot. menyatakan lisensinya di tiga tempat, dan ketiganya tidak sama. Tidak ada
berkas `LICENSE` di akar repo. Dokumen ini mengumpulkan bukti, inventaris lisensi
dependensi, dan pertanyaan yang harus dijawab agar satu keputusan bisa diambil.

> Dokumen ini **bukan nasihat hukum**. Semua pernyataan di sini adalah fakta yang bisa
> diperiksa di repo atau hasil perintah yang dicantumkan di §9. Konsekuensi hukum
> sengaja dibiarkan sebagai pertanyaan terbuka di §6.

---

## 0. Formulir jawaban — tiga baris ini yang menutup kartu

Keputusan tidak perlu berbentuk memo. Cukup isi tiga baris berikut, baik sebagai komentar
di kartu maupun dengan menyunting dokumen ini:

```text
D1 = ............   (GPL-2.0-or-later | Apache-2.0 | MIT | lain: ........)
D2 = ............   (sekarang | saat APK pertama dibagikan)
D3 = ............   (plasmoid tetap MIT | disamakan dengan D1)
Catatan/persetujuan: nama + tanggal
```

Bila satu pertanyaan memang belum bisa diputuskan, tulis **"belum diputuskan"** — itu
tetap merupakan jawaban yang sah dan mencegah agen menebak. Pertanyaan pendukung
(Q2–Q6, Q8) boleh menyusul; yang menahan rilis hanya D1–D3 (lihat §8).

Jawaban dicatat di [`LICENSE-DECISION-RECORD.md` §3](LICENSE-DECISION-RECORD.md) —
formulir yang sama, dalam bentuk tabel. Selama §3 di sana masih memuat penanda
`_(belum diisi)_`, gerbang otomatis `bash scripts/license-status.sh` akan keluar dengan
exit `1`.

---

## 1. Keputusan yang diminta

| ID | Pertanyaan | Pilihan yang tersedia | Diputuskan oleh |
|---|---|---|---|
| **D1** | Lisensi apa yang berlaku untuk kode Dot. sendiri (Android + Plasma)? | `GPL-2.0-or-later` (PRD §10) · `Apache-2.0` (NOTICE) · `MIT` (metadata plasmoid) · lain | hukum/kepatuhan + pemilik |
| **D2** | Apakah lisensi itu ditetapkan **sekarang** atau ditunda sampai APK pertama dibagikan? | sekarang · saat APK pertama dibagikan · belum diputuskan | pemilik, atas saran hukum |
| **D3** | Apakah enam plasmoid Plasma tetap `MIT` di `metadata.json`, atau disamakan dengan lisensi proyek utama? | samakan · pertahankan MIT | pemilik, atas saran hukum |

Selama D1–D3 belum dijawab, pekerjaan di §4 ditahan. Pekerjaan lain (kode, tes, CI,
dokumentasi) **tidak** ditahan.

> **Premis D2 berubah (22 Okt 2026).** Opsi "saat repo dibuka" tidak lagi tersedia: repo
> sudah publik sejak 18 Oktober 2026 (§2.2), jadi pemicu yang ditulis PRD §10 ("diterapkan
> nanti saat repo dibuka atau APK dibagikan") **sudah lewat tanpa keputusan**. D2 tinggal
> memilih: tetapkan sekarang, atau tunggu APK pertama dibagikan.

---

## 2. Fakta terverifikasi

| # | Sumber | Isi | Bukti |
|---|---|---|---|
| 1 | PRD §10 | Lisensi proyek **GPL-2.0-or-later**, "diterapkan nanti saat repo dibuka atau APK dibagikan" | [`docs/PRD-Dot.md:451`](PRD-Dot.md) |
| 2 | `NOTICE` | "Licensed under the **Apache License, Version 2.0**" | [`NOTICE:8-12`](../NOTICE) |
| 3 | Tombol "Lisensi" di aplikasi | Membuka URL `github.com/areifzyp/dot/blob/main/NOTICE`, yaitu sumber #2 — jadi **pengguna melihat Apache-2.0**, bukan GPL | [`SettingsScreen.kt:137`](../android/app/src/main/java/io/github/areifzyp/dot/settings/SettingsScreen.kt), [`strings.xml:86`](../android/app/src/main/res/values/strings.xml) |
| 4 | Berkas `LICENSE` | **Tidak ada di akar repo.** `git ls-tree --name-only HEAD` hanya memuat `NOTICE` (tanpa `LICENSE`/`COPYING`) | `git ls-tree --name-only HEAD \| grep -iE "licen\|copying"` → kosong |
| 5 | Metadata plasmoid Plasma | `"License": "MIT"` pada keenam plasmoid | [`plasma/*/metadata.json:14`](../plasma) |
| 6 | Font Doto | SIL Open Font License 1.1, teks lisensi ikut di repo | [`design/fonts/OFL.txt`](../design/fonts/OFL.txt), [`NOTICE:20-31`](../NOTICE) |
| 7 | `packaging` Gradle | Membuang `META-INF/{AL2.0,LGPL2.1}` dari APK | [`build.gradle.kts:52`](../android/app/build.gradle.kts) |
| 8 | CI | Tidak ada langkah pemeriksaan lisensi | [`.github/workflows/ci.yml`](../.github/workflows/ci.yml) |

Dua tempat lain di PRD bergantung pada keputusan ini dan saat ini menyatakan hal yang
belum terjadi:

- [`PRD-Dot.md:568`](PRD-Dot.md) — §16 Pertanyaan Terbuka butir 4: "Lisensi
  GPL-2.0-or-later diterapkan saat repo dibuka, setelah lisensi dependensi dicek."
- [`PRD-Dot.md:611`](PRD-Dot.md) — kriteria selesai Fase 4: "Lisensi semua dependensi
  sudah dicek, sehingga repo siap dipertimbangkan untuk dibuka."

### 2.1 Konflik yang perlu diputuskan

1. **PRD vs NOTICE** menyebut lisensi yang berbeda untuk kode yang sama.
2. **NOTICE sudah dipakai sebagai teks hukum yang dilihat pengguna** (fakta #3), padahal
   PRD menyebut NOTICE hanya untuk catatan aset pihak ketiga ([`PRD-Dot.md:452`](PRD-Dot.md)).
3. **Plasma menyatakan MIT** sementara proyek utama diklaim GPL — satu repo, dua lisensi,
   tanpa berkas yang menjelaskan pembagiannya.
4. **Tidak ada `LICENSE`** yang bisa dirujuk teks mana pun.

### 2.2 Eksposur yang sudah berjalan (diperiksa 22 Okt 2026, 23:35 WIB)

Klaim lisensi **sudah publik**. Menahan merge tidak menariknya kembali — ini dasar amandemen §4.

| Yang sudah terbuka | Bukti | Sejak |
|---|---|---|
| Repo publik | `curl -s https://api.github.com/repos/areifzyp/dot` → `"private": false`, `"visibility": "public"` | `created_at` `2026-10-18T21:22:52Z` |
| `NOTICE` (klaim Apache-2.0) terbaca siapa pun | `curl -s -o /dev/null -w '%{http_code}' .../areifzyp/dot/main/NOTICE` → `200` | 18 Okt 2026 (klaim ikut terbuka saat repo publik) |
| `LICENSE` tetap tidak ada | URL yang sama untuk `/main/LICENSE` → `404` | — |
| APK debug artefak CI dapat diunduh **oleh akun GitHub yang login** (anonim → `401`) | `GET /repos/areifzyp/dot/actions/artifacts` → `dot-debug` id `12437095872`, 11.087.580 byte, dibuat `2026-10-22T15:18:38Z`, kedaluwarsa `2027-01-20T15:15:00Z`; `GET …/artifacts/12437095872/zip` → `401` tanpa token, `200` dengan token | 22 Okt 2026 22:18 WIB |
| GitHub Release | `GET /repos/areifzyp/dot/releases` → `[]` (0 rilis) | belum pernah ada |
| Tag | `GET /repos/areifzyp/dot/tags` → `v0.2.0` (tag tanpa aset) | sebelum amandemen |

> **Status saluran artefak — diperiksa ulang 23 Okt 2026, 00:33 WIB.** Tiga hal yang
> mengubah gambaran "satu artefak, kedaluwarsa 20 Jan 2027":
>
> 1. **Unduhan butuh login.** `…/artifacts/12437095872/zip` → `401` tanpa token, `200`
>    dengan token. Artefak ini tidak terbuka untuk anonim seperti berkas `raw`.
> 2. **Step pembuatnya masih ada di `main`.** `git show origin/main:.github/workflows/ci.yml`
>    baris 49–53 masih memuat `Upload debug APK`, dan workflow berjalan pada `push` ke
>    `main` maupun setiap `pull_request`. Menghapus artefak hari ini **tidak** menutup
>    salurannya — step itulah yang harus dicabut (ada di PR #4, belum merge).
> 3. **Namun saat ini tidak ada artefak baru yang lahir.** Dari 21 run yang tercatat, 20
>    berakhir `failure` dan hanya `a1cde36` (PR #3) yang `success`. Penyebabnya bukan kode:
>    langkah `Setup Android SDK` menulis `packages:` sebagai blok YAML, sedangkan
>    `android-actions/setup-android` memecah input hanya pada spasi tunggal, sehingga
>    `sdkmanager` menerima satu nama paket rusak dan keluar `1` —
>    `Failed to find package 'platforms;android-26`. PR #3 sudah memuat perbaikan
>    satu-barisnya, jadi **saluran artefak terbuka kembali begitu ada run yang sukses**,
>    termasuk bila PR #3 di-merge.
>
> Kesimpulan: "tepat satu artefak" adalah keadaan sesaat, bukan sifat sistem, dan tanggal
> 20 Jan 2027 hanya berlaku untuk artefak yang sudah ada.

> **Batas kepastian:** API publik hanya membuktikan keadaan **sekarang** + `created_at`.
> Tanggal repo berubah dari privat ke publik tidak dapat diaudit tanpa log admin; yang bisa
> dikatakan: repo berstatus publik dan dibuat 18 Okt 2026. Bila ada bukti lain soal tanggal,
> ganti baris ini beserta buktinya.

---

## 3. Inventaris lisensi dependensi (terverifikasi)

Dihasilkan dari graf dependensi yang benar-benar dipakai build, bukan dari daftar di
dokumentasi:

```bash
cd android
JAVA_HOME=~/jdk/jdk-21.0.12.1+1 ./gradlew --offline :app:dependencies \
  --configuration releaseRuntimeClasspath
```

Setiap koordinat hasil resolusi dicocokkan dengan blok `<licenses>` pada POM artefak
tersebut di cache Gradle lokal. Daftar lengkap ada di
[`license-inventory/`](license-inventory/README.md).

Angka di §3.1–§3.2 masih berlaku pada commit `27d5917`: tidak ada berkas build yang
berubah sejak inventaris dibuat, jadi graf dependensinya identik.

```bash
git diff --name-only e1c7f9c..27d5917 -- \
  android/app/build.gradle.kts android/build.gradle.kts android/settings.gradle.kts \
  android/gradle.properties android/gradle/wrapper/gradle-wrapper.properties
# → kosong
```

Bila daftar dependensi berubah, inventaris **wajib** dibuat ulang lebih dulu — lihat
[`license-inventory/README.md`](license-inventory/README.md).

### 3.1 Yang ikut terdistribusi di APK (`releaseRuntimeClasspath`)

**103 artefak** total:

| Lisensi yang dinyatakan POM | Jumlah artefak |
|---|---|
| Apache-2.0 | 101 |
| BSD-3-Clause | 1 |
| Tanpa pernyataan lisensi di POM | 1 |

Artefak yang bukan Apache-2.0 dan karena itu perlu perhatian khusus:

| Artefak | Versi | Lisensi di POM |
|---|---|---|
| `androidx.glance:glance-appwidget-external-protobuf` | 1.1.0 | BSD-3-Clause |
| `com.google.guava:listenablefuture` | 1.0 | **tidak dinyatakan** |

### 3.2 Yang hanya dipakai tes (`releaseUnitTestRuntimeClasspath`)

**141 artefak.** Tidak ikut ke APK release karena masuk konfigurasi `testImplementation`
dan `androidTestImplementation` ([`build.gradle.kts:83-90`](../android/app/build.gradle.kts)),
tetapi tetap tersebar bersama repo — dan repo sudah publik sejak 18 Okt 2026 (§2.2).

| Lisensi | Jumlah | Contoh |
|---|---|---|
| Apache-2.0 | 113 | AndroidX Test, Espresso |
| MIT | 13 | Robolectric, checker-qual |
| BSD-3-Clause | 6 | `org.ow2.asm:*` |
| Tanpa pernyataan lisensi di POM | 6 | `com.google.guava:guava:31.1-jre`, `com.ibm.icu:icu4j:75.1`, `org.hamcrest:hamcrest-core:1.3`, `com.google.auto.value:auto-value-annotations:1.11.0`, `com.google.guava:failureaccess:1.0.1`, `com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava` |
| Eclipse Public License 1.0 | 1 | `junit:junit:4.13.2` |
| CDDL + GPLv2 with classpath exception | 1 | `javax.annotation:javax.annotation-api:1.3.2` |
| Bouncy Castle Licence | 1 | `org.bouncycastle:bcprov-jdk18on:1.78.1` |

Catatan: tidak ada dependensi berlisensi GPL murni di kedua graf. Satu entri
`CDDL + GPLv2 with classpath exception` adalah dependensi tes, bukan bagian APK.

---

## 4. Apa yang ditahan sampai ada keputusan

**Lingkup yang berlaku: amandemen PM 22 Oktober 2026 (butir S4).** Versi sebelumnya menahan
"tidak merge ke `main`" dan "membuka repo menjadi publik". Yang pertama dipersempit ke klaim
lisensi, yang kedua sudah tidak relevan — lihat §2.2.

| Aktivitas | Alasan ditahan | ID | Status |
|---|---|---|---|
| Membuat berkas `LICENSE` | Isinya bergantung pada D1; salah pilih berarti teks hukum yang keliru | D1 | **ditahan** |
| Merge ke `main` yang **menyentuh klaim lisensi** — `NOTICE`, PRD §10/§16.4, `plasma/*/metadata.json`, teks/URL tombol Lisensi, `LICENSE` | Setiap perubahan di berkas itu adalah pernyataan lisensi baru yang langsung publik | **S1** | **ditahan** |
| Tag, kenaikan `VERSION`, GitHub Release | Menandai rilis = mengumumkan versi yang klaim lisensinya belum final | **S2** | **ditahan** |
| CI `main` mendistribusikan APK (`actions/upload-artifact`) | Artefak repo publik dapat diunduh akun GitHub mana pun; **sebelum dicabut**, setiap push ke `main` dan setiap PR menghasilkan satu APK | **S3** | **dicabut** 22 Okt 2026 (`ab5d4ff`) |
| Membagikan APK ke orang lain (unduhan manual, chat, toko) | Distribusi memicu kewajiban lisensi (PRD §10 sendiri menyebut momen ini) | D1, D2 | **ditahan** |
| Pengajuan ke F-Droid / Play Store | Keduanya meminta pernyataan lisensi yang konsisten dengan kode | D1 | **ditahan** |
| Publikasi plasmoid ke KDE Store | `metadata.json` menyatakan MIT; harus cocok dengan keputusan D3 | D3 | **ditahan** |
| Menulis ulang teks lisensi di Tentang Aplikasi (SET-2) | String dan URL tujuan mengikuti sumber kebenaran yang baru | D1 | **ditahan** |
| Pengembangan kode, tes, CI non-distribusi, dokumentasi, dan merge yang tidak menyentuh klaim lisensi | Tidak bergantung pada lisensi | — | berjalan |

**Tidak lagi ditahan — sudah terjadi:** membuka repo menjadi publik. Repo publik sejak
18 Oktober 2026, `NOTICE` Apache-2.0 terbaca, `LICENSE` `404` (§2.2). Blokir merge tidak
menahan eksposur itu; risiko bersumber dari **klaim**, bukan dari kode fitur.

> **Keputusan PM (22 Okt 2026) — amandemen butir #4 rapat:** penahanan "tidak merge ke main"
> dipersempit menjadi penahanan aktivitas rilis/distribusi + merge yang menyentuh klaim
> lisensi (REQUEST §4, S1–S3). Alasan: repo sudah publik sejak 18 Okt 2026 (`private:false`,
> `NOTICE` 200, `LICENSE` 404), sehingga blokir merge tidak menahan eksposur; risiko
> bersumber dari klaim, bukan dari kode fitur. Wewenang membalik: operator.

> **Sisa eksposur yang belum dicabut (S3):** step `Upload debug APK` sudah dicabut (`ab5d4ff`,
> 22 Okt 2026), jadi tidak ada artefak **baru** — jumlahnya statis di satu, bukan bertambah per
> run CI. Artefak `dot-debug` yang sudah ada (id `12437095872`, 11.087.580 byte, kedaluwarsa
> 20 Jan 2027) tetap dapat diunduh sampai dihapus eksplisit. Penghapusan adalah tindakan
> distribusi terbalik → wewenang operator, dilacak di kartu `t_40fc5231`:
> `gh api -X DELETE repos/areifzyp/dot/actions/artifacts/12437095872`.

---

## 5. Opsi dan konsekuensi teknisnya

Fakta di bawah terverifikasi dari repo. Penilaian hukumnya **tidak** ada di sini — itu
isi §6.

| Opsi | Konsisten dengan | Konsekuensi teknis terverifikasi | Bertentangan dengan |
|---|---|---|---|
| **A. `GPL-2.0-or-later`** | PRD §10, §16.4 | 101 dari 103 artefak APK berlisensi Apache-2.0; PRD memilih varian *or-later* justru karena Apache-2.0 tidak cocok dengan GPL-2.0-*only* | `NOTICE`, tombol Lisensi di aplikasi, `metadata.json` plasmoid |
| **B. `Apache-2.0`** | `NOTICE`, seluruh 101 dependensi inti | Tidak ada perubahan pada teks yang sudah dilihat pengguna; perlu `LICENSE` baru berisi teks Apache-2.0 | PRD §10 dan §16.4 harus diubah; plasmoid MIT perlu penjelasan |
| **C. `MIT`** | `metadata.json` keenam plasmoid | Repo jadi satu lisensi paling permisif; `NOTICE` tetap menyebut Apache-2.0 sehingga tetap harus diubah | PRD §10; kesan "copyleft" yang jadi alasan pemilihan GPL di PRD hilang |

Opsi lain (mis. GPL-3.0-only, dual-license, lisensi berbeda per folder) belum punya
jejak di repo dan tidak diinventaris di sini.

---

## 6. Pertanyaan untuk tim hukum/kepatuhan

Jawaban tertulis atas pertanyaan berikut menutup kartu ini.

1. **Q1 — D1:** Lisensi mana yang berlaku untuk kode Dot. (Android + Plasma)?
2. **Q2:** Bila `GPL-2.0-or-later` dipilih, apakah cara pemilihan versi ("or later")
   harus dinyatakan eksplisit di `LICENSE` dan/atau header berkas sumber, dan apakah
   cukup satu berkas `LICENSE` di akar repo?
3. **Q3:** Apakah 101 dependensi Apache-2.0 di APK menimbulkan kewajiban tambahan
   (mis. menyertakan teks lisensi pihak ketiga di aplikasi) — dan apakah `NOTICE` yang
   sekarang sudah memenuhi itu?
4. **Q4:** `androidx.glance:glance-appwidget-external-protobuf:1.1.0` berlisensi
   BSD-3-Clause dan `com.google.guava:listenablefuture:1.0` tidak menyatakan lisensi di
   POM-nya. Apakah keduanya perlu tindakan khusus?
5. **Q5 — D3:** Bolehkah enam plasmoid tetap `MIT` di dalam repo yang lisensi utamanya
   GPL/Apache? Bila boleh, di berkas mana pembagian itu harus dinyatakan?
6. **Q6:** Font Doto (SIL OFL 1.1) disertakan di APK dan di repo. Apakah ketentuan
   *Reserved Font Name* terpicu bila kelak dibuat instance statis dari font variabel
   (skenario yang sudah dicatat di [`PRD-Dot.md:354`](PRD-Dot.md))?
7. **Q7 — D2:** Apakah lisensi perlu ditetapkan sekarang, atau boleh ditunda sampai repo
   dibuka/APK dibagikan seperti tertulis di PRD §10?
8. **Q8:** Apakah pemakaian nama "Nothing" dan tagline "Inspired by Nothing" di judul,
   README, dan `NOTICE` perlu ditinjau terpisah sebelum publikasi? (Risiko ini sudah
   tercatat di [`PRD-Dot.md:543`](PRD-Dot.md); di sini hanya untuk memastikan jalurnya
   tidak tertinggal.)
9. **Q9 — cakupan penggunaan:** Apakah keputusan berlaku untuk penggunaan internal,
   komersial (mis. versi berbayar atau beriklan), dan redistribusi APK lewat toko
   aplikasi? Bila ada yang **tidak** diizinkan, sebutkan yang mana.
10. **Q10 — modifikasi, sublicensing, redistribusi source:** Bolehkah pihak lain
    memodifikasi dan mendistribusikan ulang kode (fork, build komunitas)? Apakah
    sublicensing diizinkan? Apakah source wajib disertakan saat APK dibagikan?
11. **Q11 — bentuk bukti keputusan:** Apa bentuk bukti yang sah untuk menutup item ini:
    komentar di kartu `t_6cc84cef`, email, atau commit yang menerapkan `LICENSE`? Siapa
    yang berwenang menyatakan keputusan itu final?
12. **Q12 — penanda "lisensi belum final":** Apakah menambahkan penanda "lisensi belum final"
    di `README`/`NOTICE` **mengurangi** risiko (pembaca tahu klaim belum final) atau justru
    **menambah** klaim baru (pernyataan lisensi tambahan di berkas yang dibaca publik)?
    Konteks: `NOTICE` adalah berkas yang dibuka tombol "Lisensi" di aplikasi (fakta #3), jadi
    setiap penanda di sana adalah teks yang dilihat pengguna, bukan catatan internal.
    Perubahan itu sendiri **ditahan** oleh S1 sampai Q12 dijawab.

---

## 7. Berkas yang harus berubah setelah keputusan

Checklist ini yang membuat keputusan benar-benar berlaku. Belum dikerjakan — menunggu §6.

| Berkas | Perubahan yang diperlukan |
|---|---|
| `LICENSE` (baru, akar repo) | Teks lisensi penuh sesuai D1 |
| [`NOTICE:8-12`](../NOTICE) | Samakan dengan D1 |
| [`docs/PRD-Dot.md:451`](PRD-Dot.md) | §10 butir lisensi: hapus "keputusan pemilik"/"diterapkan nanti" bila D2 = sekarang. **Premis baris ini sudah salah**: teks PRD masih menulis "tidak diperlukan selama repo private" dan "dicek sebelum repo dibuka", padahal repo sudah publik 18 Okt 2026. **Ditahan S1** sampai D1/D2 keluar |
| [`docs/PRD-Dot.md:568`](PRD-Dot.md) | §16.4: tutup pertanyaan terbuka |
| [`docs/PRD-Dot.md:611`](PRD-Dot.md) | Kriteria selesai Fase 4: ganti "sudah dicek" dengan bukti |
| [`SettingsScreen.kt:137`](../android/app/src/main/java/io/github/areifzyp/dot/settings/SettingsScreen.kt) | URL tombol Lisensi: arahkan ke `LICENSE` bila berkas itu ada |
| [`strings.xml:86`](../android/app/src/main/res/values/strings.xml) | Label "Lisensi" — cek juga `values-in` bila ada terjemahan |
| [`plasma/*/metadata.json:14`](../plasma) | Field `License` untuk 6 plasmoid, sesuai D3 |
| [`AGENTS.md`](../AGENTS.md) §Conventions | Aturan: lisensi dependensi baru dicek sebelum digabung (PRD §11.7) |
| [`docs/README.md`](README.md) | Tambahkan aturan lisensi ke indeks |

> **Catatan hotspot:** `docs/PRD-Dot.md` sedang disentuh beberapa kartu lain pada saat
> dokumen ini dibuat. Empat baris PRD di atas **sengaja tidak diubah** di sini agar tidak
> bertabrakan; perubahan PRD dikerjakan setelah keputusan keluar.

---

## 8. Definisi selesai untuk kartu ini

Kartu "sampai ada keputusan lisensi dari tim hukum/kepatuhan" selesai bila:

1. D1, D2, D3 terjawab tertulis oleh tim hukum/kepatuhan (§6, Q1–Q12), dan
   [`LICENSE-DECISION-RECORD.md` §3](LICENSE-DECISION-RECORD.md) terisi.
2. Satu sumber kebenaran lisensi ditunjuk (PRD, `LICENSE`, atau `NOTICE`).
3. Checklist §7 punya pemilik dan kartu tindak lanjut.

Sampai ketiganya ada, kartu tetap terbuka dan §4 tetap ditahan. Status dapat diperiksa
kapan saja tanpa jaringan:

```bash
bash scripts/license-status.sh          # exit 0 = selesai, exit 1 = masih menunggu
bash scripts/license-status.sh --quiet  # ringkas, untuk cron
```

---

## 9. Cara memverifikasi ulang dokumen ini

```bash
# 1. Klaim lisensi yang saling bertentangan
grep -n "GPL-2.0-or-later" docs/PRD-Dot.md
grep -n "Apache License" NOTICE

# 2. Tidak ada berkas LICENSE di akar repo
git ls-tree --name-only HEAD | grep -iE "licen|copying"   # → kosong; hanya NOTICE

# 3. Lisensi plasmoid
grep -n '"License"' plasma/*/metadata.json

# 4. Sumber yang dilihat pengguna di aplikasi
grep -rn "settings_about_license" android/app/src/main

# 5. Inventaris dependensi (butuh JDK 21, boleh offline bila cache sudah terisi)
cd android
JAVA_HOME=~/jdk/jdk-21.0.12.1+1 ./gradlew --offline :app:dependencies \
  --configuration releaseRuntimeClasspath
JAVA_HOME=~/jdk/jdk-21.0.12.1+1 ./gradlew --offline :app:dependencies \
  --configuration releaseUnitTestRuntimeClasspath

# 6. Status publik repo + eksposur APK (§2.2)
curl -s https://api.github.com/repos/areifzyp/dot | grep -E '"(private|visibility|created_at)"'
curl -s -o /dev/null -w '%{http_code}\n' https://raw.githubusercontent.com/areifzyp/dot/main/NOTICE
curl -s -o /dev/null -w '%{http_code}\n' https://raw.githubusercontent.com/areifzyp/dot/main/LICENSE
curl -s https://api.github.com/repos/areifzyp/dot/actions/artifacts | grep -E '"(name|expires_at)"'
curl -s https://api.github.com/repos/areifzyp/dot/releases
```

---

## 10. Celah yang belum bisa ditutup di sini

Ditandai jujur agar tidak dianggap sudah beres:

- **Lisensi enam artefak tes tidak dinyatakan di POM-nya** (§3.2). Kebenaran upstream
  belum diperiksa: `com.google.guava:guava:31.1-jre`, `com.google.guava:failureaccess:1.0.1`,
  `com.google.guava:listenablefuture:1.0`, `com.ibm.icu:icu4j:75.1`,
  `org.hamcrest:hamcrest-core:1.3`, `com.google.auto.value:auto-value-annotations:1.11.0`.
  Bisa jadi lisensi dideklarasikan di POM induk — perlu konfirmasi, bukan asumsi.
- **Kebijakan F-Droid dan Play Store** tidak dicek ulang terhadap versi terkini; PRD §10
  hanya mencatat syarat umum.
- **Posisi merek "Nothing"/Ndot** (Q8) adalah jalur terpisah dan tidak diselesaikan di
  dokumen ini.
- **Tidak ada pemeriksaan lisensi di CI** (fakta #8). Menambahkannya masuk akal setelah
  D1 keluar, karena alat pemeriksa perlu dikonfigurasi dengan daftar lisensi yang
  diizinkan.
- **Artefak APK yang sudah terunggah tidak hilang** saat step CI dicabut (S3): artefak
  `dot-debug` id `12437095872` tetap dapat diunduh sampai dihapus eksplisit atau kedaluwarsa
  20 Jan 2027 (§2.2).
- **Tanggal repo menjadi publik tidak dapat diaudit** dari API publik; yang terbukti hanya
  status publik sekarang dan `created_at` 18 Okt 2026 (§2.2).

---

## 11. Riwayat pemeriksaan

Graf dependensi tidak dijaga CI, jadi setiap pemeriksaan ulang dicatat di sini — termasuk
pemeriksaan yang hasilnya "belum ada keputusan", supaya tidak ada yang menyimpulkan paket
ini sudah kedaluwarsa atau sudah terjawab.

| Waktu (WIB) | Commit | Hasil |
|---|---|---|
| 22 Okt 2026 ~20:35 | `e1c7f9c` | Paket keputusan disusun; inventaris dependensi dibuat dari graf resolusi Gradle |
| 22 Okt 2026 22:41 | `27d5917` | Semua fakta §2 diperiksa ulang dengan perintah §9 — hasil identik dengan `e1c7f9c`. Berkas build tidak berubah, jadi §3.1–§3.2 tetap berlaku. **D1–D3 belum terjawab.** |
| 22 Okt 2026 23:35 | commit ini | **Amandemen butir #4 rapat (S1–S4) diterapkan.** §4 ditulis ulang: "membuka repo" dihapus dari daftar ditahan (sudah terjadi, §2.2), penahanan merge dipersempit ke berkas klaim lisensi (S1), tag/VERSION/Release (S2), CI distribusi APK (S3); teks keputusan PM ditempel verbatim. Premis D2 diperbaiki (opsi "saat repo dibuka" gugur). Q12 (penanda "lisensi belum final") ditambahkan ke §6. §2.2 baru: eksposur publik + artefak APK `dot-debug` yang masih dapat diunduh. §9 perintah verifikasi #6, §10 dua celah baru. **D1–D3 tetap belum terjawab.** |
| 22 Okt 2026 23:44 | commit ini | Perbaikan rujukan usang: §8 butir 1 menyebut "Q1–Q8" padahal §6 sudah memuat Q1–Q12 sejak amandemen 23:35. Tidak ada perubahan fakta; D1–D3 tetap belum terjawab. |
| 23 Okt 2026 00:33 | commit ini | **§2.2 diperiksa ulang atas laporan peninjau.** Tiga fakta baru: (a) unduhan artefak butuh login (`401` anonim / `200` bertoken); (b) step `Upload debug APK` masih ada di `main` (baris 49–53), jadi menghapus artefak tidak menutup salurannya; (c) 20 dari 21 run gagal di `Setup Android SDK` (`packages:` blok YAML → `sdkmanager` exit 1), satu-satunya sukses `a1cde36` (PR #3) — saluran artefak terbuka lagi begitu ada run sukses. Tidak ada perubahan pada D1–D3; **D1–D3 tetap belum terjawab.** |

---

*Dokumen ini menggantikan dugaan dengan bukti. Bila ada baris di sini yang tidak cocok
lagi dengan repo, perbaiki baris itu beserta buktinya — jangan tambahkan klaim baru tanpa
perintah verifikasi di §9.*
