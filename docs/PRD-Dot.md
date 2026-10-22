# PRD — Dot. (Inspired by Nothing)

Versi 1.0 · 18 Oktober 2026 · Status: HISTORIS (v1.0) — acuan keputusan

> Dokumen ini dibekukan sebagai historis: produk telah melewati Fase 1–4 dan
> mulai mengerjakan kegiatan Pasca-Fase 4 (KDE Plasma). PRD ini bukan peta
> progres, melainkan **acuan keputusan** untuk sisa proyek; progres aktual ada
> di [`docs/STATUS-MAP.md`](STATUS-MAP.md) dan catatan proyek
> `Obsidian/Hermes/projects/dot/` (`status.md`, `roadmap.md`).
>
> Bagian yang masih relevan dipegang: design token (§8), spesifikasi per widget
> (§6), izin (§7), gaya (§8.3), dan Plasma (§12). Bagian yang sudah dieksekusi
> penuh (§11.1) ditandai HISTORIS.

## 1. Ringkasan

**Dot.** (nama lengkap: Dot. - Inspired by Nothing) adalah aplikasi Android untuk semua perangkat Android, bukan hanya ponsel Nothing. Aplikasi ini menyediakan kumpulan widget homescreen yang terinspirasi gaya desain Nothing: monokrom, font titik-titik (dot-matrix), dan satu warna aksen. Pengguna menjelajahi katalog di Galeri Widget, melihat pratinjau, lalu menambahkan widget ke homescreen. Widget yang direncanakan mencakup jam, cuaca, kalender, status perangkat, dan kontrol musik, dan tampilannya bisa disesuaikan lewat fitur Gaya Nothing.

Produk dirilis bertahap dalam empat fase. Fase 1 sudah menghasilkan produk yang bisa dipakai (galeri dan Widget Jam bergaya Nothing), disusul widget informasi harian, widget lanjutan dan kustomisasi gaya, lalu pengaturan aplikasi. Proyek dikembangkan dengan bantuan AI di repo GitHub private (open-source menjadi rencana belakangan), dan setelah Fase 4 direncanakan versi widget KDE Plasma untuk Linux (lihat bagian 12).

## 2. Latar Belakang dan Masalah

- Pengguna yang menyukai estetika Nothing (monokrom, minimalis, dot-matrix) sulit menemukan widget yang tampilannya konsisten satu sama lain.
- Widget bawaan dan widget pihak ketiga umumnya berbeda gaya, sehingga homescreen terlihat tidak seragam.
- Mengatur widget (memilih ukuran, kota, format waktu, tema) sering tersebar di banyak aplikasi.

**Peluang:** satu aplikasi dengan satu bahasa desain yang konsisten, katalog yang mudah dijelajahi, dan pengaturan yang terpusat.

## 3. Tujuan dan Non-Tujuan

### Tujuan

1. Menyediakan galeri widget yang mudah dijelajahi, dengan pratinjau sebelum dipasang.
2. Menyediakan widget harian yang berguna: jam, cuaca, kalender.
3. Menyediakan widget status perangkat dan kontrol musik.
4. Memberi kontrol gaya (tema monokrom, font titik-titik, warna aksen) yang konsisten di semua widget.
5. Ringan dan berjalan baik di berbagai perangkat Android, dengan dampak baterai dan memori rendah.
6. Siap dibuka sebagai open-source suatu hari nanti: struktur repo, dokumentasi, dan catatan lisensi aset dirapikan sejak awal, walau repo tetap private untuk waktu yang lama.
7. Struktur proyek siap untuk versi KDE Plasma di kemudian hari (berbagi desain dan spesifikasi).

### Non-Tujuan (versi awal)

- Tidak ada platform iOS.
- Tidak ada marketplace atau widget buatan pengguna lain pada versi awal.
- Tidak ada akun atau login; semua pengaturan tersimpan di perangkat.
- Tidak ada iklan, pelacakan, atau telemetri.
- Tidak meniru atau memakai aset berhak cipta milik Nothing (lihat bagian Risiko).
- Versi KDE Plasma bukan bagian dari Fase 1 sampai 4 (lihat bagian 12).

## 4. Target Pengguna

| Persona | Deskripsi | Kebutuhan utama |
|---|---|---|
| Penyuka estetika Nothing di ponsel non-Nothing | Pengguna Android merek apa pun yang menyukai gaya monokrom dan dot-matrix tetapi tidak punya perangkat Nothing | Widget bergaya Nothing yang berjalan baik di ponsel mereka |
| Pengguna ponsel Nothing | Pemilik ponsel Nothing yang ingin widget tambahan di luar bawaan | Tampilan yang menyatu dengan sistem |
| Pengguna minimalis | Ingin homescreen bersih dengan informasi penting saja | Widget ringkas, mudah dibaca sekilas |
| Pengguna kustomisasi | Suka mengatur tampilan ponsel sendiri | Pilihan ukuran, tema, dan warna aksen |
| Kontributor open-source (bila proyek kelak dibuka) | Pengembang atau desainer yang ingin menyumbang kode, desain, atau terjemahan | Repo rapi, dokumentasi jelas, panduan kontribusi |
| Pengguna desktop Linux KDE Plasma (pasca-Fase 4) | Ingin widget bergaya sama di PC | Versi Plasma dengan tampilan konsisten |

## 5. Ruang Lingkup dan Hasil Rilis

> Dikelola sebagai **ringkasan hasil**, bukan daftar fase yang akan dikerjakan.
> Fase 1–4 selesai; kegiatan yang masih terbuka ada di Bagian 12 (Plasma), yang
> dianggap dalam pengerjaan. Progres detail per widget → Bagian 17.1 dan
> [`docs/STATUS-MAP.md`](STATUS-MAP.md).

| Fase | Status | Hasil |
|---|---|---|
| 1 | ✅ Selesai | Galeri, Widget Jam, design token, onboarding, konfigurasi dasar |
| 2 | ✅ Selesai | Teks, Kalender, Cuaca |
| 3 | ✅ Selesai | Status HP, Musik, Dekoratif, Gaya Nothing |
| 4 | ✅ Selesai (rilis v0.5.0) | Pengaturan, Tentang, Bantuan, Salin log |
| 12 | 🔄 Dalam pengerjaan | KDE Plasma (remake, pasca-Fase 4) — 6 widget dibuat, instal & uji belum |

**Keputusan urutan (tetap berlaku):** Widget Jam dipindah ke Fase 1 supaya rilis
pertama sudah punya alur lengkap (menjelajah, pratinjau, pasang) dengan widget
yang benar-benar berfungsi. Gaya dasar Nothing (monokrom, font titik-titik, satu
aksen default) tertanam sebagai *design token* sejak Fase 1; Fase 3 hanya
menambahkan kontrol bagi pengguna untuk mengubahnya, sehingga widget tidak
perlu dirombak.

## 6. Persyaratan Fungsional

Prioritas: **P0** = wajib untuk rilis fase tersebut, **P1** = sebaiknya ada, **P2** = bisa menyusul.

### 6.1 Galeri Widget (Fase 1)

**Tujuan:** pintu masuk utama aplikasi; tempat pengguna menemukan, melihat, dan memasang widget.

