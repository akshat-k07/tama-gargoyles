# Tama Gargoyles — DECISIONS

> This document records **explicit technical and design decisions** made during
> development of Tama Gargoyles.
>
> Its purpose is to prevent repeated debate, reduce confusion, and provide
> context for future changes.
>
> If a decision is reversed, update this document.

---

## 1. Server-Rendered UI (Thymeleaf)

**Decision**  
Use **Thymeleaf with Spring MVC** for all UI rendering.

**Reasoning**
- Fits naturally with existing Spring Boot setup
- Easier to explain and reason about for a student project
- Works well with accessibility requirements
- Avoids complexity of client-side state management

**Explicitly Rejected**
- JavaFX (not aligned with web stack, harder to integrate and explain)
- SPA frameworks (React/Vue/etc.)

---

## 2. Virtual Time System (No Background Jobs)

**Decision**  
Virtual time progresses **only when the user visits `/game`**, not via schedulers.

**Reasoning**
- Avoids background jobs and cron complexity
- Easier to test and reason about
- Prevents unexpected stat decay while user is inactive
- Common pattern for pet-style games

**Implication**
- Hunger and happiness only change when `/game` is visited
- `/battle` does not advance virtual time

---

## 3. Pause / Resume Behaviour

**Decision**  
Paused gargoyles do not accumulate time decay.
Resuming resets `lastTickAt` to “now”.

**Reasoning**
- Prevents massive stat drops after long absences
- Makes behaviour predictable for users
- Simplifies time calculations

---

## 4. Lifecycle & Adulthood Model

**Decision**  
There is **no explicit ADULT enum**.
Adulthood is **derived from virtual age**.

**Current Rules**
- Gargoyle starts as `CHILD`
- At `gameDaysOld >= 3`, a CHILD is promoted to:
    - `GOOD` if hunger and happiness ≥ 60
    - `BAD` otherwise
- GOOD and BAD represent adulthood in practice

**Reasoning**
- Keeps lifecycle simple
- Avoids introducing redundant enum states
- Allows behaviour to be driven by player care

---

## 5. Adult-Only Battle Access

**Decision**  
Battle eligibility is determined by **virtual age**, not type.

**Current Behaviour**
- A gargoyle can battle if `gameDaysOld >= 3`
- Battle controller checks age only
- Promotion to GOOD/BAD happens in `/game`

**Known Nuance**
- A sufficiently old CHILD could battle if promotion hasn’t run yet

**Open Decision**
The team still needs to agree whether “adult” should mean:
- age only
- type only (GOOD/BAD)
- or both

---

## 6. Battle System Scope (MVP)

**Decision**  
Implement a **simple, session-based battle system**.

**Details**
- Turn-based
- Rock–paper–scissors style moves:
    - SLAM, DASH, SNEAK
- First to 3 points wins
- Opponent move is random
- Battle state stored in session, not database

**Reasoning**
- Minimises persistence complexity
- Easy to test and reason about
- Suitable for MVP and assessment scope

---

## 7. Separation of Game and Battle Modes

**Decision**  
`/game` and `/battle` are treated as **separate modes**.

**Rules**
- `/game`:
    - Handles time progression
    - Handles hunger/happiness decay
    - Handles lifecycle promotion
- `/battle`:
    - Does NOT tick virtual time
    - Focuses only on battle logic

**Reasoning**
- Prevents surprise stat decay mid-battle
- Keeps responsibilities clear
- Simplifies testing

---

## 8. Gargoyle Selection Strategy

**Decision**  
When multiple gargoyles exist:
- Prefer a `CHILD` gargoyle
- Otherwise select the first by ID

**Reasoning**
- Encourages progression of younger gargoyles
- Keeps selection logic simple

**Known Trade-off**
- Older GOOD/BAD gargoyles may not tick if a CHILD exists
- Accepted for MVP

---

## 9. Accessibility as a First-Class Concern

**Decision**  
Accessibility requirements are baked into UI decisions.

**Practices**
- Keyboard-accessible controls
- Visible focus states
- Labels and text (not colour-only)
- `<fieldset>` / `<legend>` for grouped inputs
- `aria-live="polite"` for battle updates

**Reasoning**
- Required by assessment criteria
- Improves overall UX
- No significant implementation cost

---

## 10. Testing Strategy

**Decision**
Use **controller-focused tests** with Spring Security enabled.

**Tools**
- `@WebMvcTest`
- `spring-security-test`
- OAuth2 login simulated with `oauth2Login()`
- Services and repositories mocked

**Reasoning**
- Prevents slow, brittle integration tests
- Reduces merge conflicts
- Keeps feedback fast

---

## 11. Known Follow-Up Decisions

The following are intentionally unresolved:
- Canonical definition of “adult”
- Centralising adulthood thresholds into constants
- Graceful handling of invalid battle moves
- Long-term role of Selenium tests

These should be addressed explicitly and recorded here when decided.

---

## Stat-Based Growth & Death (Deferred)

**Decision**
Stat-based growth (food/games affecting strength, speed, intelligence),
health decay from neglect, and permanent death are deferred beyond MVP.

**Reasoning**
- Core pet loop and battle mechanics are the assessment priority
- Implementing these systems cleanly would significantly increase scope
- Stats already exist, making future implementation possible without refactor

This decision may be revisited if time allows.


---

**This file exists to save time and avoid rework.  
If you find yourself re-debating something, check here first.**
