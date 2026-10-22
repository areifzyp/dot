#!/usr/bin/env bash
set -e
echo "Installing all Dot KDE Plasma Widgets..."
for widget in org.dot.clock org.dot.weather org.dot.calendar org.dot.system org.dot.music org.dot.text; do
    echo "Installing $widget..."
    plasmapkg2 --install "$widget" || plasmapkg2 --upgrade "$widget"
done
echo "All Dot Plasma widgets installed successfully!"
