# Tama Gargoyles — NEXT_STEPS

> This document lists the **next concrete steps** for the Tama Gargoyles project,
> ordered to maximise progress while protecting scope and minimising rework.
>
> It assumes `CURRENT_STATE.md` and `DECISIONS.md` are up to date and canonical.

---

## Phase 1 — Lock Core Rules (Highest Priority)

These decisions unblock UI consistency, testing, and demos.

### 1. Decide the canonical definition of “Adult”
**Current behaviour**
- Promotion to GOOD/BAD happens in `/game`
- Battle eligibility checks **age only** (`gameDaysOld >= 3`)

**Decision needed**
Choose **one**:
- Adult = age only (current behaviour)
- Adult = GOOD/BAD type only
- Adult = age **and** type

**Action**
- Agree as a team
- Update battle eligibility logic and `/game` UI if required

---

### 2. Centralise adulthood thresholds
**Problem**
- `>= 3 game days` appears in multiple places
- One comment mentions “5 days”

**Action**
- Introduce a single constant (e.g. `ADULT_AT_GAME_DAYS`)
- Replace all magic numbers
- Add a small unit test to lock behaviour

---

## Phase 2 — UI Consistency & Safety

These tasks improve clarity and prevent user confusion.

### 3. Align `/game` UI with battle eligibility
**Action**
- Show “Go to Battle” button **only if server rules allow**
- Otherwise show:
  > “Battle unlocks when your gargoyle becomes an adult”

**Outcome**
- No mismatch between UI and server validation

---

### 4. Harden battle move input
**Problem**
- `BattleMove.valueOf(move)` throws on invalid input

**Action**
- Add validation / safe parsing
- Redirect back to `/battle` with a user-friendly message if invalid

---

## Phase 3 — Testing & Confidence

These steps reduce regressions and improve demo reliability.

### 5. Add focused battle tests
**Suggested tests**
- Adult-eligible gargoyle can access `/battle`
- Non-adult gargoyle is redirected to `/game`
- Battle ends at 3 points
- Battle reset clears state

**Guidelines**
- One test class per feature area
- Avoid shared “god test” files

---

### 6. Decide Selenium’s role
**Decision needed**
Choose **one**:
- Remove Selenium dependency
- Keep Selenium and add one simple happy-path smoke test

---

## Phase 4 — Optional Gameplay Extensions (Not MVP)

These features match the original game vision but are **explicitly out of MVP scope** unless the team agrees otherwise.

### 7. Stat-based evolution (future scope)
**Description**
- Food affecting strength / speed / intelligence
- Games affecting stats
- Evolution driven by dominant stats

**Status**
- Not implemented
- No action unless explicitly promoted into MVP

---

### 8. Health decay & death (future scope)
**Description**
- Health loss from neglect
- Permanent death when health reaches 0

**Status**
- Health loss only occurs in battle
- Death is not implemented

---

## Phase 5 — Polish (Stretch)

Only attempt once earlier phases are complete.

### 9. Make BAD behaviour more visible
- UI message or flavour text when disobedience occurs

---

### 10. Review gargoyle selection strategy
**Question**
- Should `/game` always prefer a CHILD if one exists?

**Options**
- Keep current behaviour (MVP)
- Allow user to choose active gargoyle

---

## Working Agreement

- Behaviour change → update `CURRENT_STATE.md`
- Decision made → update `DECISIONS.md`
- Task completed → update this file

**This file is the team’s shared, forward-looking action list.**
