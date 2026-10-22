# Daftar Pertanyaan Eksplisit — Keputusan Lisensi Dot.

**Untuk:** tim hukum/kepatuhan · **Dari:** doc-writer (pengusul) · **Dibuat:** 23 Okt 2026

Tujuan berkas ini: setiap pertanyaan di [`LICENSE-DECISION-REQUEST.md` §6](LICENSE-DECISION-REQUEST.md)
dirumuskan ulang sebagai pertanyaan **tertutup** (pilihan tegas, bukan uraian bebas), supaya
jawaban yang diterima langsung dapat dipakai menulis `LICENSE` tanpa tanya-jawab berulang.
Berkas ini melengkapi §6, bukan menggantikannya: **daftar kanonik tetap §6** dan tabel ringkas
di [`LICENSE-DECISION-RECORD.md` §4](LICENSE-DECISION-RECORD.md).

## Cara memakai

1. Jawab per butir dengan **memilih satu opsi** (atau menulis `lain: …`). Jawaban naratif bebas
   tidak dipakai — lihat uji ambiguitas di [RECORD §4.1](LICENSE-DECISION-RECORD.md).
2. Bila satu butir memang belum bisa diputuskan, tulis **"belum diputuskan"**. Itu tetap jawaban
   sah dan mencegah agen menebak.
3. Butir bertanda **`[KLARIFIKASI PENGAJU]`** belum boleh dikirim apa adanya: pengusul harus
   melengkapi satu fakta/niat dulu (disebut di butirnya). Kirim sisanya lebih dulu bila perlu.
4. Yang menahan rilis hanya **Q1, Q2, Q5** (= D1–D3 di [REQUEST §8](LICENSE-DECISION-REQUEST.md)).
   Sisanya wajib dijawab sebelum `LICENSE`/`NOTICE` ditulis, tetapi tidak memblokir pekerjaan kode.

**Asal butir:** Q1–Q12 adalah rumusan tertutup dari §6 (ID sama, isi sama, hanya bentuknya
dipertegas). **Q13–Q18 adalah butir baru** yang lahir dari audit 23 Okt 2026 dan **belum ada di §6** —
jangan dikirim sebagai bagian paket resmi sebelum §6 dan RECORD §4 diperbarui (lihat catatan di
akhir berkas).

---

## A. Keputusan inti

### Q1 — Lisensi mana yang berlaku untuk kode Dot. (Android + Plasma)?

**Pilih satu:** `GPL-2.0-or-later` · `GPL-3.0-or-later` · `Apache-2.0` · `MIT` · `lain: …`

**Mengapa perlu:** ini satu-satunya butir yang menahan pembuatan berkas `LICENSE` (REQUEST §4).
Sampai dijawab, repo publik tidak punya `LICENSE` (HTTP 404), sementara tiga sumber yang sudah
terbaca publik menyatakan hal berbeda: `NOTICE` memuat teks Apache-2.0 penuh, `docs/PRD-Dot.md:451`
menulis keputusan pemilik `GPL-2.0-or-later`, dan enam `plasma/*/metadata.json` menulis `"License": "MIT"`.
Tanpa ID lisensi yang pasti, setiap perubahan klaim lisensi tetap ditahan (S1) dan tidak ada teks
sah untuk ditempel ke `LICENSE`.

### Q2 — Bila opsi "or later" dipilih, bagaimana bentuk penerapannya?

**Pilih satu:**
(a) cukup satu `LICENSE` di akar repo dengan klausa "or later"
(b) `LICENSE` + header SPDX di setiap berkas sumber
(c) keduanya
(d) `lain: …`

**Mengapa perlu:** pilihan lisensi bisa benar tetapi penerapannya salah. Butir ini menentukan
apakah pekerjaan penerapan menyentuh ratusan berkas sumber (b/c) atau hanya satu berkas (a) —
dan itu menentukan besar pekerjaan di kartu penutupan (`t_cecf5a53`).

### Q3 — Apakah 101 dependensi Apache-2.0 di APK menimbulkan kewajiban tambahan, dan apakah `NOTICE` sekarang sudah memenuhinya?