| ID | Persyaratan | Prioritas |
|---|---|---|
| GAL-1 | Menampilkan katalog semua widget yang tersedia dalam daftar/grid dengan nama, ikon, dan ukuran yang didukung | P0 |
| GAL-2 | Pratinjau widget dalam tampilan yang mendekati hasil akhir, termasuk pilihan ukuran; berupa gambar statis versi terang dan gelap per ukuran (bukan widget hidup) agar Fase 1 tetap sederhana | P0 |
| GAL-3 | Tombol “Tambah ke Homescreen” yang memicu proses pemasangan widget lewat sistem Android | P0 |
| GAL-4 | Widget Terpasang: daftar widget Dot. yang sudah ada di homescreen (dibaca lewat `GlanceAppWidgetManager`), masing-masing dengan jalan pintas membuka layar konfigurasinya; filter kategori sederhana cukup di Jelajahi Katalog, bukan fitur terpisah | P1 |
| GAL-5 | Widget yang belum rilis ditandai “Segera hadir”. Semua tujuh widget sudah dapat dipasang, jadi penanda ini kini hanya dipakai bila sebuah widget ditarik dari rilis | P1 |

**Kriteria penerimaan:**

- Pengguna dapat membuka galeri, memilih widget, melihat pratinjau, dan menambahkannya ke homescreen dalam tidak lebih dari 3 ketukan setelah memilih widget.
- Jika peluncur (launcher) tidak mendukung pemasangan langsung, aplikasi menampilkan panduan manual singkat.

### 6.2 Widget Jam (Fase 1)

**Tujuan:** jam digital bergaya Nothing, widget paling sederhana dan paling sering dipakai. Karena tidak butuh internet maupun izin khusus, widget ini masuk Fase 1 dan menjadi acuan gaya untuk widget lain.

| ID | Persyaratan | Prioritas |
|---|---|---|
| JAM-1 | Jam Digital dengan font titik-titik | P0 |
| JAM-2 | Format Waktu: 12 jam atau 24 jam | P0 |
| JAM-3 | Zona Waktu Tambahan: menampilkan satu atau lebih zona waktu lain berdampingan | P1 |
| JAM-4 | Opsi tampilkan detik (dengan catatan dampak baterai) | P2 |

**Kriteria penerimaan:**

- Waktu selalu akurat mengikuti pengaturan sistem, termasuk saat zona waktu atau format sistem berubah.
- Pembaruan tampilan tidak menyebabkan konsumsi baterai yang terukur berlebihan.

### 6.3 Widget Cuaca (Fase 2)

**Tujuan:** menampilkan cuaca ringkas untuk kota pilihan pengguna.

| ID | Persyaratan | Prioritas |
|---|---|---|
| CUA-1 | Pilih Kota: pengguna mencari dan memilih kota secara manual | P0 |
| CUA-2 | Info Cuaca Ringkas: suhu saat ini, kondisi (cerah/hujan/dll.), dan ikon bergaya dot-matrix | P0 |
| CUA-3 | Atur Ukuran Widget: minimal dua ukuran (kecil dan sedang); ukuran sedang menambah perkiraan beberapa jam ke depan | P0 |
| CUA-4 | Pembaruan data berkala dengan hemat baterai; menampilkan waktu pembaruan terakhir | P1 |
| CUA-5 | Pilihan satuan suhu °C/°F | P1 |
| CUA-6 | Sumber data cuaca yang tidak memerlukan API key pribadi (mis. Open-Meteo), supaya proyek open-source bisa dibangun dan dipakai tanpa kunci rahasia | P0 |

**Kriteria penerimaan:**

- Setelah kota dipilih, widget menampilkan data dalam waktu wajar (target di bawah 5 detik pada koneksi normal).
- Tanpa koneksi, widget menampilkan data terakhir yang tersimpan beserta keterangan waktunya, bukan layar kosong.

### 6.4 Widget Kalender (Fase 2)

**Tujuan:** menampilkan tanggal dan agenda terdekat.

| ID | Persyaratan | Prioritas |
|---|---|---|
| KAL-1 | Tanggal Hari Ini: hari dan tanggal ditampilkan jelas | P0 |
| KAL-2 | Agenda Mendatang: daftar acara terdekat dari kalender perangkat | P0 |
| KAL-3 | Pilih Tampilan: minimal dua mode, misalnya tanggal saja dan tanggal + agenda | P0 |
| KAL-4 | Pilihan kalender mana yang ditampilkan | P1 |
| KAL-5 | Ketuk acara untuk membuka aplikasi kalender | P2 |

**Kriteria penerimaan:**

- Izin akses kalender hanya diminta saat pengguna memilih tampilan yang memerlukannya; mode “tanggal saja” tetap berfungsi tanpa izin.
- Jika tidak ada acara, widget menampilkan keterangan kosong yang jelas.

### 6.5 Widget Status HP (Fase 3)

**Tujuan:** memantau kondisi perangkat sekilas.

| ID | Persyaratan | Prioritas |
|---|---|---|
| HP-1 | Pantau CPU: indikator beban CPU | P1 (lihat Risiko) |
| HP-2 | Suhu Perangkat: suhu baterai atau perangkat | P0 |
| HP-3 | Baterai dan Memori: persentase baterai, status pengisian, serta pemakaian RAM/penyimpanan | P0 |
| HP-4 | Visualisasi bergaya dot-matrix (bar atau grafik titik) | P1 |

**Kriteria penerimaan:**

- Nilai yang tampil sesuai dengan yang dilaporkan sistem Android dalam batas toleransi wajar.
- Bila suatu metrik tidak tersedia di perangkat tertentu, widget menyembunyikannya atau menandainya “tidak tersedia”, tidak menampilkan angka palsu.

### 6.6 Widget Musik (Fase 3)

**Tujuan:** kontrol pemutaran dan info lagu langsung dari homescreen.

| ID | Persyaratan | Prioritas |
|---|---|---|
| MUS-1 | Kontrol Putar: putar/jeda, berikutnya, sebelumnya | P0 |
| MUS-2 | Info Lagu: judul, artis, dan sampul album | P0 |
| MUS-3 | Pilih Pemutar Musik: pengguna memilih aplikasi pemutar yang dikendalikan | P1 |
| MUS-4 | Keadaan kosong saat tidak ada musik yang diputar | P0 |

**Kriteria penerimaan:**

- Widget mengendalikan pemutar yang sedang aktif dan memperbarui info lagu saat lagu berganti.
- Izin yang diperlukan dijelaskan dengan bahasa sederhana sebelum diminta.

### 6.7 Gaya Nothing (Fase 3)

**Tujuan:** kontrol gaya terpusat yang berlaku di semua widget. Gaya dasar sudah tersedia sejak Fase 1; fitur ini menambahkan pilihan bagi pengguna.

| ID | Persyaratan | Prioritas |
|---|---|---|
| GAY-0 | Gaya dasar Nothing sebagai design token bersama (warna, font, radius, jarak) dengan dua varian, terang dan gelap, yang dipakai semua widget; palet dasar hitam, putih, dan aksen Nothing wajib menjadi default (lihat 8.5) | P0 (Fase 1) |
| GAY-1 | Tema Monokrom: varian terang dan gelap untuk aplikasi dan semua widget, otomatis mengikuti tema sistem (termasuk jadwal siang/malam otomatis) | P0 (Fase 1) |
| GAY-1b | Pilihan tema: Ikuti Sistem (bawaan), Terang, atau Gelap; berlaku untuk aplikasi dan semua widget | P0 (Fase 1 sebagai pengaturan sederhana; dipindah ke Gaya Nothing di Fase 3) |
| GAY-1c | Tema per widget yang berbeda dari tema global | P2 |
| GAY-2 | Font Titik-Titik: pengguna dapat mengaktifkan atau menonaktifkan font dot-matrix (Doto) dan mengatur ketebalan serta kebulatan titik lewat sumbu variabel font | P0 |
| GAY-3 | Warna Aksen: palet dasar Nothing (hitam, putih, aksen merah khas Nothing) selalu ada sebagai default dan tidak bisa dihapus; aksen lain, termasuk oranye, tersedia sebagai preset, ditambah opsi Warna Sistem (Material You) yang mengikuti wallpaper di Android 12 ke atas; di Android 8 sampai 11 opsi ini tidak muncul | P0 |
| GAY-4 | Perubahan gaya langsung tercermin pada semua widget yang sudah terpasang | P0 |

