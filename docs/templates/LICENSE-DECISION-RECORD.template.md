# License Decision Record — Template

> **Metadata template** — versi `1.0` · dibuat 23 Oktober 2026 · pemilik template: doc-writer
> (pengusul) · pemilik keputusan: tim hukum/kepatuhan + pemilik proyek.
> Turunan dari formulir kanonik Dot.: [`../LICENSE-DECISION-RECORD.md` §3](../LICENSE-DECISION-RECORD.md#3-license-decision-record-formulir)
> (v1.0) — template ini dipakai untuk keputusan yang **tidak** tercakup formulir itu.
>
> **Cara pakai:** salin berkas ini ke `docs/license-records/<slug-komponen>-<YYYY-MM-DD>.md`
> (folder tujuan: [`../license-records/README.md`](../license-records/README.md)), ganti setiap
> `_(belum diisi)_` dengan isi sebenarnya, lalu tautkan record terisi ke item/pekerjaan yang
> diblokir. Jangan menghapus baris field — baris kosong adalah tanda keputusan belum lengkap.

## Aturan pengisian

1. **Hanya keputusan tertulis.** Sumber sah: komentar kartu, email/chat yang ditempel, atau berkas
   keputusan. Unblock kartu tanpa teks, perubahan berkas tanpa alasan, dan kesimpulan agen **bukan**
   keputusan.
2. **`belum diputuskan` adalah jawaban sah** untuk satu field yang memang belum bisa diputuskan.
   Yang tidak boleh: field tidak disebut sama sekali, atau diisi dengan tebakan.
3. **Satu record per keputusan.** Bila keputusan menyangkut beberapa komponen dengan hasil berbeda,
   buat satu record per komponen dan saling tautkan di field (h).
4. **ID lisensi harus persis** (`GPL-2.0-or-later`, bukan "GPL"; `Apache-2.0`, bukan "open source").
5. **Isi record menggantikan placeholder, bukan menambah bagian baru** — struktur field tetap.

## Indeks field

| Kode | Field | Wajib |
|---|---|---|
| a | Jenis lisensi/komponen yang ditinjau | ya |
| b | Pertanyaan hukum yang diajukan | ya |
| c | Tanggal pengajuan ke tim hukum/kepatuhan | ya |
| d | Nama & narahubung penanggung jawab | ya |
| e | Keputusan akhir (disetujui / ditolak / bersyarat) | ya |
| f | Syarat & batasan | ya |
| g | Tanggal berlaku | ya |
| h | Lampiran/referensi | ya |
| i | Item/pekerjaan yang diblokir (tautan) | ya |

---

## a. Jenis lisensi / komponen yang ditinjau

```text
_(belum diisi)_ — komponen: ......  · path repo: ......  · versi/commit: ......  · lisensi yang ditinjau: ......
```

> Petunjuk: sebut komponen persisnya (path + versi/commit) dan lisensi yang sedang dipertanyakan; satu baris per komponen bila lebih dari satu.

## b. Pertanyaan hukum yang diajukan

```text
_(belum diisi)_
Q1 = ......
Q2 = ......   (tambah Q3, Q4, … bila perlu)
```

> Petunjuk: tulis pertanyaan yang butuh jawaban hukum, bernomor dan satu per baris; bila sudah ada daftar kanonik, rujuk ID-nya (Dot.: [`../LICENSE-DECISION-REQUEST.md` §6](../LICENSE-DECISION-REQUEST.md#6-pertanyaan-untuk-tim-hukumkepatuhan)).

## c. Tanggal pengajuan ke tim hukum/kepatuhan

```text
_(belum diisi)_ — YYYY-MM-DD (WIB)
```

> Petunjuk: tanggal paket pertanyaan benar-benar dikirim ke tim, bukan tanggal record ini dibuat; kanal pengirimannya dicatat di field (h).

## d. Nama & narahubung penanggung jawab di tim tersebut

```text
_(belum diisi)_ — nama: ......  · jabatan/tim: ......  · kanal: ......
```

> Petunjuk: nama orang yang menjawab, bukan nama tim saja; bila proyek tidak punya tim hukum formal, tulis pemilik proyek yang menyatakan bertindak atas nama mereka — jangan mengarang pihak lain.

## e. Keputusan akhir

```text
_(belum diisi)_
```

- [ ] **disetujui**
- [ ] **ditolak**
- [ ] **bersyarat**

> Petunjuk: centang tepat satu; pilihan "bersyarat" wajib punya isi di field (f) — bila (f) kosong, keputusan belum lengkap dan record tidak boleh ditutup.

## f. Syarat & batasan

```text
_(belum diisi)_ — ......
```

> Petunjuk: sebutkan kewajiban yang mengikat (atribusi, larangan redistribusi/bentuk tertentu, berkas yang wajib diubah, tenggat); tulis "tidak ada" bila memang tidak ada — jangan biarkan kosong.

## g. Tanggal berlaku

```text
_(belum diisi)_ — YYYY-MM-DD atau pemicu: ......
```

> Petunjuk: tanggal berlaku, atau pemicu yang bisa diverifikasi dari repo/rilis (mis. "saat APK pertama dibagikan"); pemicu yang tidak bisa dicek dianggap belum dijawab.

## h. Lampiran/referensi

```text
_(belum diisi)_ — tautan: ......
```

> Petunjuk: tautkan bukti asli keputusan (komentar kartu, email yang ditempel, commit yang menerapkan) plus dokumen bukti yang dipakai; tautan mati membuat record tidak sah.

## i. Item/pekerjaan yang diblokir (tautan)

| Item / pekerjaan | Tautan | Ditahan oleh |
|---|---|---|
| _(belum diisi)_ | ...... | ...... |

> Petunjuk: daftar pekerjaan yang menunggu keputusan ini beserta tautan kartu/berkasnya; saat record diisi, perbarui status setiap item di tabel ini.

---

## Setelah record terisi

Checklist penutupan lengkap (a–f, termasuk bukti keputusan, status `RESOLVED`, tautan ke
pekerjaan diblokir, dan notifikasi pemilik): [`../LICENSE-DECISION-RECORD.md` §6](../LICENSE-DECISION-RECORD.md#6-prosedur-penutupan--checklist-af).

1. Ganti semua `_(belum diisi)_` — record dengan penanda tersisa dianggap **belum diputuskan**.
2. Tautkan record ini dari setiap item di field (i), lalu perbarui status item tersebut.
3. Bila record menyangkut lisensi Dot. sendiri: jalankan `bash scripts/license-status.sh`
   (exit `0` hanya bila `LICENSE` ada, `NOTICE`/PRD §10 sejalan, dan formulir §3 terisi).
4. Simpan record sebagai berkas, bukan komentar kartu saja — komentar hilang dari pandangan,
   berkas tetap bisa dirujuk.
