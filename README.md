# 🗿 Tama-Gargoyles

**Tama-Gargoyles** is a virtual-pet web game inspired by Tamagotchi, where players raise a magical gargoyle creature over real time.  
Players must balance feeding, play, and care decisions to shape how their gargoyle grows, evolves, and ultimately battles.

Built as a **Spring Boot MVC application** with:
- Virtual time simulation
- Persistent creatures
- OAuth2 (Auth0) authentication
- MVC feature testing

---

## 🎮 Game Rules — Player View

### 🥚 Starting the Game
- When you start, you receive an **egg**
- The egg hatches and you **name your Tama-Gargoyle**
- Once named, it becomes *your responsibility* to care for it

---

### ⏱️ Time & Needs
- As **time passes**, your Tama-Gargoyle:
    - Gets **hungrier**
    - Loses **happiness**
- If hunger stays too low:
    - Happiness decreases **faster**
- If happiness and hunger are neglected:
    - **Health** will decrease
- If health reaches **0**:
    - 💀 Your Tama-Gargoyle dies

---

### 🍎 Feeding
The food you give your Tama-Gargoyle affects how it evolves:

| Food | Effect |
|-----|-------|
| 🪨 Rocks | Increases **Strength**, lowers Speed & Intelligence |
| 🐛 Bugs | Increases **Speed**, lowers Strength & Intelligence |
| 🍎 Fruit | Increases **Intelligence**, lowers Strength & Speed |
| ❓ Mystery Food | Random effects — good *or* bad |

- You have **limited food**, except **Fruit** (infinite)

---

### 🎲 Playing Games
- Playing increases skills:
    - 💪 Strength games → Strength
    - ⚡ Speed games → Speed
    - 🧠 Intelligence games → Intelligence
- If you **don’t play enough**, your gargoyle may grow up **bad**

---

### 🧬 Growth & Evolution
- With good care, your Tama-Gargoyle grows into an **adult**
- Its **strongest stat** influences its evolution type
- Adults can be:
    - **Good** (obedient)
    - **Bad** (may disobey… sometimes helpfully!)

---

### ⚔️ Battle Mode
- Adult gargoyles may battle opponents with **unknown stats**
- Battles are **turn-based**, card-style

#### Battle Rules (Rock-Paper-Scissors Logic):
- 💪 Strength beats ⚡ Speed
- ⚡ Speed beats 🧠 Intelligence
- 🧠 Intelligence beats 💪 Strength

If both players choose the same type:
- The **higher stat wins**

#### Battle Outcomes:
- Win → 🎁 10 random **special foods**
- Lose → ❤️ Health drops by 20%, receive **2 special foods**

---

## 🧑‍💻 Game Rules — Developer View

### 🕒 Virtual Time System
- The game uses **real-world time**
- Creatures age only while **active**
- Time does **not progress while logged out**

Key rules:
1. Resume the creature on login
2. Apply time decay only after resume
3. Prevent “offline punishment”

---

### 🧬 Stat Evolution (Developer Logic)

| Action | Effect |
|------|-------|
| Rocks | +Strength, −Speed |
| Bugs | +Speed, −Strength & Intelligence |
| Fruit | +Intelligence & Speed, −Strength |
| Mystery Food | +2 stats (random), −1 stat (random) |

---

### ⚔️ Battle Logic
- Turn-based
- Rock-Paper-Scissors stat comparison
- “Bad” gargoyles may **ignore commands**
- Health reaches 0 → battle ends

---

## 🧪 Testing Strategy

The project uses **MVC feature tests** to verify game flow:

### Covered Behaviours
- Redirect when user has no gargoyles
- Correct gargoyle selection (Child > others)
- Time logic order: `resume → tick`
- Model attributes rendered correctly

### Example
```java
@WebMvcTest(GargoyleController.class)
class GameFlowFeatureTest {
    // verifies game flow without hitting database
}
```
These tests:
- Mock repositories & services
- Verify **behaviour**, not implementation
- Prevent regressions in time logic

## 🔐 Authentication
- OAuth2 login via **Auth0**
- Users are created automatically on first login
- Logout fully clears both local session and Auth0 session

## 🛠 Tech Stack
- Java 21
- Spring Boot (MVC, Security, JPA)
- Thymeleaf
- PostgreSQL
- Auth0 (OAuth2)
- JUnit 5, Mockito, MockMvc

## 👥 Team & QA Notes
This project is designed to support:
- Feature-based branching
- Isolated testing per controller/service
- Clear behaviour-driven acceptance criteria
QA students may focus on:
- Time-based edge cases
- Stat boundaries (0–100)
- Battle outcome fairness
- Login/logout transitions