**Kriteria penerimaan:**

- Mengubah gaya sekali memperbarui seluruh widget terpasang tanpa perlu memasang ulang.
- Kontras teks tetap terbaca pada semua kombinasi tema dan warna aksen. Bila pilihan tema adalah Ikuti Sistem, semua widget ikut berganti dalam beberapa detik saat tema sistem berubah; bila pengguna memilih Terang atau Gelap, widget tidak terpengaruh perubahan tema sistem.

### 6.8 Pengaturan Aplikasi (Fase 4)

**Tujuan:** pengaturan umum, informasi, dan jalur dukungan.

| ID | Persyaratan | Prioritas |
|---|---|---|
| SET-1 | Preferensi Widget: pengaturan global untuk semua widget (interval pembaruan, satuan, bahasa); pengaturan per widget (kota, tampilan, zona waktu, pemutar) tetap di layar konfigurasi widget masing-masing | P0 |
| SET-2 | Tentang Aplikasi: versi, lisensi aplikasi dan pihak ketiga, tautan kode sumber dan kebijakan privasi (keduanya bila kelak dirilis publik) | P0 |
| SET-3 | Bantuan dan Masukan: FAQ dan tautan ke pelacak isu (issue tracker) proyek | P1 |
| SET-4 | Salin log: tombol untuk menyalin log aplikasi ke papan klip saat melaporkan masalah, tanpa telemetri dan tanpa data pribadi | P1 |
| SET-5 | Cadangan pengaturan: pengaturan ikut tercadang lewat cadangan otomatis Android sehingga pulih saat ganti ponsel. Diterapkan sebagai `backup_rules.xml` + `data_extraction_rules.xml` (hanya domain `sharedpref`); layar Pengaturan menampilkan status "Otomatis" tanpa sakelar, karena pencadangan dikendalikan sistem, bukan aplikasi | P2 |

### 6.9 Konfigurasi, Onboarding, dan Aksi Ketuk (lintas widget, Fase 1)

**Tujuan:** menghubungkan galeri dengan widget yang berfungsi, dan memberi batas yang jelas antara pengaturan global dan pengaturan per widget.

| ID | Persyaratan | Prioritas |
|---|---|---|
| KON-1 | Layar konfigurasi tampil otomatis saat widget baru dipasang jika widget punya opsi (mis. kota, tampilan); widget tanpa opsi seperti Jam langsung terpasang dengan nilai bawaan | P0 |
| KON-2 | Onboarding pertama kali: maksimal tiga layar singkat tentang cara memasang widget, tanpa login | P0 |
| KON-3 | Cadangan bila launcher tidak mendukung pemasangan langsung: panduan manual bergambar (tekan lama homescreen, pilih Widget) | P0 |
| KON-4 | Aksi ketuk: setiap widget membuka tujuan yang jelas (usulan di bawah) | P1 |
| KON-5 | Panduan pengecualian optimasi baterai untuk pabrikan yang agresif mematikan proses latar belakang (mis. Xiaomi, Oppo, Vivo, Samsung), muncul saat widget yang butuh pembaruan dipasang | P1 (Fase 2) |

**Pembagian pengaturan:**

- Global (Gaya Nothing dan Pengaturan Aplikasi): tema, font, warna aksen, satuan, interval pembaruan, bahasa.
- Per widget (layar konfigurasi): kota, tampilan, ukuran, zona waktu tambahan, pemutar musik.

**Aksi ketuk (diterapkan, `widget/WidgetIntents.kt` + `weather/WeatherRefreshAction.kt`):**

- Jam: membuka aplikasi jam bawaan perangkat (`ACTION_SHOW_ALARMS`).
- Cuaca: menyegarkan data cuaca sekarang lewat WorkManager (`ActionCallback`).
- Kalender: membuka aplikasi kalender pada hari ini (`CalendarContract` `time`).
- Status HP: membuka pengaturan baterai sistem (`ACTION_POWER_USAGE_SUMMARY`).
- Musik: membuka pemutar yang sedang aktif (`getLaunchIntentForPackage` dari paket sesi media).

Bila aplikasi tujuan tidak terpasang (mis. ROM tanpa aplikasi jam), ketukan jatuh ke aplikasi Dot; `<queries>` di manifes menjaga resolusi intent tetap berhasil di Android 11+.

### 6.10 Keadaan Kosong dan Error (lintas widget)

Semua widget memakai aturan yang sama, dengan teks singkat dan tampilan bergaya Nothing, supaya tidak ada widget yang menampilkan area kosong atau berhenti diam-diam.

| Situasi | Perilaku widget |
|---|---|
| Offline atau tanpa internet | Tampilkan data terakhir yang tersimpan beserta waktu pembaruan terakhir; bila belum ada data, tampilkan teks singkat seperti Tidak ada koneksi |
| Layanan cuaca gagal atau membatasi permintaan | Perlakukan seperti offline, coba lagi pada jadwal berikutnya, jangan crash |
| Izin ditolak atau dicabut | Tampilkan satu baris penjelasan; ketuk membuka layar izin di aplikasi; fitur yang tidak butuh izin tetap berjalan |
| Data memang kosong (tidak ada acara, tidak ada musik diputar) | Tampilkan keadaan kosong yang jelas, mis. Tidak ada agenda, bukan area kosong |
| Pengaturan belum dilakukan (mis. kota belum dipilih) | Tampilkan placeholder dengan ajakan ketuk untuk mengatur |
| Metrik tidak tersedia di perangkat (mis. sensor suhu tertentu) | Sembunyikan metrik atau tandai tidak tersedia, tanpa angka palsu |
| Kesalahan tak terduga saat memuat widget | Tampilkan keadaan error bergaya sama dengan ketuk untuk memuat ulang; catat di log lokal (untuk fitur Salin log) |

### 6.11 Widget Teks (Fase 2)

**Tujuan:** widget kosmetik tempat pengguna menulis teks sendiri di aplikasi (kata-kata favorit, nama, pengingat, atau sekadar hiasan), lalu teks itu tampil di homescreen dengan font dot-matrix.

| ID | Persyaratan | Prioritas |
|---|---|---|
| TXT-1 | Editor teks di aplikasi: pengguna menulis teks satu sampai beberapa baris dengan batas panjang (usulan 100 karakter) | P0 |
| TXT-2 | Teks tampil di widget dengan font Doto, mengikuti tema dan warna aksen | P0 |
| TXT-3 | Beberapa widget Teks dapat dipasang sekaligus, masing-masing dengan teks sendiri | P0 |
| TXT-4 | Perubahan teks di aplikasi langsung memperbarui widget tanpa dipasang ulang | P0 |
| TXT-5 | Karakter yang tidak ada di font Doto (mis. emoji, aksara non-Latin) ditangani dengan jelas: memakai font cadangan, atau pengguna diberi peringatan saat menulis | P0 |
| TXT-6 | Pilihan perataan teks: kiri, tengah, kanan | P1 |
| TXT-7 | Pilihan ukuran huruf (kecil, sedang, besar); ukuran menyesuaikan otomatis agar teks tidak terpotong | P1 |
| TXT-8 | Opsi huruf kapital semua, dan opsi bingkai atau garis titik di sekitar teks | P2 |