**Pilih satu:**
(a) `NOTICE` yang sekarang sudah cukup
(b) perlu teks lisensi pihak ketiga **di dalam aplikasi** (layar Lisensi)
(c) perlu berkas `NOTICE` ikut di dalam paket APK
(d) `lain: …`

**Mengapa perlu:** `android/app/build.gradle.kts:45-58` mengecualikan
`/META-INF/{AL2.0,LGPL2.1}` dari paket, sehingga berkas lisensi bawaan artefak tidak ikut ke APK.
Tombol "Lisensi" di aplikasi hanya membuka URL GitHub (`SettingsScreen.kt:137`), bukan teks di
dalam aplikasi. Bila jawabannya (b) atau (c), ada pekerjaan aplikasi yang belum tercatat di
checklist mana pun.

### Q4 — `glance-appwidget-external-protobuf:1.1.0` (BSD-3-Clause) dan `com.google.guava:listenablefuture:1.0` (POM tanpa lisensi) perlu tindakan khusus?

**Pilih satu:** (a) keduanya perlu tindakan · (b) hanya `listenablefuture` · (c) tidak perlu ·
(d) `lain: …`

**Mengapa perlu:** ini dua-satunya artefak runtime non-Apache dari 103 baris inventaris
(`docs/license-inventory/android-release-runtime.tsv`), dan `listenablefuture` tidak menyatakan
lisensi apa pun di POM-nya — dasar hukum redistribusinya belum ada. Keduanya ikut di APK, jadi
jawabannya menentukan apakah `LICENSE`/`NOTICE` harus memuat atribusi tambahan.

### Q5 — Enam plasmoid Plasma tetap `MIT`, atau disamakan dengan Q1?

**Pilih satu:** (a) tetap `MIT` · (b) disamakan dengan Q1 · (c) `lain: …`
**Bila (a), pembagian itu dinyatakan di mana?** `LICENSE` per direktori · satu `LICENSE` + tabel di `README` · hanya field `License` di `metadata.json` · `lain: …`

**Mengapa perlu:** enam `plasma/*/metadata.json` menyatakan MIT tanpa satu pun berkas yang
menjelaskan pembagiannya. Ini menahan publikasi ke KDE Store (REQUEST §4), dan tanpa jawaban
`metadata.json` bisa bertentangan dengan `LICENSE` induk.

---

## B. Cakupan dan hak pakai

### Q6 — Font Doto (SIL OFL 1.1): apakah instance statis boleh tetap memakai nama "Doto"?

**Pilih satu:** (a) boleh tanpa syarat · (b) wajib ganti nama saat instance dibuat ·
(c) perlu tinjauan · (d) `lain: …`

**Mengapa perlu:** `design/fonts/OFL.txt` baris 1 berbunyi
`Copyright 2024 The Doto Project Authors (https://github.com/oliverlalan/Doto)` **tanpa** frasa
"with Reserved Font Name", jadi secara teks tidak ada RFN yang dideklarasikan. Skenario membuat
instance statis sudah tercatat di `docs/PRD-Dot.md:354`; saat ini kode belum memakai axis variabel
(tidak ada `FontVariation`/`wght`/`ROND`), sehingga butir ini mencegah langkah berikutnya salah
sejak awal.

### Q7 — Lisensi ditetapkan sekarang, atau boleh ditunda sampai APK pertama dibagikan?

**Pilih satu:** (a) sekarang · (b) saat APK pertama dibagikan · (c) `lain: …`

**Mengapa perlu:** opsi "saat repo dibuka" sudah gugur — repo publik sejak 18 Okt 2026 dan `NOTICE`
Apache-2.0 sudah terbaca publik. Selama butir ini kosong, gerbang `bash scripts/license-status.sh`
keluar dengan exit `1`, tag/rilis ditahan (S2), dan PRD `:451` masih memuat premis yang salah
("tidak diperlukan selama repo private").

### Q8 — Pemakaian nama "Nothing" dan tagline "Inspired by Nothing" perlu ditinjau terpisah?

