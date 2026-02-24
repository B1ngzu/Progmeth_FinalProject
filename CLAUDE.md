# CLAUDE.md — Memory Card Matching Game
## Project Reference for Code Generation

---

## 1. Game Overview

A JavaFX Memory Card Matching Game where the player flips two cards at a time.
If they match, the cards stay face-up; otherwise they flip back. Play continues until all
pairs are matched or the countdown timer expires.

### Core Mechanics
- **Combo System**: Consecutive correct matches multiply the score (x1, x2, x3...). A mismatch resets the combo to x1.
- **Difficulty Levels**:
  - Easy  → 4×4 grid (8 pairs), 120 s timer, base 10 pts/pair
  - Medium → 5×6 grid (15 pairs), 180 s timer, base 15 pts/pair
  - Hard  → 6×6 grid (18 pairs), 240 s timer, base 20 pts/pair
- **Power-ups** (one use each per level, recharged on next level):
  - Reveal  → shows all cards for 2 seconds
  - Freeze  → pauses the countdown for 10 seconds
  - Hint    → highlights one unmatched matching pair for 2 seconds
- **Leaderboard**: persists top-10 scores to `~/.progmeth_project2/leaderboard.dat` via Java Serialization.
- **Themes**: Animals (emoji), Fruits (emoji), Numbers (1–18).
- **Animations**: card-flip (X-scale squash/expand), match-glow (scale + brightness), combo-text (translate + fade).
- **Sound**: all sounds synthesised programmatically via `javax.sound.sampled` (no audio files needed).
- **Level Progression**: completing a level starts the next with the same difficulty, higher score multiplier, and slightly shorter timer.

---

## 2. Package & File Structure

```
src/
├── main/
│   ├── java/Progmeth_project2/
│   │   ├── Main.java                          ← extends Application; entry via Launcher
│   │   ├── Launcher.java                      ← plain main(); workaround for shadow-JAR
│   │   ├── interfaces/
│   │   │   ├── Scoreable.java
│   │   │   ├── Flippable.java
│   │   │   ├── Resetable.java
│   │   │   ├── Playable.java
│   │   │   └── Persistable.java
│   │   ├── model/
│   │   │   ├── Difficulty.java                ← enum
│   │   │   ├── Theme.java                     ← enum
│   │   │   ├── GameState.java
│   │   │   ├── ScoreEntry.java
│   │   │   ├── Leaderboard.java
│   │   │   ├── card/
│   │   │   │   ├── BaseCard.java              ← abstract
│   │   │   │   ├── AnimalCard.java
│   │   │   │   ├── FruitCard.java
│   │   │   │   └── NumberCard.java
│   │   │   ├── powerup/
│   │   │   │   ├── BasePowerUp.java           ← abstract
│   │   │   │   ├── RevealPowerUp.java
│   │   │   │   ├── FreezePowerUp.java
│   │   │   │   └── HintPowerUp.java
│   │   │   └── effect/
│   │   │       ├── BaseEffect.java            ← abstract
│   │   │       ├── FlipEffect.java
│   │   │       ├── MatchEffect.java
│   │   │       └── ComboEffect.java
│   │   ├── view/
│   │   │   ├── BaseScene.java                 ← abstract
│   │   │   ├── MainMenuScene.java
│   │   │   ├── GameScene.java
│   │   │   ├── LeaderboardScene.java
│   │   │   ├── SettingsScene.java
│   │   │   ├── CardView.java
│   │   │   └── PowerUpView.java
│   │   ├── controller/
│   │   │   ├── GameController.java
│   │   │   ├── MenuController.java
│   │   │   └── LeaderboardController.java
│   │   └── util/
│   │       ├── SoundManager.java
│   │       ├── AnimationManager.java
│   │       └── FileManager.java
│   └── resources/Progmeth_project2/
│       └── styles.css
└── test/java/Progmeth_project2/
    ├── model/
    │   ├── card/CardTest.java
    │   ├── powerup/PowerUpTest.java
    │   ├── GameStateTest.java
    │   └── LeaderboardTest.java
    └── controller/GameControllerTest.java
```

