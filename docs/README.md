# Dot. — Project Playbook

Living, versioned rules for the Dot. development project. This README is the
index into those rules; individual rules live in their own files under
`docs/`.

## Rules

1. **One task per session** → see `../AGENTS.md` §Conventions.
2. **Immutable Application ID** → `io.github.areifzyp.dot` (see `../AGENTS.md`).
3. **Release cadence** → PRD 9.1: Phase 1 = internal debug; Phase 2/3 =
   signed APK via GitHub workflow dispatch; Phase 4 = Play Store.
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
