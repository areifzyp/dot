#!/usr/bin/env bash
set -e

DEST_DIR="$HOME/.local/share/plasma/plasmoids"
mkdir -p "$DEST_DIR"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "Installing all Dot KDE Plasma 6 Widgets..."
for widget in org.dot.clock org.dot.weather org.dot.calendar org.dot.system org.dot.music org.dot.text; do
    echo "Installing $widget to $DEST_DIR/$widget..."
    rm -rf "$DEST_DIR/$widget"
    cp -r "$SCRIPT_DIR/$widget" "$DEST_DIR/"
done

echo "Refreshing KPackage index..."

echo "All Dot Plasma widgets installed successfully!"
