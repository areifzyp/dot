#!/usr/bin/env python3
"""Generator kanonik design/tokens.json -> Color.kt.

tokens.json adalah SSOT warna Dot. Berkas ini menghasilkan
android/.../ui/theme/Color.kt yang dipakai Compose/Glance. Color.kt tidak
boleh diedit tangan: setiap perubahan warna dimulai dari tokens.json lalu
dijalankan generator ini. CI menjalankan `--check` (regenerasi + git diff)
sehingga drift antara tokens.json dan Color.kt memblokir merge.

Aturan drift (dua arah):
  - token warna baru tanpa entri NAME_MAP -> error (memaksa keputusan
    penamaan, bukan diam-diam diabaikan).
  - token warna dihapus dari NAME_MAP tetapi masih dipakai Kotlin ->
    hilang dari hasil dan akan ditangkap gate pada CI berikutnya.

Pemakaian:
  python3 design/generate_color_kt.py           # tulis Color.kt di tempat
  python3 design/generate_color_kt.py --check   # bandingkan tanpa menulis; exit 1 bila beda
"""

from __future__ import annotations

import json
import re
import sys
from pathlib import Path

SCRIPT_DIR = Path(__file__).resolve().parent
TOKENS_PATH = SCRIPT_DIR / "tokens.json"
OUT_PATH = (
    SCRIPT_DIR.parent
    / "android/app/src/main/java/io/github/areifzyp/dot/ui/theme/Color.kt"
)

HEX_RE = re.compile(r"^#([0-9A-Fa-f]{6})$")

# Kamus penamaan: path token (dipisah titik) -> nama val Kotlin.
# Urutan menentukan urutan deklarasi di Color.kt; jangan diurutkan ulang
# tanpa regenerasi karena diff hasilnya harus tetap stabil & minim.
NAME_MAP: list[tuple[str, str]] = [
    # Palet dasar — PRD §8.5
    ("color.black", "DotBlack"),
    ("color.white", "DotWhite"),
    ("color.gray.secondary_light", "DotGraySecondaryLight"),
    ("color.gray.secondary_dark", "DotGraySecondaryDark"),
    ("color.gray.inactive", "DotGrayInactive"),
    # Aksen
    ("color.accent.nothing_red", "DotAccentRed"),
    ("color.accent.nothing_orange", "DotAccentOrange"),
    # Light theme
    ("color.theme.light.background", "DotLightBackground"),
    ("color.theme.light.foreground", "DotLightForeground"),
    ("color.theme.light.secondary", "DotLightSecondary"),
    ("color.theme.light.surface", "DotLightSurface"),
    ("color.theme.light.inactive_dot", "DotLightInactiveDot"),
    ("color.theme.light.border", "DotLightBorder"),
    # Dark theme
    ("color.theme.dark.background", "DotDarkBackground"),
    ("color.theme.dark.foreground", "DotDarkForeground"),
    ("color.theme.dark.secondary", "DotDarkSecondary"),
    ("color.theme.dark.surface", "DotDarkSurface"),
    ("color.theme.dark.inactive_dot", "DotDarkInactiveDot"),
    ("color.theme.dark.border", "DotDarkBorder"),
]

SECTION_COMMENTS: dict[str, str] = {
    "color.black": "// Palet dasar Nothing §8.5",
    "color.accent.nothing_red": "// Aksen",
    "color.theme.light.background": "// Light theme",
    "color.theme.dark.background": "// Dark theme",
}


def collect_color_leaves(node: object, prefix: str) -> dict[str, str]:
    """Kumpulkan semua leaf string #RRGGBB di bawah kunci 'color'."""
    leaves: dict[str, str] = {}
    if isinstance(node, dict):
        for key, value in node.items():
            path = f"{prefix}.{key}" if prefix else key
            leaves.update(collect_color_leaves(value, path))
    elif isinstance(node, str) and HEX_RE.match(node):
        leaves[prefix] = node
    return leaves


def render_color(hex_value: str) -> str:
    match = HEX_RE.match(hex_value)
    assert match is not None
    return f"Color(0xFF{match.group(1).upper()})"


def build_source(tokens: dict) -> str:
    version = tokens.get("meta", {}).get("version", "?")
    leaves = collect_color_leaves(tokens.get("color", {}), "color")
    mapped = dict(NAME_MAP)

    unknown = sorted(set(leaves) - set(mapped))
    if unknown:
        sys.exit(
            "ERROR: token warna tanpa entri NAME_MAP (keputusan penamaan belum diambil):\n  "
            + "\n  ".join(unknown)
            + "\nTambahkan ke NAME_MAP di design/generate_color_kt.py."
        )
    stale = sorted(set(mapped) - set(leaves))
    if stale:
        sys.exit(
            "ERROR: entri NAME_MAP tidak lagi ada di tokens.json:\n  "
            + "\n  ".join(stale)
            + "\nHapus entri itu (dan periksa pemakai Kotlin-nya)."
        )

    lines: list[str] = [
        "package io.github.areifzyp.dot.ui.theme",
        "",
        "import androidx.compose.ui.graphics.Color",
        "",
        "// ==============================================================",
        "// BERKAS HASIL GENERATOR — jangan edit tangan.",
        "// SSOT: design/tokens.json v" + version,
        "// Regenerasi: python3 design/generate_color_kt.py",
        "// Drift diblokir CI: .github/workflows/ci.yml (token drift gate).",
        "// ==============================================================",
        "",
    ]

    last_section: str | None = None
    for path, name in NAME_MAP:
        section = SECTION_COMMENTS.get(path)
        # section baru muncul saat path bertemu kunci pertama dari kelompoknya
        if section is not None and section != last_section:
            lines.append(section)
            last_section = section
        lines.append(f"val {name} = {render_color(leaves[path])}")
    lines.append("")
    return "\n".join(lines)


def main() -> int:
    if len(sys.argv) > 2 or (len(sys.argv) == 2 and sys.argv[1] not in {"-c", "--check"}):
        print(__doc__, file=sys.stderr)
        return 2
    check = len(sys.argv) == 2

    tokens = json.loads(TOKENS_PATH.read_text(encoding="utf-8"))
    generated = build_source(tokens)

    if check:
        current = OUT_PATH.read_text(encoding="utf-8") if OUT_PATH.is_file() else ""
        if generated == current:
            print("OK: Color.kt sesuai tokens.json (tidak ada drift).")
            return 0
        print("DRIFT: Color.kt tidak sesuai design/tokens.json. Jalankan generator.", file=sys.stderr)
        return 1

    changed = not OUT_PATH.is_file() or OUT_PATH.read_text(encoding="utf-8") != generated
    OUT_PATH.write_text(generated, encoding="utf-8")
    print(f"{'Diperbarui' if changed else 'Sudah segar'}: {OUT_PATH}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
