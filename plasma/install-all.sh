#!/usr/bin/env bash
# Instal semua widget Dot untuk KDE Plasma 6.
#
# Pola aman per plasmoid (pemeriksaan 23 Okt 2026, kartu t_a71f5814):
#   1. salin ke direktori staging tersembunyi di DEST_DIR (filesystem sama,
#      sehingga tukar akhirnya adalah rename atomik),
#   2. validasi manifes: metadata.json, contents/ui/main.qml,
#      contents/fonts/Doto.ttf — satu hilang = gagal,
#   3. baru tukar ke tempatnya; plasmoid lama hanya dihapus SETELAH salinan
#      baru terverifikasi. Kegagalan cp tidak pernah meninggalkan tujuan
#      kosong, dan salinan lama tetap utuh (masih dimuat plasmashell).
#
# "installed successfully" hanya dicetak bila KEENAM plasmoid terpasang
# tervalidasi. (Refresh KPackage index tidak diperlukan — verifikasi 22–23
# Okt 2026: kpackagetool6 -t Plasma/Applet --list mengenali plasmoid baru
# tanpa kbuildsycoca6 maupun restart plasmashell; STATUS-MAP §2.)
set -euo pipefail

# DOT_PLASMOID_DEST: penunjuk opsional untuk pengujian (default: lokasi asli).
DEST_DIR="${DOT_PLASMOID_DEST:-$HOME/.local/share/plasma/plasmoids}"
mkdir -p "$DEST_DIR"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

WIDGETS=(org.dot.clock org.dot.weather org.dot.calendar org.dot.system org.dot.music org.dot.text)

# Manifes wajib tiap plasmoid (cek pasca-salin sebelum tukar).
REQUIRED_FILES=(metadata.json contents/ui/main.qml contents/fonts/Doto.ttf)

STAGE_DIR=""
cleanup() {
    if [ -n "$STAGE_DIR" ] && [ -d "$STAGE_DIR" ]; then
        rm -rf "$STAGE_DIR"
    fi
}
trap cleanup EXIT

echo "Installing all Dot KDE Plasma 6 Widgets..."
for widget in "${WIDGETS[@]}"; do
    echo "Installing $widget to $DEST_DIR/$widget..."

    STAGE_DIR=""
    if ! STAGE_DIR="$(mktemp -d "$DEST_DIR/.dot-install.XXXXXX")"; then
        echo "ERROR: cannot create staging dir in $DEST_DIR; aborting." >&2
        exit 1
    fi

    if ! cp -r "$SCRIPT_DIR/$widget" "$STAGE_DIR/"; then
        echo "ERROR: copy of $widget failed; existing install (if any) left untouched." >&2
        exit 1
    fi

    for rel in "${REQUIRED_FILES[@]}"; do
        if [ ! -f "$STAGE_DIR/$widget/$rel" ]; then
            echo "ERROR: $widget is incomplete — missing required file '$rel'; existing install (if any) left untouched." >&2
            exit 1
        fi
    done

    # Salinan baru terverifikasi: tukar ke tempatnya. Plasmoid lama baru
    # dihapus di sini, setelah verifikasi (bukan sebelum cp).
    if [ -e "$DEST_DIR/$widget" ]; then
        rm -rf "${DEST_DIR:?}/$widget"
    fi
    mv "$STAGE_DIR/$widget" "$DEST_DIR/$widget"
    rmdir "$STAGE_DIR"
    STAGE_DIR=""
done

echo "All Dot Plasma widgets installed successfully!"
