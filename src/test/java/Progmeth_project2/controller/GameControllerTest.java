package Progmeth_project2.controller;

import Progmeth_project2.model.*;
import Progmeth_project2.model.card.BaseCard;
import Progmeth_project2.model.powerup.*;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 tests for controller-layer logic that does not require a live
 * JavaFX Application Thread.
 *
 * <p>Because {@link GameController} requires a running JavaFX stage and scene,
 * these tests focus on the <em>model</em> interactions that the controller
 * orchestrates: they call model methods in the same sequence the controller
 * would, and verify the resulting model state.</p>
 *
 * <p>This ensures that the game's scoring, combo, and power-up logic is
 * correct independently of the UI framework.</p>
 */
@DisplayName("Controller-side Logic Tests (model simulation)")
class GameControllerTest {

    private GameState state;

    @BeforeEach
    void setUp() {
        state = new GameState(Difficulty.EASY, Theme.ANIMALS);
    }

    // ── Match evaluation sequence ─────────────────────────────────────────────

    @Test
    @DisplayName("Sequential matches build up the combo correctly")
    void comboBuildsWithSequentialMatches() {
        // Simulate 3 consecutive correct matches
        state.recordMatch(10); // combo 1 → score = 10
        state.recordMatch(10); // combo 2 → score = 10 + 20 = 30
        state.recordMatch(10); // combo 3 → score = 30 + 30 = 60

        assertEquals(3,  state.getCombo(),  "Combo should be 3 after 3 consecutive matches");
        assertEquals(60, state.getScore(), "Score should be 60 after 3 matches");
    }

    @Test
    @DisplayName("Mismatch resets combo to 0 and does not affect score")
    void mismatchResetsCombo() {
        state.recordMatch(10);
        state.recordMatch(10);
        int scoreBefore = state.getScore();
        state.recordMismatch();

        assertEquals(0, state.getCombo(), "Combo must reset to 0 on mismatch");
        assertEquals(scoreBefore, state.getScore(), "Score must not change on mismatch");
    }

    @Test
    @DisplayName("Combo restarts from 1 after a mismatch and then a match")
    void comboRestartsAfterMismatch() {
        state.recordMatch(10); // combo = 1
        state.recordMatch(10); // combo = 2
        state.recordMismatch(); // combo = 0
        state.recordMatch(10); // combo = 1 again

        assertEquals(1, state.getCombo(), "Combo should restart at 1 after mismatch+match");
    }

    // ── Power-up activation and game state ────────────────────────────────────

    @Test
    @DisplayName("Reveal power-up sets and can be cleared in game state")
    void revealPowerUpGameState() {
        RevealPowerUp reveal = new RevealPowerUp();
        reveal.activate(state);
        assertTrue(state.isRevealing(), "Game state should be in revealing mode");

        state.setRevealing(false);
        assertFalse(state.isRevealing(), "Revealing flag should be clearable");
    }

    @Test
    @DisplayName("Freeze power-up prevents timer decrement for freeze duration")
    void freezePowerUpTimer() {
        int timerBefore = state.getTimeRemaining();
        FreezePowerUp freeze = new FreezePowerUp();
        freeze.activate(state);

        // Tick the timer several times within the freeze window
        for (int i = 0; i < FreezePowerUp.FREEZE_DURATION_SECONDS - 1; i++) {
            state.tickTimer(); // decrements frozen counter, not main timer
        }
        // Timer should still equal the before value
        assertEquals(timerBefore, state.getTimeRemaining(),
            "Timer should not decrement during freeze");
    }

    @Test
    @DisplayName("Hint power-up identifies a valid unmatched pair")
    void hintPowerUpFindsUnmatchedPair() {
        HintPowerUp hint = new HintPowerUp();
        hint.activate(state);

        List<String> hintIds = state.getHintCardIds();
        assertEquals(2, hintIds.size(), "Hint must provide exactly 2 card IDs");

        List<BaseCard> cards = state.getCards();
        String key0 = cards.stream()
                .filter(c -> c.getCardId().equals(hintIds.get(0)))
                .map(BaseCard::getSymbolKey)
                .findFirst().orElse(null);
        String key1 = cards.stream()
                .filter(c -> c.getCardId().equals(hintIds.get(1)))
                .map(BaseCard::getSymbolKey)
                .findFirst().orElse(null);

        assertNotNull(key0);
        assertEquals(key0, key1, "Hinted cards should form a matching pair");
    }

    // ── Level progression ─────────────────────────────────────────────────────

    @Test
    @DisplayName("Level is not complete until all pairs are matched")
    void levelNotCompleteUntilAllMatched() {
        int pairs = Difficulty.EASY.getTotalPairs();
        for (int i = 0; i < pairs - 1; i++) {
            state.recordMatch(10);
        }
        assertFalse(state.isLevelComplete(),
            "Level should not be complete with one pair still to match");

        state.recordMatch(10);
        assertTrue(state.isLevelComplete(),
            "Level should be complete after all pairs are matched");
    }

    @Test
    @DisplayName("advanceLevel() awards time bonus to score")
    void advanceLevelAwardsTimeBonus() {
        int scoreBefore = state.getScore();
        int timeLeft    = state.getTimeRemaining();
        state.advanceLevel();

        // Score should have increased by at least timeLeft × TIME_BONUS_PER_SECOND
        int expectedMinIncrease = timeLeft * GameState.TIME_BONUS_PER_SECOND
                                + GameState.LEVEL_COMPLETION_BONUS * 1; // level was 1
        assertTrue(state.getScore() >= scoreBefore + expectedMinIncrease,
            "Score after level advance should include time bonus");
    }

    @Test
    @DisplayName("Timer decreases (or is capped at 30) on each level advance")
    void timerDecreasesEachLevel() {
        int firstLevelTimer = Difficulty.EASY.getTimerSeconds();

        state.advanceLevel(); // Level 2
        int secondLevelTimer = state.getTimeRemaining();

        assertTrue(secondLevelTimer <= firstLevelTimer,
            "Timer should be equal or shorter on level 2");
        assertTrue(secondLevelTimer >= 30,
            "Timer should never drop below 30 seconds");
    }

    // ── Difficulty constants ───────────────────────────────────────────────────

    @Test
    @DisplayName("Difficulty enum provides correct grid dimensions and scores")
    void difficultyConstants() {
        assertEquals(4,   Difficulty.EASY.getColumns());
        assertEquals(4,   Difficulty.EASY.getRows());
        assertEquals(120, Difficulty.EASY.getTimerSeconds());
        assertEquals(10,  Difficulty.EASY.getBaseScore());
        assertEquals(8,   Difficulty.EASY.getTotalPairs());

        assertEquals(6,   Difficulty.MEDIUM.getColumns());
        assertEquals(5,   Difficulty.MEDIUM.getRows());
        assertEquals(180, Difficulty.MEDIUM.getTimerSeconds());
        assertEquals(15,  Difficulty.MEDIUM.getTotalPairs());

        assertEquals(6,   Difficulty.HARD.getColumns());
        assertEquals(5,   Difficulty.HARD.getRows());
        assertEquals(240, Difficulty.HARD.getTimerSeconds());
        assertEquals(15,  Difficulty.HARD.getTotalPairs());
    }

    // ── Theme ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Each theme provides at least 18 symbols")
    void themeSymbolCount() {
        for (Theme theme : Theme.values()) {
            assertEquals(18, theme.getSymbols().length,
                "Theme " + theme + " should have exactly 18 symbols");
        }
    }
}