**Pilih satu:** (a) perlu tinjauan sebelum publikasi · (b) aman, tidak perlu · (c) perlu ubah penamaan · (d) `lain: …`

**Mengapa perlu:** risiko ini sudah tercatat di `docs/PRD-Dot.md:543`, tetapi belum ada butir yang
memastikan jalurnya tidak tertinggal saat publikasi. Teksnya ada di judul, `README`, dan `NOTICE`.

### Q9 — Cakupan penggunaan mana yang diizinkan? `[KLARIFIKASI PENGAJU]`

**Jawab tiap baris** (`ya` / `tidak` / `belum diputuskan`):

| # | Cakupan | Jawaban |
|---|---|---|
| a | Internal saja (tanpa distribusi) | |
| b | Komersial: versi berbayar | |
| c | Komersial: iklan / monetisasi tidak langsung | |
| d | Redistribusi APK (toko aplikasi, unduhan langsung) | |
| e | Redistribusi source code (fork publik) | |

**Mengapa perlu:** batas yang tidak pernah ditulis tetap berlaku sebagai risiko rilis. Butir ini
yang menentukan isi `README` + `LICENSE`, dan menentukan apakah pengajuan ke F-Droid/Play Store
boleh dilanjutkan.
**Klarifikasi yang dibutuhkan pengusul:** niat distribusi pemilik (Play Store? berbayar? iklan?)
belum pernah ditulis di repo — tanpa itu pilihan (b)–(d) tidak bisa diusulkan secara jujur.

### Q10 — Modifikasi, sublicensing, dan kewajiban source saat APK dibagikan `[KLARIFIKASI PENGAJU]`

**Jawab tiap baris:**

| # | Pertanyaan | Jawaban |
|---|---|---|
| a | Pihak lain boleh memodifikasi & mendistribusikan ulang (fork, build komunitas)? | ya / tidak / bersyarat |
| b | Sublicensing diizinkan? | ya / tidak |
| c | Source wajib disertakan saat APK dibagikan? | ya / tidak / cukup tautan |

**Mengapa perlu:** (a) menentukan apakah perlu pernyataan tambahan di `README` — fork sudah mungkin
sekarang karena repo publik; (b) menentukan apakah `LICENSE` perlu klausa di luar teks baku;
(c) adalah kewajiban redistribusi yang paling mudah terlewat saat APK keluar dan menentukan isi
halaman rilis/toko.
**Klarifikasi yang dibutuhkan pengusul:** sikap pemilik terhadap fork komunitas belum pernah
dinyatakan; pilihan (a) tidak bisa diusulkan tanpa itu.

---

## C. Kompatibilitas lisensi

### Q11 — Apakah `GPL-2.0-or-later` (pilihan pemilik di PRD) sah dipakai bersama 101 dependensi Apache-2.0 di APK? `[KLARIFIKASI PENGAJU]`

**Pilih satu:**
(a) `GPL-2.0-or-later` sah — lanjutkan
(b) hanya `GPL-3.0-or-later` yang sah — batalkan pilihan pemilik di PRD
(c) perlu langkah tambahan (mis. dual-license, exception) — sebutkan
(d) `lain: …`

**Mengapa perlu:** ini pertanyaan kompatibilitas yang sebenarnya, dan ia belum ada di §6. Apache-2.0
kompatibel dengan GPL-3.0 tetapi **tidak** dengan GPL-2.0-only; karena itu opsi "or later" menentukan
apakah pilihan pemilik bisa dieksekusi. Jawaban (b) atau (c) berarti Q1 harus dijawab ulang, jadi
butir ini menahan pembuatan `LICENSE` bersama Q1.
**Klarifikasi yang dibutuhkan pengusul:** konfirmasi apakah `GPL-2.0-or-later` adalah preferensi
yang bisa dinegosiasikan atau harga mati. Tanpa itu, opsi (b) tidak bisa ditawarkan sebagai jalan keluar.

### Q12 — Dependensi yang hanya dipakai tes (`EPL-1.0`, BouncyCastle, ASM, `javax.annotation`) cukup dicatat, atau perlu tindakan?

