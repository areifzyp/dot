# Dot. — Cursor Rules (AI Agent Contract)

Human-facing summary of the rules every AI agent follows in this repo.
The binding contract is `../AGENTS.md`; keep the two aligned.

## 1. Build & test (mandatory before any session ends)
```
cd android
./gradlew :app:assembleDebug
./gradlew :app:testDebugUnitTest
```
Both must be green. If a build fails, fix it in the same session or note a
blocker in the Obsidian session file.

## 2. Application ID is frozen
`io.github.areifzyp.dot` — never change once the app is installed on a device
(PRD §9, butir "Struktur repo (usulan)").

## 3. Code style
- Kotlin 2.x, official Kotlin coding conventions
- Compose: prefer small, pure `@Composable` functions; no magic numbers —
  read from `theme/Dimens.kt` / `theme/Color.kt`, both generated from
  `design/tokens.json` (generator kanonik belum ada — lihat
  `docs/STATUS-MAP.md` §4c)
- No `TODO` left in committed code without an issue reference

## 4. Commits
- Conventional commits (`feat:`, `fix:`, `chore:`, `docs:`, `test:`)
- One logical change per commit; no WIP commits
- Squash-merge disabled in the repo (preserves per-session rhythm)

## 5. Secrets
Nothing secret in the repo. Signing config is injected by CI from GitHub
secrets: `SIGNING_STORE_BASE64`, `SIGNING_STORE_PASSWORD`, `SIGNING_KEY_ALIAS`,
`SIGNING_KEY_PASSWORD` (PRD §10, butir "Keamanan repo").

## 6. Session state
Agent updates Obsidian project `dot` (`status.md` + session note) at the end
of every session via the obsidian-workflow skill. Never mark `[x]` what is
not verified on disk / in git.
