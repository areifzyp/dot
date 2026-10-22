# Dot KDE Plasma Widgets

Implementasi widget Dot (Nothing-style) untuk KDE Plasma 6 (Plasmoid).

## Struktur
- `org.dot.clock/`: Widget Jam Digital Nothing (QML + Doto Font).
- `org.dot.weather/`: Widget Cuaca Nothing (QML + Open-Meteo API + Doto Font).
- `org.dot.calendar/`: Widget Kalender Nothing (QML + Doto Font).
- `org.dot.system/`: Widget Status Sistem Nothing (QML + Doto Font).

## Instalasi Pengembangan (Lokal)
```bash
plasmapkg2 --install org.dot.clock
plasmapkg2 --install org.dot.weather
plasmapkg2 --install org.dot.calendar
plasmapkg2 --install org.dot.system
# atau untuk update:
plasmapkg2 --upgrade org.dot.clock
plasmapkg2 --upgrade org.dot.weather
plasmapkg2 --upgrade org.dot.calendar
plasmapkg2 --upgrade org.dot.system
```