---

## 3. Class Diagram (Brief)

```
<<interface>> Scoreable        int getScore()
<<interface>> Flippable        void flip(); boolean isFaceUp()
<<interface>> Resetable        void reset()
<<interface>> Playable         void playSound()
<<interface>> Persistable      void save(String); void load(String)

BaseCard (abstract) implements Flippable, Scoreable, Resetable
  ├── AnimalCard
  ├── FruitCard
  └── NumberCard

BasePowerUp (abstract) implements Resetable, Playable
  ├── RevealPowerUp
  ├── FreezePowerUp
  └── HintPowerUp

BaseEffect (abstract)
  ├── FlipEffect
  ├── MatchEffect
  └── ComboEffect

BaseScene (abstract)
  ├── MainMenuScene
  ├── GameScene
  ├── LeaderboardScene
  └── SettingsScene

GameState implements Resetable
Leaderboard implements Persistable
ScoreEntry implements Comparable<ScoreEntry>, Serializable
SoundManager, AnimationManager, FileManager  (util singletons/statics)
GameController, MenuController, LeaderboardController
CardView extends StackPane
PowerUpView extends Button
```

---

## 4. Scoring Criteria Checklist

| # | Criterion                         | Points | Approach                                                          |
|---|-----------------------------------|--------|-------------------------------------------------------------------|
| 1 | Overall functionality             | 10     | Full game loop, no known bugs                                     |
| 2 | JUnit 5 testing                   | 5      | Tests in model/card, model/powerup, GameState, Leaderboard, Ctrl  |
| 3 | Visuals + Sound                   | 5      | CSS styling + synthesised javax.sound.sampled sounds              |
| 4 | Development difficulty            | 5      | Combo, 3 difficulties, 3 power-ups, leaderboard, themes, levels  |
| 5 | Inheritance                       | 6      | BaseCard→3, BasePowerUp→3, BaseScene→4, BaseEffect→3              |
| 6 | Interfaces                        | 3      | Scoreable, Flippable, Resetable, Playable, Persistable            |
| 7 | Polymorphism                      | 6      | List<BaseCard>, List<BasePowerUp>, overridden getScore/activate   |
| 8 | Access modifiers                  | 2      | private default, protected for inheritance, public for API        |
| 9 | Overall design (MVC)              | 5      | model/view/controller packages strictly separated                 |
|10 | Coding style (Javadoc, naming)    | 3      | JavaDoc on every public class/method; naming conventions followed |
|   | **Total**                         | **50** | (rubric base; difficulty bonus brings max to 70)                  |

---

## 5. Coding Conventions

- **Classes**: PascalCase — `GameState`, `BaseCard`, `RevealPowerUp`
- **Methods & Variables**: camelCase — `getScore()`, `comboMultiplier`, `isFaceUp`
- **Constants**: UPPER_SNAKE_CASE — `MAX_ENTRIES`, `TIMER_FREEZE_SECONDS`
- **Packages**: lowercase (exception: root `Progmeth_project2` matches GroupId)
- **Access modifiers**: `private` by default; `protected` only when a subclass needs direct field access; `public` only for API surfaces (constructors, getters, interface methods).
- **JavaDoc**: every `public` class and every `public` method must have a `/** */` block.
- **Null safety**: never return null from public methods; return `Optional` or empty collections.
- **Thread safety**: all JavaFX scene-graph mutations must run on the FX Application Thread (`Platform.runLater`). Sound playback runs on a daemon thread via `SoundManager`.
- **Constants file**: magic numbers (grid sizes, timer durations, etc.) are defined as constants on their respective enum/class.

---

## 6. Build Notes

- Gradle 8.14, Groovy DSL, JDK 24, JavaFX 24.
- Shadow plugin creates a fat JAR; manifest Main-Class is `Progmeth_project2.Launcher` (not `Main`) to bypass JavaFX module-system restriction.
- Run: `./gradlew run` or `java -jar build/libs/Progmeth_project2-1.0.jar`
- Tests: `./gradlew test`