**Perilaku:** editor teks ada di layar Konfigurasi Widget (muncul saat widget dipasang dan bisa dibuka lagi dari Widget Terpasang). Saat teks masih kosong, widget menampilkan ajakan ketuk untuk menulis. Ketuk widget membuka editor teks widget tersebut (usulan).

**Kriteria penerimaan:**

- Teks yang ditulis tampil di widget dalam beberapa detik, tidak terpotong, dan tetap terbaca di ukuran widget terkecil.
- Teks tersimpan di perangkat, bertahan setelah restart ponsel, dan tidak dikirim ke mana pun.
- Widget Teks tidak membutuhkan izin apa pun.

### 6.12 Widget Dekoratif (Fase 3)

**Tujuan:** widget murni kosmetik, tanpa data dan tanpa fungsi, yang hanya mempercantik homescreen dengan gaya titik-titik.

| ID | Persyaratan | Prioritas |
|---|---|---|
| DEK-1 | Pola Titik: grid titik dengan pola preset (mis. gelombang, spiral, diagonal) yang mengikuti tema dan warna aksen | P0 |
| DEK-2 | Opsi tiap widget dekoratif: pilih pola atau simbol, ukuran, dan apakah memakai warna aksen | P0 |
| DEK-3 | Simbol Titik: simbol besar dari titik (mis. hati, bintang, senyum, panah) dengan beberapa pilihan preset | P1 |
| DEK-4 | Pembatas: garis atau deret titik tipis sebagai pemisah visual antar widget | P2 |
| DEK-5 | Gambar Titik: pengguna memilih gambar lewat pemilih foto sistem, lalu diubah menjadi pola titik monokrom (ide; kerumitan lebih tinggi) | P2 |

**Kriteria penerimaan:**

- Widget dekoratif statis (tanpa animasi di versi awal) dan tidak memicu pembaruan latar belakang.
- Tidak butuh internet maupun izin khusus.
- Tampilannya mengikuti tema terang dan gelap serta warna aksen.

## 7. Persyaratan Non-Fungsional

- **Platform:** semua perangkat Android, bukan hanya Nothing. Versi minimum Android 8.0 (API 26), karena pemasangan widget langsung dari aplikasi dan pengaturan sumbu variabel font (wght, ROND) tersedia mulai versi ini; target SDK memakai versi stabil terbaru. Suhu Perangkat memakai suhu baterai yang tersedia di semua versi, sedangkan API termal yang lebih baru hanya sebagai tambahan.
- **Kompatibilitas:** diuji di beberapa merek dan launcher populer (mis. Samsung, Xiaomi, Oppo/Vivo, Pixel, Nothing OS) serta beberapa ukuran layar.
- **Performa:** galeri terbuka cepat; widget tidak menyebabkan pemakaian baterai yang mencolok. Pembaruan latar belakang memakai penjadwalan hemat daya dari sistem.
- **Privasi:** tanpa akun, tanpa iklan, tanpa pelacakan atau telemetri; data pengaturan tersimpan di perangkat; izin diminta seminimal mungkin dan hanya saat dibutuhkan. Pemilihan kota manual membuat widget cuaca tidak wajib memakai izin lokasi.
- **Ketahanan:** widget tetap menampilkan data terakhir saat offline dan tidak crash bila izin dicabut.
- **Aksesibilitas:** kontras memadai, ukuran teks mengikuti pengaturan sistem, dan deskripsi konten untuk pembaca layar.
- **Lokalisasi:** Bahasa Indonesia dan Inggris sejak awal, dengan struktur string yang memudahkan kontributor menambah terjemahan.

### 7.1 Izin Android per Fitur

Tabel ini jadi acuan izin. Izin baru tidak boleh ditambahkan sebelum tabel ini diperbarui dan alasannya jelas. Tabel ini juga menjadi bahan kebijakan privasi nanti.

| Fitur | Izin atau akses | Kapan diminta | Catatan |
|---|---|---|---|
| Galeri, Jam, Teks, Dekoratif, Gaya Nothing, Pengaturan | Tidak ada | Tidak berlaku | Tidak butuh izin khusus |
| Cuaca | Internet (izin biasa, tanpa dialog) | Otomatis saat dipasang | Tidak butuh izin lokasi karena kota dipilih manual |
| Kalender | Baca kalender (izin runtime) | Saat pengguna memilih tampilan yang memakai agenda | Mode tanggal saja tetap berjalan tanpa izin |
| Status HP | Tidak ada izin berbahaya | Tidak berlaku | Baterai dan suhu dibaca lewat siaran sistem, memori lewat API standar; CPU dibatasi sistem (lihat Risiko) |
| Musik | Akses notifikasi (akses khusus lewat pengaturan sistem) untuk membaca sesi media yang aktif | Saat widget Musik pertama kali dipasang, dengan penjelasan lebih dulu | Hanya dipakai untuk info lagu dan kontrol putar; tidak disimpan atau dikirim ke mana pun |
| Pembaruan latar belakang | Tidak ada izin berbahaya | Tidak berlaku | Tidak meminta pengecualian optimasi baterai otomatis; hanya panduan manual (KON-5) |
| Widget Jam (pembaruan tiap menit) | `SCHEDULE_EXACT_ALARM` (API 31–32, dapat dicabut) dan `USE_EXACT_ALARM` (API 33+) | Otomatis saat dipasang; bila dicabut, kode jatuh ke alarm tidak tepat waktu | Bukan izin berbahaya, tetapi akses khusus: dipakai hanya untuk menyegarkan tampilan jam, tanpa membangunkan perangkat |

## 8. Prinsip Desain dan UX

- **Monokrom dulu:** hitam, putih, abu-abu; satu warna aksen untuk penekanan.
- **Dot-matrix:** font dan ikon bertema titik untuk angka dan elemen utama.
- **Informasi sekilas:** setiap widget dapat dipahami dalam 1–2 detik.
- **Konsisten:** semua widget memakai token desain yang sama (warna, radius sudut, tipografi, jarak).
- **Alur aplikasi:** Galeri sebagai beranda, Gaya Nothing dan Pengaturan sebagai tab atau layar terpisah.

### 8.1 Font: Doto

Font yang dipakai adalah **Doto**, font dot-matrix variabel berlisensi terbuka yang paling mirip dengan gaya titik-titik Nothing. Font asli Nothing (Ndot) tidak dipakai karena bukan berlisensi terbuka.

