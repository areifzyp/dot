#!/usr/bin/env bash
# license-status.sh — cek status keputusan lisensi Dot. tanpa jaringan, tanpa menulis apa pun.
#
# Pemakaian:
#   bash scripts/license-status.sh            # ringkasan status; exit 1 bila masih ada pekerjaan
#   bash scripts/license-status.sh --quiet     # senyap bila sudah selesai (dipakai cron)
#
# Exit 0 hanya bila:
#   1. berkas LICENSE (atau COPYING) ada di akar repo,
#   2. NOTICE dan PRD §10 menyatakan keluarga lisensi yang sama dengan LICENSE,
#   3. formulir §3 docs/LICENSE-DECISION-RECORD.md sudah terisi (tidak ada penanda
#      "_(belum diisi)_").
# Metadata plasmoid hanya dilaporkan sebagai catatan, tidak menahan: D3 boleh memutuskan
# plasmoid tetap MIT di repo yang lisensi utamanya lain.
#
# Rujukan: docs/LICENSE-DECISION-RECORD.md §1.1 dan §5.

set -uo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
RECORD="$ROOT/docs/LICENSE-DECISION-RECORD.md"
PRD="$ROOT/docs/PRD-Dot.md"
QUIET=0
[ "${1:-}" = "--quiet" ] && QUIET=1

problems=()
warnings=()

# --- 1. berkas LICENSE -------------------------------------------------------
license_file=""
if [ -f "$ROOT/LICENSE" ]; then
  license_file="LICENSE"
elif [ -f "$ROOT/COPYING" ]; then
  license_file="COPYING"
else
  license_file="$(git -C "$ROOT" ls-tree --name-only HEAD 2>/dev/null \
    | grep -iE '^(LICENSE|COPYING)' | head -1 || true)"
fi

family_of() { # $1 = teks → apache | gpl | mit | lain
  local t="$1"
  if grep -qiE 'apache license|Apache-2\.0' <<<"$t"; then echo apache
  elif grep -qiE 'GNU GENERAL PUBLIC LICENSE|GPL-[0-9]' <<<"$t"; then echo gpl
  elif grep -qiE 'MIT License|\bMIT\b|Permission is hereby granted, free of charge' <<<"$t"; then echo mit
  else echo lain; fi
}

license_family="tidak ada"
if [ -n "$license_file" ] && [ -f "$ROOT/$license_file" ]; then
  license_family="$(family_of "$(head -40 "$ROOT/$license_file")")"
elif [ -n "$license_file" ]; then
  license_family="$(family_of "$(git -C "$ROOT" show "HEAD:$license_file" 2>/dev/null | head -40)")"
else
  problems+=("LICENSE: tidak ada berkas LICENSE/COPYING di akar repo")
fi

license_display="tidak ada"
[ -n "$license_file" ] && license_display="${license_file} (${license_family})"

# --- 2a. NOTICE --------------------------------------------------------------
notice_family="tidak ada"
if [ -f "$ROOT/NOTICE" ]; then
  notice_family="$(family_of "$(head -20 "$ROOT/NOTICE")")"
  [ "$notice_family" = "lain" ] && notice_family="tidak dikenali"
fi

# --- 2b. PRD §10 -------------------------------------------------------------
# Yang dinilai hanya klaimnya ("**Lisensi (keputusan pemilik):** GPL-2.0-or-later ..."),
# bukan seluruh baris: baris itu juga menyebut Apache-2.0 saat menjelaskan dependensi.
prd_line="$(grep -n -m1 'Lisensi (keputusan pemilik)' "$PRD" 2>/dev/null || true)"
prd_claim=""
prd_family="tidak ditemukan"
if [ -n "$prd_line" ]; then
  prd_claim="$(printf '%s\n' "${prd_line#*:}" \
    | sed -E 's/.*keputusan pemilik\):\*\*[[:space:]]*//' | cut -c1-60)"
  prd_family="$(family_of "$prd_claim")"
  [ "$prd_family" = "lain" ] && prd_family="tidak dikenali"
fi