## 🚀 Future Ideas
- Battle UI
- Multiple gargoyles per user
- Trading food items
- Visual evolution paths
- Leaderboards

**Good luck raising your Tama-Gargoyle!** 🗿




# 🧪 Controller Testing with Spring Security & Auth0
(Student Guide – read this if your tests suddenly start redirecting or crashing)
This project uses Spring Security + Auth0 + Thymeleaf.
That means controller tests need a little setup, otherwise you’ll hit confusing errors.
This guide shows exactly what to do and why.

## 🚨 Common problems you’ll see
### ❌ Problem 1: Tests redirect instead of returning 200
You expect:
```java 
.andExpect(status().isOk())
```

But you get:
```text 
302 → /oauth2/authorization/auth0
```
👉 This means Spring Security thinks the user is not logged in.

### ❌ Problem 2: Thymeleaf crashes with _csrf.parameterName
Error looks like:
```text 
Exception evaluating SpringEL expression: "_csrf.parameterName"
```
👉 This happens when security is half-enabled (very common in tests).
## ✅ The Golden Rules (memorise these)
### ✅ Rule 1: ALWAYS use `@WithMockUser` in controller tests
This tells Spring Security:
“Pretend a user is logged in.”
```java 
@WithMockUser
@Test
void myTest() throws Exception {
...
}
```
Without this → redirect to Auth0.
---

✅ Rule 2: NEVER disable security filters in MVC tests
❌ Do NOT do this:
```java
@AutoConfigureMockMvc(addFilters = false)
```
Why?
- Spring Security **adds the CSRF token**
- Thymeleaf templates expect `_csrf`
- Turning filters off = missing CSRF = template crash
✅ Leave filters **ON** and mock the user instead.

### ✅ Rule 3: POST requests MUST include CSRF
Every `@PostMapping` test needs this:
```java
.with(csrf())
```
Example:
```java

mockMvc.perform(post("/game/action")
.with(csrf())
.param("delta", "10"))
.andExpect(status().is3xxRedirection());
```
Without CSRF → **403 Forbidden**

### ✅ Rule 4: Mock CurrentUserService correctly
If your controller does this:
```java
User user = currentUserService.getCurrentUser(authentication);
```
Then your test **must** include:
```java
when(currentUserService.getCurrentUser(any()))
.thenReturn(user);
```
If you forget → `NullPointerException`.

## 🧩 Required dependency (check your `pom.xml`)
Make sure this exists:
```xml
<dependency>
<groupId>org.springframework.security</groupId>
<artifactId>spring-security-test</artifactId>
<scope>test</scope>
</dependency>
```
Without it → `@WithMockUser` won’t work.

## 🧱 Standard Controller Test Template (copy this)
```java
@WebMvcTest(SomeController.class)
class SomeControllerTest {

    @Autowired MockMvc mockMvc;

    @MockitoBean CurrentUserService currentUserService;
    @MockitoBean SomeRepository someRepository;

    @Test
    @WithMockUser
    void GET_page_rendersSuccessfully() throws Exception {
        User u = new User();
        u.setId(1L);

        when(currentUserService.getCurrentUser(any()))
                .thenReturn(u);

        mockMvc.perform(get("/some-page"))
                .andExpect(status().isOk())
                .andExpect(view().name("some-page"));
    }
}
```
🧠 Mental model (important for learning)
Think of controller tests like this:

| Layer      | Real?    | Why                        |
| ---------- | -------- | -------------------------- |
| Controller | ✅ real   | This is what we’re testing |
| Security   | ✅ real   | Needed for Auth + CSRF     |
| User       | ❌ fake   | `@WithMockUser`            |
| Services   | ❌ mocked | Fast + predictable         |
| Database   | ❌ mocked | No real data               |

If any part is half-real / half-fake → weird errors.

---

## 🛟 Quick “something broke” checklist
If a controller test fails, ask:
1. ❓ Do I have `@WithMockUser`?
2. ❓ Is this a POST without `.with(csrf())`?
3. ❓ Did I accidentally disable filters?
4. ❓ Did I mock `CurrentUserService.getCurrentUser(any())`?
5. ❓ Am I testing the right controller in `@WebMvcTest(...)`?
99% of issues are one of these.

---
## 🎯 Final reassurance
If you hit these issues:
- **You didn’t do anything wrong**
- This is normal when adding security
- Every professional Spring project has the same setup
Once you’ve copied this pattern, `controller tests become boring again` — which is exactly what we want 😄