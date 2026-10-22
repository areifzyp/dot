# Inventaris lisensi dependensi — cara membaca dan membuat ulang

Dua berkas di folder ini adalah **keluaran mentah** dari graf dependensi Gradle, dicocokkan
dengan blok `<licenses>` pada POM setiap artefak di cache Gradle lokal. Keduanya dipakai
sebagai bukti di [`../LICENSE-DECISION-REQUEST.md`](../LICENSE-DECISION-REQUEST.md) §3.

| Berkas | Konfigurasi Gradle | Artefak | Ikut ke APK? |
|---|---|---|---|
| `android-release-runtime.tsv` | `releaseRuntimeClasspath` | 103 | ya |
| `android-unit-test.tsv` | `releaseUnitTestRuntimeClasspath` | 141 | tidak (tes saja) |

Format: TSV dengan header `group`, `artifact`, `version`, `license`, `license_url`.
Nilai `NO_LICENSE_DECLARED_IN_POM` berarti POM artefak itu tidak memuat blok `<licenses>` —
**bukan** berarti artefak tersebut tanpa lisensi. Enam artefak tes masuk kategori ini dan
belum dikonfirmasi ke upstream (lihat §10 dokumen induk).

## Membuat ulang

```bash
cd android
export JAVA_HOME=~/jdk/jdk-21.0.12.1+1   # build Dot. butuh JDK 21

./gradlew --offline :app:dependencies --configuration releaseRuntimeClasspath \
  > /tmp/releaseRuntimeClasspath.txt
./gradlew --offline :app:dependencies --configuration releaseUnitTestRuntimeClasspath \
  > /tmp/testRuntimeClasspath.txt
```

Lalu untuk setiap koordinat `group:artifact:version` pada keluaran tersebut, ambil POM-nya
dari `~/.gradle/caches/modules-2/files-2.1/<group>/<artifact>/<version>/*/*.pom` dan baca
blok `<licenses><license><name>`. Baris `(c)` (constraint) dilewati; versi akhir diambil
dari sisi kanan `->` bila ada konflik resolusi.

**Kapan harus dibuat ulang:** setiap kali `android/app/build.gradle.kts` menambah,
menghapus, atau menaikkan versi dependensi. Graf ini tidak dijaga oleh CI, jadi berkas
basi tidak akan ketahuan dengan sendirinya.

## Batasan yang diketahui

- Hanya dependensi yang resolvable secara offline di mesin ini. Artefak yang belum pernah
  diunduh tidak muncul; jalankan tanpa `--offline` sekali bila perlu melengkapi cache.
- Lisensi diambil dari POM, bukan dari berkas `LICENSE` di dalam JAR. Untuk artefak tanpa
  deklarasi POM, POM induk atau situs upstream perlu diperiksa manual.
- Graf ini tidak mencakup font (`design/fonts/OFL.txt`) atau aset desain; itu dicatat di
  `NOTICE`.
