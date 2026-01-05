# Tama Gargoyles — CURRENT_STATE

> **Single source of truth for the Tama Gargoyles project**  
> This document reflects what is **actually implemented** plus clearly marked
> **known risks and decisions still to be made**.
>
> If behaviour changes, this document must be updated.

---

## 1. Confirmed Systems (Implemented)

### Authentication & Authorization
- OAuth2 login via **Auth0** using **Spring Security**
- All gameplay routes require authentication
- Users are redirected back to the app after login
- Logout clears the local session and logs out of Auth0 via `/v2/logout`
- Security filters are enabled in all environments, including tests

---

### User System
- Users are created automatically on first login
- Identity is based on the Auth0 email claim
- Username derivation order:
    1. Custom Auth0 claim `https://myapp.com/username`
    2. `preferred_username`
    3. Email prefix
- Username uniqueness enforced by appending numbers if needed
- Users have inventory fields:
    - `rocks`
    - `bugs`
    - `mystery_food`
- Inventory fields initialise to `0`

---

### Gargoyle (Pet) System
- Each user owns **one or more Gargoyles**
- If a user has **no gargoyles**, one is automatically created on first visit to `/game`
- Gargoyles are persisted in **PostgreSQL**
- Gargoyles have stats:
    - hunger (0–100)
    - happiness (0–100)
    - health
    - experience
    - strength
    - speed
    - intelligence
- Gargoyles have:
    - `type` (`CHILD`, `GOOD`, `BAD`)
    - `status`
    - virtual-time fields (`activeMinutes`, `lastTickAt`, pause flags)

---

## 2. Lifecycle & Adulthood (Implemented)

### Gargoyle Creation
- New gargoyle created via `new Gargoyle(user)`
- Initial name: `"Egg-" + user.getId()` (safe with UNIQUE constraint)
- Initial type: `CHILD`

---

### Stat Growth & Death (Not Yet Implemented)

Design intent (from team rules):
- Different foods and games affect strength, speed, and intelligence
- Long-term neglect reduces health
- Gargoyles can die if health reaches 0

Current implementation:
- Food and play affect hunger and happiness only
- Stats exist but do not yet change via food or games
- Health loss occurs only via battle
- Death is not implemented

These mechanics are considered **future scope**, not MVP.

---

### Virtual-Time Lifecycle
- There is **no explicit ADULT enum**
- “Adulthood” is derived from **virtual age**
- Virtual age is calculated from `activeMinutes`

---

### Promotion Logic (runs in `/game`)
- After time tick, if:
    - `type == CHILD`
    - `gameDaysOld >= 3`
- Gargoyle is promoted to:
    - `GOOD` if `happiness >= 60 && hunger >= 60`
    - `BAD` otherwise

GOOD/BAD represent adulthood in practice.

---

## 3. Virtual Time System (Implemented)

### Time Tracking
- Implemented via `GargoyleTimeService`
- Uses real clock time (`Instant`)
- Time advances only if:
    - gargoyle is **not paused**
    - elapsed minutes > 0

---

### Decay Rules
While active:
- `activeMinutes += elapsedMinutes`
- hunger decreases by `minutes * 2`
- happiness decreases by `minutes * 3`
- if hunger < 30:
    - happiness decreases by an additional `minutes`

---

### Pause / Resume
- `pause()` freezes time progression
- `resume()` resets `lastTickAt` to now
- Prevents large decay after being offline

---

### Separation of Concerns
- Virtual time **only updates in `/game`**
- `/battle` does **not** advance hunger or happiness
- Battle is treated as a separate game mode

---

## 4. Game Flow (`/game`) — Implemented

**Controller:** `GargoyleController#game`

1. Resolve current user
2. Load gargoyles ordered by ID
3. Auto-create gargoyle if none exist
4. Select active gargoyle:
    - Prefer `CHILD` if present
    - Else first by ID
5. Apply time updates (strict order):
    - `resume(g)`
    - `tick(g)`
