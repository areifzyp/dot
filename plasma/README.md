# Dot KDE Plasma Widgets

Implementasi widget Dot (Nothing-style) untuk KDE Plasma 6 (Plasmoid).

## Struktur
- `org.dot.clock/`: Widget Jam Digital Nothing (QML + Doto Font).
- `org.dot.weather/`: Widget Cuaca Nothing (QML + Open-Meteo API + Doto Font).

## Instalasi Pengembangan (Lokal)
```bash
plasmapkg2 --install org.dot.clock
plasmapkg2 --install org.dot.weather
# atau untuk update:
plasmapkg2 --upgrade org.dot.clock
plasmapkg2 --upgrade org.dot.weather
```