| Item | Nilai |
|---|---|
| Nama font | Doto |
| Perancang | Óliver Lalan |
| Lisensi | SIL Open Font License 1.1 (boleh disertakan di aplikasi; teks lisensi ikut disertakan dan dicatat di NOTICE) |
| Karakter | Font dot-matrix variabel; tiap glyph dibangun dari matriks 6x10 |
| Sumbu variabel | wght (ketebalan, 100 sampai 900) dan ROND (kebulatan titik) |
| Aksara | Latin (cukup untuk bahasa Indonesia dan Inggris) |
| Halaman resmi | https://fonts.google.com/specimen/Doto |
| Repo perancang | https://github.com/oliverlalan/Doto |
| Berkas di repo Google Fonts | ofl/doto/Doto[ROND,wght].ttf |
| Cadangan bila Doto tidak cocok | Sixtyfour, Handjet, atau Workbench (font bergaya titik di Google Fonts); cek lisensi masing-masing sebelum dipakai |

**Petunjuk untuk AI yang mengimplementasikan:**

1. Ambil font hanya dari halaman resmi atau repo Google Fonts di atas (bukan dari situs pihak ketiga), simpan di /design/fonts, lalu salin ke sumber daya font aplikasi Android.
2. Simpan teks lisensi OFL bersama berkas font dan catat di NOTICE.
3. Atur font dan nilai sumbu variabelnya (wght, ROND) sebagai design token, bukan angka yang tersebar di kode.
4. Uji di Widget Jam (Fase 1) apakah font variabel tampil benar di widget. Jika tidak, render teks sebagai gambar atau buat instance statis; untuk instance statis, periksa ketentuan OFL tentang Reserved Font Name.
5. Sediakan font cadangan (monospace sistem) bila Doto gagal dimuat.
6. Untuk versi Plasma nanti, pakai berkas font yang sama.

### 8.2 Ikon dan Ilustrasi

Ikon dan ilustrasi dibuat dengan bantuan AI. Supaya hasilnya konsisten dan mudah dirawat, AI diminta menghasilkan kode, bukan gambar raster:

- **Ikon widget dan ilustrasi cuaca:** pola titik pada grid tetap (mis. 12x12), didefinisikan di kode atau SVG lalu digambar sebagai lingkaran. Cocok dengan gaya dot-matrix, dan warna aksennya mudah diganti lewat design token.
- **Ikon aplikasi (launcher):** vektor (SVG atau adaptive icon), dihasilkan dengan bantuan AI lalu dirapikan.
- **Hindari gambar raster dari generator gambar AI:** gayanya sulit konsisten, warnanya tidak bisa diubah, dan status hak cipta serta syarat layanannya berbeda-beda antar layanan dan negara. Bila tetap dipakai, catat alat dan ketentuan layanannya di NOTICE.

### 8.3 Tema Terang dan Gelap

Aplikasi dan semua widget punya dua varian, terang dan gelap. Keduanya tetap monokrom dengan satu warna aksen. Nilai warna kedua varian didefinisikan di design token (/design), bukan tersebar di kode.

1. **Tiga pilihan:** Ikuti Sistem (bawaan), Terang, dan Gelap.
2. **Ikuti Sistem:** aplikasi dan widget berganti otomatis mengikuti tema sistem, termasuk jadwal siang/malam otomatis dari sistem.
3. **Pilihan manual:** Terang atau Gelap berlaku untuk aplikasi dan semua widget, dan tidak terpengaruh perubahan tema sistem.
4. **Lokasi pengaturan:** sejak Fase 1 berupa pengaturan sederhana di layar utama aplikasi; pada Fase 3 dipindah ke Gaya Nothing dan Pengaturan Aplikasi.
5. **Tema per widget:** opsional dan tidak dijadwalkan (P2).
6. **Catatan teknis untuk AI:** bila teks widget dirender sebagai gambar karena font Doto (lihat 8.1), gambar dibuat satu warna lalu diwarnai ulang atau dirender ulang saat tema berubah, supaya tidak tertinggal di tema lama. Uji ini sejak Widget Jam di Fase 1.
7. **Pengujian:** ganti tema sistem dan pilihan tema aplikasi saat widget terpasang; semua widget harus ikut berubah tanpa dipasang ulang.

**Catatan implementasi (sudah diterapkan):**
- Satu sumber kebenaran: `widget/WidgetColors.kt` → `DayNightColor` memanggil `isWidgetDark()`, yang membaca `DotStyleSettings.getTheme()` (Ikuti Sistem → `uiMode`; Terang → paksa terang; Gelap → paksa gelap). Tidak ada widget yang membaca `uiMode` langsung, sehingga pilihan manual benar-benar mengunci palet.
- Widget Teks (RemoteViews, bukan Glance) mewarnai ulang latar dan teks dari palet yang sama saat `updateWidget`, dan memilih varian font Doto/sistem sesuai pilihan Gaya.
- Mengubah gaya di layar Gaya langsung memicu `StyleRefresher.refreshAll()` untuk seluruh widget terpasang (Glance + Widget Teks).
- Perubahan tema sistem memicu update dari launcher; bila launcher tidak memicunya, widget menyusul pada siklus update berikutnya (Jam ≤ 1 menit, widget lain ≤ 30 menit).

### 8.4 Peta Layar dan Navigasi

Usulan awal; bentuk akhir ditentukan saat desain layar.

| Layar | Isi | Fase |
|---|---|---|
| Beranda (Galeri) | Katalog widget, pintu masuk utama, dan pilihan tema | 1 |
| Detail dan Pratinjau Widget | Pratinjau per ukuran dan tema, tombol Tambah ke Homescreen | 1 |
| Konfigurasi Widget | Muncul saat widget dipasang atau diubah (kota, tampilan, dll.) | 1 (Cuaca dan Kalender di Fase 2) |
| Widget Terpasang | Daftar widget yang sudah dipasang, dengan jalan pintas mengubah konfigurasi | 1 |
| Onboarding | Maksimal tiga layar, hanya saat pertama kali dibuka | 1 |
| Gaya Nothing | Tema, font titik-titik, warna aksen | 3 |
| Pengaturan Aplikasi | Preferensi Widget, Tentang Aplikasi, Bantuan dan Masukan, Salin log | 4 |

- **Navigasi:** bilah bawah. Fase 1 berisi dua tab (Galeri dan Terpasang); tab Gaya masuk di Fase 3 dan Pengaturan di Fase 4.
- **Alur utama:** Galeri, lalu Detail dan Pratinjau, lalu Tambah ke Homescreen; layar Konfigurasi muncul bila widget punya opsi.
- **Gaya aplikasi sendiri:** mengikuti gaya Nothing (monokrom, font Doto) dan tema terang atau gelap yang sama dengan widget.

### 8.5 Palet Warna Dasar (wajib)

Palet dasar Nothing adalah inti identitas aplikasi: hitam, putih, dan satu warna aksen khas Nothing. Palet ini selalu menjadi default dan tidak bisa dihapus; pilihan lain hanya tambahan.

| Peran | Warna | Catatan |
|---|---|---|
| Hitam | #000000 | Latar tema gelap; teks tema terang |
| Putih | #FFFFFF | Latar tema terang; teks tema gelap |
| Abu-abu netral | Satu atau dua tingkat abu-abu | Teks sekunder, garis, dan titik tidak aktif |
| Aksen Nothing (default) | Merah khas Nothing; titik awal sekitar #D71921 (perkiraan, belum diverifikasi dari sumber resmi), dicocokkan dengan tangkapan layar Nothing OS saat design token dibuat | Satu-satunya warna berwarna di tampilan default |
| Aksen oranye (preset) | Oranye; nilai ditetapkan di design token | Tersedia sebagai pilihan siap pakai; bisa dijadikan default oleh pemilik proyek bila diinginkan |
| Warna Sistem (opsional) | Material You, Android 12 ke atas | Tidak pernah menjadi default |