6. Apply promotion logic (see Lifecycle)
7. Save gargoyle
8. Add model attributes:
    - `gargoyle`
    - `gameDaysOld`
    - `minutesIntoDay`
9. Render `game.html`

---

### Stat Interaction Endpoints
- `POST /hunger-increase`
- `POST /hunger-decrease`
- `POST /entertainment-increase`
- `POST /entertainment-decrease`
- Values clamped to `0–100`
- Redirect back to `/game`

---

### Pause Endpoint
- `POST /gargoyles/pause`
- Uses same selection logic as `/game`
- Redirects to `/`

---

## 5. Battle System (MVP — Implemented)

### Core Mechanics
- Turn-based, rock–paper–scissors style
- Moves:
    - `SLAM`
    - `DASH`
    - `SNEAK`
- Resolution:
    - SLAM beats DASH
    - DASH beats SNEAK
    - SNEAK beats SLAM
    - Same move → DRAW
- First to **3 points** wins

---

### Battle State
- Stored **in session** (not persisted)
- Each user has their own `BattleState`
- Tracks:
    - scores
    - last moves
    - last outcome
    - finished flag

---

### Adult-only Access (Actual Behaviour)
A gargoyle may battle if:
- `gameDaysOld >= 3`

⚠️ Note:
- Battle eligibility checks **age**, not type
- Promotion to GOOD/BAD happens only in `/game`

---

### Battle Routes
- `GET /battle`
    - Requires eligible gargoyle by age
    - Calls `resume()` → `tick()` → save
    - Renders `battle.html`
- `POST /battle/move`
    - Validates eligibility
    - Plays one turn
    - Redirects to `/battle`
- `POST /battle/reset`
    - Resets battle state

---

## 6. Battle UI (`battle.html`) — Implemented

### Displays
- Gargoyle name, type, status
- Player vs opponent score
- Last turn summary
- Winner message when finished
- Static rules text

---

### Inputs
- Buttons submit moves (`SLAM`, `DASH`, `SNEAK`)
- CSRF tokens included
- When finished:
    - Move buttons hidden
    - Reset button shown

---

## 7. UI Layer (Confirmed Direction)

- Server-rendered HTML using **Thymeleaf**
- No frontend JS framework
- JavaFX is **not used** and **not planned**

### Screens
- Home (`/`)
    - Login / Logout
    - Link to game
- Game (`/game`)
    - Gargoyle stats + care actions
    - Conditional “Go to Battle”
- Battle (`/battle`)
    - Turn-based battle screen

---

### Accessibility Commitments
- Keyboard-accessible controls
- Visible focus states
- Text + labels (not colour-only)
- `<fieldset>` / `<legend>` for move selection
- `aria-live="polite"` for battle log updates

---

## 8. Technical Stack

- Java 21
- Spring Boot 3.5.x
- Spring MVC
- Spring Security + OAuth2 Client
- Thymeleaf
- Spring Data JPA / Hibernate
- Flyway
- PostgreSQL
- HikariCP
- Maven
- Embedded Tomcat

---

## 9. Testing

- JUnit 5
- Mockito
- `@WebMvcTest`
- `spring-security-test`
- OAuth2 login simulated via `oauth2Login()`
- Selenium dependency present (usage unclear)

---

## 10. Known Risks & Decisions Needed

### Adulthood Definition
- “Adult” sometimes means GOOD/BAD
- Battle checks **age only**
  ➡️ Team must decide:
- adult-by-age
- adult-by-type
- or both

---

### Hardcoded Thresholds
- `>= 3 game days` appears in multiple places
- One comment mentions “5 days”
  ➡️ Should be centralised to a constant

---

### Battle Input Safety
- `BattleMove.valueOf(move)` can throw on invalid input
  ➡️ Needs graceful handling

---

### Gargoyle Selection Bias
- `/game` always prefers CHILD if one exists
- Older GOOD/BAD gargoyles may not tick by default
  ➡️ Acceptable for MVP, but must be agreed

---

**This document is the canonical reference for the team.**
Any behavioural change must be reflected here.