# --- 2c. metadata plasmoid ---------------------------------------------------
plasmoid_summary="tidak ada berkas"
plasmoid_family="tidak ada"
shopt -s nullglob
plasmoid_files=("$ROOT"/plasma/*/metadata.json)
shopt -u nullglob
if [ "${#plasmoid_files[@]}" -gt 0 ]; then
  plasmoid_values="$(grep -h '"License"' "${plasmoid_files[@]}" 2>/dev/null \
    | sed -E 's/.*"License"[[:space:]]*:[[:space:]]*"([^"]+)".*/\1/' | sort | uniq -c \
    | awk '{printf "%s%s× %s", (NR>1 ? ", " : ""), $1, $2}')"
  plasmoid_summary="${#plasmoid_files[@]} berkas → ${plasmoid_values:-tidak ada field License}"
  distinct="$(grep -h '"License"' "${plasmoid_files[@]}" 2>/dev/null \
    | sed -E 's/.*"License"[[:space:]]*:[[:space:]]*"([^"]+)".*/\1/' | sort -u | wc -l)"
  if [ "$distinct" -eq 1 ]; then
    plasmoid_family="$(family_of "$(grep -h '"License"' "${plasmoid_files[@]}" | head -1)")"
    [ "$plasmoid_family" = "lain" ] && plasmoid_family="$(grep -h '"License"' "${plasmoid_files[@]}" \
      | sed -E 's/.*"License"[[:space:]]*:[[:space:]]*"([^"]+)".*/\1/' | head -1)"
  else
    plasmoid_family="campuran"
  fi
fi

# --- 3. formulir §3 ----------------------------------------------------------
record_state="berkas tidak ada"
record_open=1
if [ -f "$RECORD" ]; then
  blanks="$(grep -c '_(belum diisi)_' "$RECORD" || true)"
  if [ "${blanks:-0}" -eq 0 ]; then
    record_state="terisi"
    record_open=0
  else
    record_state="BELUM DIISI (${blanks} field kosong)"
  fi
fi

# --- konsistensi -------------------------------------------------------------
# NOTICE dan PRD §10 wajib sejalan dengan LICENSE: keduanya klaim yang dipelihara kode
# sendiri, jadi selisihnya berarti penerapan keputusan belum selesai.
# Metadata plasmoid sengaja TIDAK memblokir: D3 boleh memutuskan plasmoid tetap MIT di
# dalam repo yang lisensi utamanya lain (docs/LICENSE-DECISION-RECORD.md §4 D3).
if [ "$license_family" != "tidak ada" ]; then
  [ "$notice_family" != "$license_family" ] && \
    problems+=("NOTICE menyatakan '${notice_family}', LICENSE '${license_family}'")
  [ "$prd_family" != "$license_family" ] && \
    problems+=("PRD §10 menyatakan '${prd_family}', LICENSE '${license_family}'")
  [ "$plasmoid_family" != "$license_family" ] && \
    warnings+=("metadata plasmoid '${plasmoid_family}' ≠ LICENSE '${license_family}' — sah hanya bila D3 menyatakan terpisah; kalau tidak, samakan")
fi
[ "$record_open" -ne 0 ] && problems+=("formulir §3 docs/LICENSE-DECISION-RECORD.md belum diisi")

if [ "${#problems[@]}" -eq 0 ]; then
  [ "$QUIET" -eq 0 ] && echo "Dot. — keputusan lisensi SELESAI dan konsisten (${license_file}: ${license_family})"
  exit 0
fi

echo "Dot. — status keputusan lisensi: BELUM SELESAI"
echo
printf '  %-12s : %s\n' "LICENSE"   "$license_display"
printf '  %-12s : %s\n' "NOTICE"    "$notice_family"
printf '  %-12s : %s\n' "PRD 10"    "${prd_family}${prd_line:+  [baris ${prd_line%%:*}] → ${prd_claim}}"
printf '  %-12s : %s\n' "Plasmoid"  "$plasmoid_summary"
printf '  %-12s : %s\n' "Record 3"  "$record_state"
echo
echo "  Yang kurang:"
for p in "${problems[@]}"; do echo "    - $p"; done
if [ "${#warnings[@]}" -gt 0 ]; then
  echo
  echo "  Catatan (tidak menahan, tapi periksa):"
  for w in "${warnings[@]}"; do echo "    - $w"; done
fi
echo
echo "  Yang dibutuhkan dari manusia: D1 (lisensi), D2 (waktu berlaku), D3 (plasmoid)."
echo "  Formulir + daftar pertanyaan: docs/LICENSE-DECISION-RECORD.md §3–§4"
echo "  Bukti + inventaris dependensi: docs/LICENSE-DECISION-REQUEST.md"
exit 1