**Catatan:** sepengetahuan penulis, antarmuka Nothing OS identik dengan hitam, putih, dan merah, sedangkan oranye lebih dikenal dari sub-merek CMF by Nothing. Karena itu default ditetapkan merah (keputusan final), dan oranye disediakan sebagai preset. Mengubah default cukup dilakukan di satu tempat, yaitu design token.

**Aturan:**

1. Tampilan pertama setelah pemasangan memakai palet dasar (hitam, putih, aksen Nothing), bukan Warna Sistem.
2. Pilihan warna lain hanya tambahan di Gaya Nothing; tombol untuk mengembalikan ke warna Nothing selalu tersedia.
3. Nilai warna hanya didefinisikan di design token (/design), tidak ditulis langsung di kode widget atau layar.

## 9. Pertimbangan Teknis (Usulan)

- **Bahasa dan UI:** Kotlin native. Widget dibangun dengan Jetpack Glance atau AppWidget klasik, dan aplikasi (galeri, pengaturan) dengan Jetpack Compose. Pilihan final dicatat di repo pada awal pengembangan.
- **Font kustom di widget:** widget Android punya batasan dalam memakai font kustom. Solusi yang umum adalah merender teks menjadi gambar (bitmap). Ini perlu diuji dengan font Doto sejak Widget Jam di Fase 1 (lihat 8.1).
- **Pemasangan dari galeri:** memakai fungsi *pin widget* milik sistem Android.
- **Sumber data cuaca:** layanan tanpa API key (mis. Open-Meteo; periksa ketentuan penggunaan dan atribusinya) agar tidak ada kunci rahasia di repo publik.
- **Kalender dan musik:** kalender dibaca dari penyedia kalender perangkat; musik dikendalikan lewat sesi media sistem.
- **Pembaruan latar belakang:** memakai penjadwalan resmi sistem (mis. WorkManager).
- **Gaya:** nilai gaya didefinisikan sekali sebagai design token dalam berkas netral (JSON) di folder /design, lalu dipakai semua widget dan disimpan sebagai preferensi lokal.
- **Struktur repo (usulan):** monorepo dengan folder /android, /design, /docs, dan nanti /plasma. Application ID (usulan format): io.github.<username-github>.dot; username diambil agen AI dari akun GitHub yang sudah terautentikasi (bukan ditanyakan atau ditebak), ditetapkan sebelum build pertama dan tidak diubah lagi setelah aplikasi terpasang di perangkat.

### 9.1 Ukuran dan Pembaruan Widget (usulan awal)

| Widget | Ukuran (sel homescreen) | Pembaruan | Sumber data |
|---|---|---|---|
| Jam | 2x1 dan 2x2 | Tiap menit, tanpa detik di Fase 1 | Waktu sistem |
| Cuaca | 2x2 dan 4x2 | Bawaan tiap 60 menit; bisa diubah di Preferensi Widget | Layanan cuaca tanpa API key |
| Kalender | 2x2 dan 4x2 | Saat data kalender berubah, dan tiap 30 menit | Penyedia kalender perangkat |
| Teks | 2x1, 2x2, dan 4x2 | Hanya saat teks diubah, tanpa pembaruan berkala | Teks yang disimpan di perangkat |
| Status HP | 2x2 dan 4x1 | Tiap 15 menit (batas minimum penjadwalan sistem); dampak baterai diukur | Siaran sistem dan API standar |
| Musik | 4x1 dan 4x2 | Berbasis peristiwa (lagu atau status putar berubah), tanpa polling | Sesi media sistem |
| Dekoratif | 2x2 dan 4x2 (Pembatas: 4x1) | Statis, tanpa pembaruan berkala | Pola preset di dalam aplikasi |

Catatan teknis: mekanisme pembaruan bawaan widget Android dibatasi (sepengetahuanku minimal sekitar 30 menit), jadi pembaruan yang lebih sering, seperti Jam per menit, memakai mekanisme lain. AI perlu memverifikasi caranya di dokumentasi Android terbaru dan mengujinya sejak spike Fase 1 (lihat 11.1). Ukuran dan interval di atas hanya titik awal.

## 10. Repo, Lisensi, dan Distribusi

- **Status repo:** GitHub private untuk waktu yang lama. Open-source adalah rencana belakangan, bukan target rilis.
- **Lisensi (keputusan pemilik):** GPL-2.0-or-later, diterapkan nanti saat repo dibuka atau APK dibagikan ke orang lain; tidak diperlukan selama repo private. Dipilih varian **GPL-2.0-or-later**, bukan GPL-2.0-only: pustaka Android (AndroidX, Kotlin) umumnya berlisensi Apache-2.0, yang tidak kompatibel dengan GPL-2.0-only tetapi kompatibel dengan GPLv3. Varian or-later juga selaras dengan lisensi yang umum di KDE untuk versi Plasma. Lisensi tiap dependensi dicek sebelum repo dibuka. Ini informasi umum, bukan nasihat hukum.
- **Catatan lisensi aset (mulai sekarang):** catat sumber, lisensi, dan tautan setiap font, ikon, dan pustaka pihak ketiga di berkas NOTICE (Doto: SIL OFL 1.1).
- **Aset:** font dan ikon berlisensi terbuka atau buatan sendiri; tidak memakai aset milik Nothing.
- **Berkas repo:** README, NOTICE, dan panduan build disiapkan sejak awal. LICENSE, CONTRIBUTING, kebijakan privasi, dan panduan melapor isu disiapkan mendekati pembukaan repo.
- **Distribusi:** selama private, APK dipasang langsung di perangkat sendiri. Bila kelak dibuka: GitHub Releases, lalu F-Droid; Play Store opsional.
- **Syarat F-Droid (bila kelak dituju):** semua dependensi harus open-source, jadi hindari Google Play Services, Firebase, dan SDK analitik atau iklan. Ini murah dilakukan sejak awal.
- **Keamanan repo:** jangan memasukkan keystore penandatangan, kata sandi, token GitHub, atau API key ke repo atau berkas konteks AI, walau masih private. Cadangkan keystore di tempat aman, karena kehilangannya berarti pembaruan aplikasi tidak bisa dirilis lagi.

## 11. Pengembangan dengan AI

Proyek dikerjakan sepenuhnya dengan bantuan AI, jadi cara kerjanya harus membuat hasil mudah diperiksa dan tidak bergantung pada ingatan satu sesi.

1. **PRD sebagai sumber kebenaran:** simpan PRD ini di folder /docs, dan rujuk ID persyaratan (mis. JAM-2) di setiap tugas ke AI.
2. **Berkas konteks di repo** (mis. AGENTS.md atau CLAUDE.md): ringkasan arsitektur, konvensi, perintah build dan tes, serta aturan agar AI tidak menambah izin atau dependensi tanpa konfirmasi.
3. **Satu tugas kecil per sesi:** satu sub-fitur atau satu persyaratan, dengan kriteria penerimaan sebagai definisi selesai.
4. **Tes dan CI sejak awal:** GitHub Actions yang menjalankan build, lint, dan tes di setiap push, supaya regresi cepat ketahuan.
5. **Commit sering:** satu commit setiap ada hal yang berfungsi, supaya mudah kembali ke versi yang bekerja.
6. **Uji di perangkat nyata:** AI tidak bisa memastikan perilaku merek ponsel tertentu. Catat temuan per perangkat di repo.
7. **Tinjau izin dan dependensi baru:** setiap izin Android atau pustaka baru dicek tujuan dan lisensinya sebelum digabung.

