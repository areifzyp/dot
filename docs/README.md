# Dot. — Project Playbook

Living, versioned rules for the Dot. development project. This README is the
index into those rules; individual rules live in their own files under
`docs/`.

## Rules

1. **One task per session** → see `../AGENTS.md` §Conventions.
2. **Immutable Application ID** → `io.github.areifzyp.dot` (see `../AGENTS.md`).
3. **Release cadence** → PRD §10 "Repo, Lisensi, dan Distribusi": selama
   private, APK dipasang langsung; Phase 1–4 lihat PRD §5 (Ruang Lingkup dan
   Hasil Rilis).
4. **Design handoff** → all token values live in `../design/tokens.json`;
   the Compose theme imports a file generated from it, never hand-written magic
   numbers. Generator kanonik belum ada — lihat risiko terbuka di
   `STATUS-MAP.md` §4c.

## Folders

```
docs/          ← these rules + the PRD
design/        ← design tokens & assets
android/       ← Android app (Gradle monorepo module)
.github/       ← CI workflows
```

## Version pin

App version: **read from `VERSION` at repo root (SSOT)** — currently `0.5.0`,
verified at commit `e1c7f9c`. Never hardcode the version number in docs; quote
`VERSION` with the verification commit. PRD document version (1.0) is separate
from the app version. Peta paragraf & kontrol rujukan: `PRD-PETA.md`.