**Pilih satu:** (a) cukup dicatat, tidak terdistribusi · (b) perlu tindakan · (c) `lain: …`

**Mengapa perlu:** `docs/license-inventory/android-unit-test.tsv` berisi 141 baris dan memuat
lisensi yang lebih ketat daripada runtime (termasuk EPL-1.0). Semua artefak ini ada di
`releaseUnitTestRuntimeClasspath`, bukan di APK — tetapi tanpa pernyataan tertulis, temuan audit
berikutnya akan mengangkatnya lagi. Jawaban ini juga menjadi dasar aturan "cek lisensi dependensi
baru sebelum digabung" yang harus masuk `AGENTS.md` §Conventions.

---

## D. Atribusi dan notice

### Q13 — Teks OFL font Doto harus ikut di paket mana? `[KLARIFIKASI PENGAJU]`

**Pilih satu:**
(a) cukup `design/fonts/OFL.txt` di repo
(b) wajib disertakan di dalam APK (mis. layar Lisensi / aset)
(c) wajib di APK **dan** di setiap paket plasmoid
(d) `lain: …`

**Mengapa perlu:** font benar-benar didistribusikan: tiga salinan identik (`sha256` awalan
`6f4fe7d37853b91d`) ada di `android/app/src/main/res/font/doto.ttf`, `design/fonts/Doto.ttf`, dan
`plasma/org.dot.*/contents/fonts/Doto.ttf`, dan font ini adalah default UI/widget (`FontChoice.DOTO`).
Namun tidak ada berkas OFL di dalam `android/app/src/main` maupun di paket plasmoid — hanya di
`design/fonts/`. Bila jawabannya (b) atau (c), ada pekerjaan paket yang belum tercatat.
**Klarifikasi yang dibutuhkan pengusul:** keputusan kemasan (apakah teks lisensi ikut ke APK atau
cukup ditautkan) adalah keputusan produk, bukan hukum — perlu ditanyakan ke pemilik sebelum dikirim.

### Q14 — Bila Q1 bukan `Apache-2.0`, apa yang terjadi pada isi `NOTICE`? `[KLARIFIKASI PENGAJU]`

**Pilih satu:**
(a) ganti seluruh teks Apache-2.0 dengan teks lisensi Q1
(b) pertahankan teks Apache-2.0 sebagai lisensi pihak ketiga, tambahkan teks Q1
(c) hapus teks lisensi dari `NOTICE`, arahkan tombol "Lisensi" ke `LICENSE`
(d) `lain: …`

**Mengapa perlu:** `NOTICE` adalah berkas yang dibuka tombol "Lisensi" di aplikasi
(`SettingsScreen.kt:137`), jadi isinya adalah teks yang dibaca pengguna, bukan catatan internal.
Bila `LICENSE` nanti menyatakan GPL sementara `NOTICE` masih memuat teks Apache-2.0 penuh, pembaca
mendapat dua klaim yang bertentangan. Perubahan `NOTICE` sendiri ditahan S1 sampai butir ini dijawab.
**Klarifikasi yang dibutuhkan pengusul:** rencana isi `NOTICE` pasca-keputusan belum ada; opsi (a)–(c)
berbeda besar pekerjaannya.

### Q15 — Apakah penanda "lisensi belum final" ditambahkan di `README`/`NOTICE`?

**Pilih satu:** (a) di `README` saja · (b) di `README` dan `NOTICE` · (c) jangan sama sekali · (d) `lain: …`

**Mengapa perlu:** penanda di `NOTICE` adalah teks publik dan bisa dibaca sebagai klaim lisensi
tambahan, bukan sebagai catatan proses. Butir ini menahan satu-satunya perubahan `NOTICE` yang
diizinkan sebelum keputusan keluar.

---

## E. Kewajiban hukum lain

### Q16 — Perlu pernyataan/klausa paten terpisah?

**Pilih satu:** (a) perlu · (b) cukup lisensi yang dipilih · (c) tidak perlu · (d) `lain: …`