### 11.1 Rencana Pengerjaan Fase 1 (urutan tugas untuk AI)

Setiap tugas dikerjakan dalam satu sesi AI dan di-commit setelah berfungsi. Tugas berikutnya baru dimulai setelah tugas sebelumnya lolos.

1. **Siapkan repo:** struktur monorepo, README, NOTICE, .gitignore (termasuk keystore), berkas konteks AI (AGENTS.md atau CLAUDE.md), dan CI dasar.
2. **Proyek Android kosong:** minSdk 26, Application ID (username diambil dari akun GitHub yang terautentikasi), tema terang dan gelap dasar, build debug berjalan di CI.
3. **Design token:** palet dasar Nothing (lihat 8.5) dalam varian terang dan gelap, font Doto beserta sumbunya, radius, dan jarak, disimpan di /design lalu dihubungkan ke Android.
4. **Spike teknis (sebelum membangun widget sungguhan):** uji font Doto variabel di widget percobaan (font langsung atau teks sebagai gambar) di emulator API 26 dan API terbaru, serta uji cara memperbarui jam tiap menit. Hasilnya dicatat di repo dan menentukan cara membangun widget.
5. **Widget Jam:** Jam Digital dan Format Waktu, lalu Zona Waktu Tambahan; tampil benar di tema terang dan gelap.
6. **Galeri:** katalog (Widget Jam dan sisanya Segera hadir), Detail dan Pratinjau, Tambah ke Homescreen, dan panduan manual.
7. **Pilihan tema:** Ikuti Sistem, Terang, Gelap, berlaku untuk aplikasi dan widget.
8. **Widget Terpasang, layar Konfigurasi dasar, dan Onboarding.**
9. **Uji lintas perangkat** dan periksa definisi selesai Fase 1 (bagian 17.1).

Sebelum Fase 3 dimulai, jadwalkan spike kecil untuk menguji kelayakan Pantau CPU, supaya keputusannya tidak menunggu sampai fitur itu dibangun.

### 11.2 Strategi Pengujian

- **Tes unit** untuk logika murni: format waktu, zona waktu, pengolahan data cuaca, pemilihan tema.
- **Tes tampilan** untuk widget di tema terang dan gelap (mis. snapshot test), supaya perubahan gaya yang tidak sengaja ketahuan.
- **Matriks perangkat minimal:** emulator API 26 (batas bawah), API 31 atau lebih baru (Warna Sistem), dan SDK terbaru; ditambah perangkat fisik yang tersedia, termasuk satu ponsel dengan penghemat baterai agresif bila ada.
- **Skenario manual setiap rilis:** ganti tema sistem dan tema aplikasi, ganti format 12 atau 24 jam, mode pesawat, cabut izin, putar layar, ubah ukuran widget, restart ponsel (widget harus tetap tampil dan ter-update), dan ubah zona waktu.

## 12. Rencana Versi KDE Plasma (Pasca-Fase 4)

Versi Plasma sebaiknya dikerjakan sebagai *remake*, bukan port. Widget Plasma (plasmoid) ditulis dengan QML (dengan JavaScript, atau C++ bila perlu), sedangkan widget Android ditulis dengan Kotlin, sehingga kode antarmuka tidak bisa dipakai ulang. Yang bisa dipakai ulang adalah desain dan spesifikasinya.

**Dibagikan antar platform:**

- Design token (warna, font, radius, jarak) dalam berkas JSON di folder /design.
- Spesifikasi tiap widget: tata letak, perilaku, dan data yang ditampilkan (turunan dari PRD ini).
- Aset: font Doto (berlisensi terbuka) dan ikon SVG.
- Sumber data dan logikanya (mis. layanan cuaca yang sama).

**Berbeda di Plasma:**

- Galeri dan pemasangan tidak diperlukan; widget ditambahkan lewat mekanisme widget Plasma dan bisa dibagikan lewat KDE Store (usulan).
- Status perangkat dan musik justru lebih mudah di Linux (sensor sistem, dan protokol MPRIS untuk pemutar musik), sedangkan di Android dibatasi izin.
- Plasma sudah punya widget bawaan untuk jam, cuaca, kalender, monitor sistem, dan pemutar media. Nilai tambah versi ini adalah tampilan Nothing yang konsisten, bukan fungsi baru.

**Urutan usulan:** Jam, Cuaca, Kalender, Status Sistem, Musik, lalu gaya yang bisa diatur.

**Struktur:** monorepo dengan folder /android, /plasma, /design, dan /docs. Kotlin Multiplatform tidak disarankan di sini: sisi Plasma memakai QML, jadi berbagi logika lewat KMP menambah kerumitan yang tidak sebanding untuk proyek kecil.

**Keputusan sementara untuk versi Plasma:**

- Target Plasma 6 saja.
- Dikerjakan untuk pemakaian pribadi dulu; publikasi (mis. lewat KDE Store) dipertimbangkan setelah repo dibuka dan versinya stabil.
- Mulai dari satu widget (Jam) sebagai bukti konsep, termasuk memuat font Doto dari paket widget (mis. dengan FontLoader di QML), lalu lanjut sesuai urutan di atas.
- Pengaturan widget lewat halaman konfigurasi bawaan Plasma; tidak ada galeri atau layar pengaturan aplikasi sendiri.
- Lisensi mengikuti proyek utama.

## 13. Metrik Keberhasilan (Usulan Target)

| Metrik | Target awal | Berlaku |
|---|---|---|
| Build CI (lint, tes, APK) | Hijau di setiap rilis internal | Sekarang |
| Widget terpasang dan tampil benar di perangkat uji | Lulus di semua perangkat dan emulator yang tersedia, minimal 2 merek atau launcher berbeda | Sekarang |
| Bug kritis (crash, widget tidak tampil) | Ditutup sebelum fase berikutnya dimulai | Sekarang |
| Unduhan (GitHub Releases, dan F-Droid bila sudah tayang) | ≥ 500 dalam 3 bulan setelah rilis publik pertama | Setelah dibuka |
| Bintang GitHub | ≥ 100 dalam 6 bulan setelah dibuka | Setelah dibuka |
| Kontributor luar (PR yang digabung) | ≥ 3 | Setelah dibuka |

Angka di atas hanya titik awal dan perlu disesuaikan dengan target nyata proyek. Aplikasi tanpa telemetri, jadi metrik tahap pribadi diukur manual lewat pengujian di perangkat, sedangkan metrik setelah dibuka memakai data publik (unduhan, bintang, isu).

## 14. Risiko dan Dependensi

