# Tama Gargoyles — Team Workflow Rules (Anti-Chaos)

> This project is hitting merge-conflict pain. This doc sets **simple boundaries**
> so we can work in parallel without breaking each other’s work.
>
> Aim: fewer conflicts, faster PRs, calmer teamwork.

---

## Small note: what “god files” are (and why we avoid them)

A **god file** is one file that tries to do *too much* (e.g. a controller/service/test that handles multiple features).
They cause:
- constant merge conflicts (everyone edits the same file)
- harder debugging (everything is tangled)
- slower progress (fear of touching “the big file”)

**Rule:** keep files small and feature-focused. If a file starts “owning everything”, split it.

---

## 1) Ownership zones (no surprise edits)

To avoid conflicts, we work in **zones**. If you’re changing a zone you don’t “own”, coordinate first (Slack message is enough).

### Recommended zones (based on current structure)
- **Controllers**
    - One controller per screen/feature (e.g. `GargoyleController`, `BattleController`)
- **Services**
    - One service per domain (e.g. `GargoyleTimeService`, `BattleService`)
- **Templates**
    - One person owns a template at a time (`game.html`, `battle.html`, `homepage.html`)
- **Docs**
    - One person owns updates to CURRENT_STATE / DECISIONS / NEXT_STEPS per PR

**Rule:** one PR should ideally touch **one zone** + related tests.

---

## 2) “One feature per PR” rule

A PR should do **one clear thing**:
- “Add battle input validation”
- “Centralise adult threshold constant”
- “Add battle flow feature tests”

Avoid PRs that mix:
- controller changes + service changes + templates + refactors + docs  
  Those are conflict magnets.

---

## 3) File boundaries (keep responsibilities clean)

### Controllers
Controllers should:
- read request params
- call services
- return a view / redirect

Controllers should NOT:
- contain battle rules
- contain time decay logic
- contain complicated branching logic

**Rule of thumb:** if a controller method is getting long or “clever”, push logic into a service.

### Services
Services should:
- contain business logic (time decay, battle resolution)
- be unit testable

Services should NOT:
- know about HTTP request/response
- build view models directly

---

## 4) Testing rule: no “god test” files

**Rule:** one test class per feature area.

Suggested pattern:
- `GameFlowFeatureTest` (controller/flow)
- `BattleFlowFeatureTest` (controller/flow)
- `GargoyleTimeServiceTest` (logic)
- `BattleServiceTest` (logic)

Avoid dumping every test into `AppTest` / `GameTest` etc.

---

## 5) Template safety (avoid conflicts in HTML)

Templates are easy to conflict on. Use this approach:
- 1 person edits a template at a time
- small PRs for template work
- avoid reformatting the whole file (mass whitespace changes create conflicts)

---

## 6) Daily merge discipline (prevents “PR hell”)

**Before you start work**
1. `git checkout main`
2. `git pull`
3. `git checkout -b feature/your-branch`

**Before you push**
1. `git fetch origin`
2. `git rebase origin/main` (or merge if your team prefers)
3. run tests
4. push

**Rule:** if your branch is more than ~1 day old, rebase/merge main before continuing.

---

## 7) Conflict hotspots (special rules)

These files are high-conflict zones:
- controller classes
- service classes
- `SecurityConfig` (if present)
- big templates

**Rules**
- avoid “drive-by edits” in hotspots
- if you must edit, keep the change tiny and focused
- announce in Slack: “I’m editing BattleController for the next 30 mins”

---

## 8) PR checklist (fast review, fewer back-and-forths)

Every PR should include:
- what changed (1–3 bullets)
- how to test it (1–2 bullets)
- what files were touched (optional but helpful)

Example:
- Changed: validate battle move param + flash error
- Test: run `mvn test`, try invalid move manually
- Files: BattleController, battle.html, BattleFlowFeatureTest

---

## 9) When to split a file (simple trigger)

Split a controller/service if:
- it handles more than one major feature area
- it has multiple unrelated responsibilities
- two people keep editing it in parallel
- it’s repeatedly causing merge conflicts

---

## 10) Team agreement (the boundary)

- If a change affects gameplay rules → update `CURRENT_STATE.md`
- If a decision is made → update `DECISIONS.md`
- If a task is done → update `NEXT_STEPS.md`

**This keeps the team aligned without extra meetings.**
