# AGENTS.md — AI Coding Contract for Dot.

This file is the standing contract for any AI agent working in this
repository. See `.cursor/rules.md` for the human-facing summary; keep both
in sync. When they disagree, AGENTS.md governs agent behavior.

## Project identity

- App name: Dot.
- App version: read from `VERSION` at repo root (SSOT); never hardcode the
  number in docs.
- Package / Application ID: `io.github.areifzyp.dot` (PRD §9 "Pertimbangan
  Teknis" — immutable once the first build succeeds; do **not** change).
- Min SDK: 26 | Compile/Target SDK: 34 (PRD §7 "Persyaratan Non-Fungsional";
  SDK bump is deliberate).
- Stack: Kotlin 2.x, Jetpack Compose, Coroutines, Material 3, Glance widgets.

## Conventions

1. One small task per agent session (PRD §11 butir 3). Commit at the end of
   session; do not leave WIP commits on long-lived branches.
2. Branch per task: `feature/<short-slug>` off `main`; merge via PR through
   the GitHub CLI.
3. All new code must be exercised by a test or lint gate before the session
   ends. Unverified `[x]` boxes are forbidden.
4. No plaintext secrets. Signing config is injected by CI from GitHub
   secrets: `SIGNING_STORE_BASE64`, `SIGNING_STORE_PASSWORD`,
   `SIGNING_KEY_ALIAS`, `SIGNING_KEY_PASSWORD` (PRD §10 "Repo, Lisensi, dan
   Distribusi" — Keamanan repo).
5. Commits follow Conventional Commits (`feat:`, `fix:`, `chore:`, `docs:`,
   `test:`). No WIP commits.

## Tooling

- Local build: `cd android && ./gradlew :app:assembleDebug` (requires JDK 21)
- Unit tests: `./gradlew :app:testDebugUnitTest`
- CI: `.github/workflows/ci.yml` runs on every push/PR.
- Obsidian project state: `Documents/Obsidian Vault/Hermes/projects/dot/`

## Handoff

At the end of each session the agent updates `status.md` Next / Done, fills
the session note, and closes the session via the `obsidian-workflow` skill.
If blocked, add a blocker to the session note and leave an open `[ ]`
Next Action.
