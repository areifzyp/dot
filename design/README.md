# Design Assets & Tokens

Source of truth for Dot.'s visual language.

- **`tokens.json`** — color, shape, spacing, typography tokens (Doto font, monokrom). SSOT: nilai yang dipakai kode widget hanya boleh berasal dari sini.
- **`fonts/`** — aset font (Doto, lisensi terbuka).
- **`generate_color_kt.py`** — generator kanonik `tokens.json` → `android/.../ui/theme/Color.kt`.

## Alur token (warna)

`design/tokens.json` → `design/generate_color_kt.py` → `Color.kt` (Compose/Glance).
**Color.kt adalah berkas hasil generator** — jangan edit tangan. Ubah warna di
`tokens.json` lalu jalankan:

```
python3 design/generate_color_kt.py          # tulis Color.kt
python3 design/generate_color_kt.py --check  # cek drift tanpa menulis (dipakai CI)
```

CI (`.github/workflows/ci.yml`, step *Token drift gate*) meregenerasi Color.kt
lalu `git diff --exit-code`: bila hasil tidak identik dengan yang ter-commit,
build gagal dan merge terblokir. Ini menutup risiko drift dua arah — termasuk
token baru yang lupa diberi nama Kotlin (generator keluar error, bukan diam).

## Putusan nilai kanonik (pasangan QML yang menyimpang)

Diriwayatkan saat pembuatan generator, 22 Oktober 2026, oleh arsitek
(kartu `t_7fc87ea7`). Keputusan ini kanonik: lane refaktor QML (kartu terpisah)
wajib memetakan QML ke nilai-nilai ini, bukan sebaliknya.

| Pasangan | Keputusan kanonik | Alasan |
|---|---|---|
| QML `#333333` (border 6 plasmoid) vs `dark.surface=#1A1A1A` | peran baru `color.*.border`: `dark=#333333`, `light=#D4D4D4` | `#333333` adalah stroke/border, bukan surface — selama ini tidak ada peran border di skema, sehingga tampak "menyimpang" ke surface. Peran ditambahkan, nilai QML diadopsi untuk dark; light dilengkapi agar sumbu mode utuh. Teks hari non-bulan-berjalan di kalender (`#333333`) juga memetakan ke nilai border (palet monokrom memperbolehkan pemakaian ulang peran sebagai teks muted). |
| QML `#888888` vs `secondary_dark=#999999` | **`#999999`** | `#888888` tidak pernah lewat keputusan palet — nilai ad-hoc transliterasi. QML ganti ke `color.theme.dark.secondary`. |
| QML `radius: 4` pada kontainer vs `shape.radius.widget=16` | **`widget=16`**; peran baru `shape.radius.item=4` untuk elemen kecil (sel hari kalender, badge) | SSOT menang vs prototipe yang belum diuji pasang: radius kontainer adalah peran `widget`. `radius: 2` (sel hari) → `item=4` (pembulatan ke langkah skala terdekat). |
| QML font `pixelSize` hardcode 56/42/22/18/16/14/13/12/11/10 | skala kanonik `font.size`: `display=56, headline=42, title=22, body=16, secondary=14, caption=12, micro=10` | Sepuluh nilai acak dikonsolidasi jadi tujuh langkah. Pemetaan lane QML: 18→body, 13→secondary, 11→caption; sisanya 1:1. QML memakai piksel (≈sp 1:1). |
| `DotAccentOrange` (`#F97316`) ada di Color.kt tetapi tidak di tokens.json | token baru `color.accent.nothing_orange=#F97316` | Repo menang atas dokumen: aksen PRESET_ORANGE dipakai Theme.kt, jadi token harus membawanya — jika tidak, generator akan menghapusnya dan build rusak. |

Catatan struktur: `color.theme.{light,dark}` kini bersumbu mode penuh
(background, foreground, secondary, surface, inactive_dot, border). Aksen tidak
diulang per mode — `color.accent.*` bersifat lintas-mode.

## Sisa & lane berikutnya

- Sisa konsumen mesin: sisi QML (6 plasmoid masih dark-only hardcode) — lane
  refaktor terpisah, lihat [`docs/STATUS-MAP.md`](../docs/STATUS-MAP.md) §4c.
- `font.size` / `shape.radius` / `spacing` belum punya konsumen Kotlin
  (Dimens.kt) — baru dibuat saat Android mengadopsinya, jangan spekulatif.