| Risiko | Dampak | Mitigasi |
|---|---|---|
| Pembacaan beban CPU dibatasi oleh Android versi baru bagi aplikasi biasa | Fitur Pantau CPU mungkin tidak akurat atau tidak bisa | Verifikasi kelayakan sejak awal; siapkan alternatif (mis. indikator suhu dan memori saja) |
| Pembatasan baterai dan proses latar belakang dari pabrikan ponsel | Widget tidak ter-update tepat waktu | Gunakan penjadwalan resmi sistem; sediakan panduan pengecualian optimasi baterai (KON-5) |
| Font kustom sulit dipakai di widget Android | Tampilan dot-matrix gagal atau tidak konsisten | Uji sejak Widget Jam di Fase 1; render teks sebagai gambar bila perlu |
| Merek dan font: nama Nothing dan font Ndot bisa dilindungi merek dagang atau hak cipta | Risiko hukum bila kelak dipublikasikan, terutama di toko aplikasi | Pakai font Doto (SIL OFL); nama aplikasi Dot. dengan tagline Inspired by Nothing, label launcher cukup Dot.; tinjau ulang pemakaian nama Nothing di judul sebelum rilis ke toko aplikasi |
| Ketergantungan pada layanan cuaca pihak ketiga | Batas pemakaian atau perubahan ketentuan layanan | Pilih layanan tanpa API key; siapkan cache dan cadangan |
| Izin sensitif (kalender, notifikasi/media) | Pengguna enggan mengizinkan | Minta izin kontekstual dengan penjelasan jelas; fitur tetap berguna tanpa izin bila memungkinkan |
| Perbedaan perilaku antar launcher dan merek | Pemasangan atau ukuran widget tidak konsisten | Uji di beberapa launcher dan ukuran layar populer |
| Kode hasil AI: bug halus, izin atau dependensi yang tidak perlu, lisensi dependensi yang tidak cocok | Aplikasi tidak stabil atau tidak bisa didistribusikan | CI dan tes sejak awal; tinjau perubahan izin dan dependensi; cek lisensi setiap pustaka |
| Proyek dipelihara satu orang (lebih terasa bila kelak dibuka sebagai open-source) | Isu menumpuk, kecepatan turun | Cakupan kecil per fase; dokumentasi jelas; jangan menjanjikan jadwal |
| Versi Plasma lebih besar dari perkiraan karena kode tidak bisa dipakai ulang | Pekerjaan hampir mengulang dari awal | Anggap sebagai remake; bagikan design token dan spesifikasi; mulai dari satu widget |

## 15. Asumsi

1. Platform Android untuk semua merek; aplikasi mandiri tanpa akun.
2. Pemilihan kota untuk cuaca dilakukan manual.
3. Widget memiliki beberapa ukuran standar (kecil dan sedang) sejak awal.
4. Gaya dasar Nothing tertanam sejak Fase 1.
5. Antarmuka tersedia dalam Bahasa Indonesia dan Inggris.
6. Proyek pribadi di repo GitHub private (open-source menjadi rencana belakangan), tanpa iklan, dikembangkan dengan bantuan AI oleh satu orang, dengan Kotlin sebagai bahasa utama.
7. Versi Plasma menargetkan Plasma 6 dan dibuat sebagai remake.
8. Android minimum API 26 (Android 8.0), target SDK stabil terbaru.
9. Ikon dan ilustrasi dibuat dengan bantuan AI sebagai vektor atau pola titik (lihat 8.2).

## 16. Pertanyaan Terbuka

1. Tidak ada pertanyaan yang menghambat Fase 1.
2. Nilai pasti warna aksen merah Nothing dicocokkan dengan tangkapan layar Nothing OS saat design token dibuat (Fase 1, tugas 3); #D71921 hanya perkiraan awal.
3. Ide widget tambahan di bagian 18: urutan prioritas ditentukan setelah Fase 4.
4. Lisensi GPL-2.0-or-later diterapkan saat repo dibuka, setelah lisensi dependensi dicek.
5. Keputusan sementara untuk versi Plasma (bagian 12) ditinjau ulang saat pengerjaannya dimulai.

## 17. Ringkasan Peta Jalan

| Fase | Fokus | Hasil akhir |
|---|---|---|
| 1 | Galeri, Widget Jam, gaya dasar dengan tema terang/gelap, konfigurasi, onboarding | Rilis pertama yang bisa dipakai: memasang dan mengatur Widget Jam bergaya Nothing |
| 2 | Cuaca, Kalender, dan Teks | Dua widget informasi harian dan satu widget teks buatan pengguna |
| 3 | Status HP, Musik, Dekoratif, Gaya Nothing | Widget lanjutan, widget dekoratif, dan kustomisasi gaya oleh pengguna |
| 4 | Pengaturan Aplikasi | Preferensi, informasi aplikasi, bantuan, dan masukan |
| Pasca-4 | Versi KDE Plasma (remake) | Widget Plasma bergaya Nothing, dimulai dari Jam |

### 17.1 Definisi Selesai per Fase

Satu fase dianggap selesai bila semua poinnya terpenuhi.

**Fase 1 selesai bila:**

- Galeri menampilkan seluruh widget yang sudah rilis (kini ketujuh widget) dengan pratinjau terang dan gelap.
- Widget Jam bisa dipasang dari galeri ke homescreen dan tampil benar dengan font Doto dan palet dasar Nothing (8.5).
- Widget Jam mengikuti tema sistem dan pilihan tema (Ikuti Sistem, Terang, Gelap).
- Panel Widget Terpasang (kartu di atas katalog), onboarding, dan panduan manual (untuk launcher tanpa pemasangan langsung) berfungsi.
- Lolos uji di minimal 2 merek atau launcher berbeda (emulator boleh), CI hijau, dan NOTICE sudah memuat Doto.

**Fase 2 selesai bila:**

- Widget Cuaca dan Kalender berfungsi lengkap dengan layar konfigurasi masing-masing.
- Widget Teks berfungsi: teks yang ditulis di aplikasi tampil di widget tanpa terpotong, bertahan setelah restart ponsel, dan karakter yang tidak didukung font ditangani.
- Keadaan offline, izin ditolak, dan data kosong tertangani sesuai bagian 6.10.
- Izin kalender diminta secara kontekstual; panduan pengecualian baterai (KON-5) tersedia.
- Widget tetap ter-update di perangkat dengan penghemat baterai agresif.

**Fase 3 selesai bila:**

- Widget Status HP dan Musik berfungsi; Pantau CPU diputuskan layak atau diganti alternatif.
- Widget Dekoratif (minimal Pola Titik) tersedia dan mengikuti tema.
- Mengubah pengaturan Gaya Nothing memperbarui semua widget terpasang tanpa dipasang ulang.
- Tabel izin (7.1) dan NOTICE sudah mutakhir.

**Fase 4 selesai bila:**

- Preferensi Widget, Tentang Aplikasi, Bantuan dan Masukan, dan Salin log berfungsi.
- Lisensi semua dependensi sudah dicek, sehingga repo siap dipertimbangkan untuk dibuka.

**Pasca-Fase 4 (Plasma):** Widget Jam versi Plasma berfungsi dengan font Doto sebagai bukti konsep.

## 18. Backlog Ide Widget Tambahan (Pasca-Fase 4)

Belum dijadwalkan. Diurutkan dari yang paling mudah dan paling cocok dengan gaya titik-titik; Progres Tahun dan Hitung Mundur cocok jadi kandidat pertama karena tanpa izin dan tanpa internet.

| Ide | Deskripsi | Izin khusus | Kerumitan |
|---|---|---|---|
| Progres Tahun | Grid titik, satu titik per hari; titik hari yang sudah lewat terisi | Tidak ada | Rendah |
| Hitung Mundur | Sisa hari menuju tanggal tertentu, ditampilkan dengan angka dot-matrix | Tidak ada | Rendah |
| Pelacak Kebiasaan | Grid titik per hari; ketuk untuk menandai selesai | Tidak ada | Sedang (penyimpanan lokal dan interaksi di widget) |
| Timer Fokus | Hitung mundur sesi fokus (Pomodoro) | Mungkin notifikasi | Sedang |
