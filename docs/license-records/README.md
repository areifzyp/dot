# License Decision Records — Dot.

Satu berkas per keputusan lisensi yang keluar dari tim hukum/kepatuhan. Berkas di sini adalah
**record terisi**, bukan template.

| Berkas | Komponen | Keputusan | Tanggal berlaku | Ditautkan dari |
|---|---|---|---|---|
| _(belum ada)_ | — | — | — | — |

## Cara membuat record baru

1. Salin [`../templates/LICENSE-DECISION-RECORD.template.md`](../templates/LICENSE-DECISION-RECORD.template.md)
   menjadi `<slug-komponen>-<YYYY-MM-DD>.md` di folder ini.
2. Isi field (a)–(i); jangan hapus baris field. Aturan pengisian ada di bagian atas template.
3. Tambahkan satu baris ke tabel di atas, lalu tautkan record dari item/pekerjaan yang diblokir
   (field i) dan perbarui status item tersebut.

## Hubungan dengan dokumen lain

- **Prosedur penutupan** (checklist a–f: record, bukti, status RESOLVED, tautan, notifikasi):
  [`../LICENSE-DECISION-RECORD.md` §6](../LICENSE-DECISION-RECORD.md#6-prosedur-penutupan--checklist-af).
  Jalankan checklist itu — bukan urutan di atas saja — sebelum item ditutup.
- **Lisensi Dot. sendiri** punya formulir kanonik sendiri di
  [`../LICENSE-DECISION-RECORD.md` §3](../LICENSE-DECISION-RECORD.md#3-license-decision-record-formulir);
  keputusan untuk lisensi Dot. diisi **di sana**, bukan sebagai berkas baru di folder ini.
- Paket bukti & daftar pertanyaan: [`../LICENSE-DECISION-REQUEST.md`](../LICENSE-DECISION-REQUEST.md).
- Gerbang otomatis status lisensi Dot.: `bash scripts/license-status.sh` (exit `0` = selesai).