**Mengapa perlu:** ketiga kandidat lisensi berbeda soal paten — Apache-2.0 §3 memberi grant paten
eksplisit, GPL-2.0 tidak eksplisit, MIT tanpa grant sama sekali. Repo belum punya pernyataan paten
apa pun, sehingga eksposur paten berubah otomatis mengikuti Q1 tanpa pernah diputuskan.

### Q17 — Nama dan identitas aplikasi "Dot." + `io.github.areifzyp.dot` perlu clearance merek?

**Pilih satu:** (a) perlu clearance sebelum publikasi · (b) aman, tidak perlu · (c) perlu ubah penamaan · (d) `lain: …`

**Mengapa perlu:** `AGENTS.md` menyatakan Application ID `io.github.areifzyp.dot` **immutable**,
jadi kesalahan penamaan baru ketahuan setelah rilis berarti biaya tinggi (tidak bisa diganti tanpa
aplikasi baru). Belum ada pernyataan siapa pemegang merek "Dot." maupun penafian afiliasi.

---

## F. Format bukti keputusan

### Q18 — Bentuk bukti apa yang sah, dan siapa yang berwenang menyatakan final?

**Pilih satu (bukti):** (a) komentar di kartu `t_6cc84cef` · (b) email resmi dari alamat domain · (c) dokumen bertanda tangan · (d) commit yang menerapkan `LICENSE` oleh pemilik repo · (e) `lain: …`
**Wewenang final:** pemilik repo (`areifzyp`) · operator · tim hukum · `lain: …`

**Mengapa perlu:** `LICENSE-DECISION-RECORD.md` §2 menetapkan kapan keputusan dianggap diterima,
dan §3 adalah formulir yang harus diisi. Tanpa butir ini, item bisa ditutup dengan "bukti" yang
tidak sah — misalnya komentar orang yang bukan pemegang wewenang — dan gerbang
`bash scripts/license-status.sh` akan lolos atas dasar yang keliru.

---

## Ringkasan

| Kelompok | Butir | Menahan rilis? |
|---|---|---|
| Keputusan inti | Q1, Q2, Q5 | **ya** (D1–D3) |
| Cakupan & hak pakai | Q6, Q7, Q8, Q9, Q10 | tidak (Q7 = D2, menahan tag/rilis S2) |
| Kompatibilitas | Q11, Q12 | tidak, tetapi Q11 menahan isi `LICENSE` |
| Atribusi & notice | Q3, Q4, Q13, Q14, Q15 | tidak, tetapi menahan penerapan |
| Kewajiban hukum lain | Q16, Q17 | tidak |
| Format bukti | Q18 | tidak, tetapi menentukan sah/tidaknya penutupan |

Butir bertanda `[KLARIFIKASI PENGAJU]`: **Q9, Q10, Q11, Q13, Q14** — lima butir ini belum boleh
dikirim sebelum pengusul melengkapi fakta/niat yang disebut di masing-masing butir.

## Setelah dijawab

1. Salin jawaban ke [`LICENSE-DECISION-RECORD.md` §3](LICENSE-DECISION-RECORD.md) (formulir a–i).
2. Periksa ulang dengan uji ambiguitas [RECORD §4.1](LICENSE-DECISION-RECORD.md); bila ada yang
   belum lolos, tanyakan ulang butir itu saja.
3. Jalankan `bash scripts/license-status.sh` — harus keluar `0`, bukan `1`.

## Catatan pemeliharaan

Q13–Q18 adalah usulan baru dari audit 23 Okt 2026 dan **belum** tercantum di
[`LICENSE-DECISION-REQUEST.md` §6](LICENSE-DECISION-REQUEST.md) maupun tabel
[`LICENSE-DECISION-RECORD.md` §4](LICENSE-DECISION-RECORD.md). Kedua tempat itu adalah daftar
kanonik dan wajib saling cocok. Selama Q13–Q18 belum disalin ke sana, berkas ini berstatus
**usulan**, bukan paket resmi — dan paket yang dikirim ke tim hukum tetap harus memuat §6 versi
terbaru. Penyalinan itu dikerjakan di kartu penutupan (`t_cecf5a53`) atau kartu terpisah.
