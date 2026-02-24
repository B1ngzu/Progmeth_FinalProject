package Progmeth_project2.model;

import Progmeth_project2.model.card.BaseCard;
import Progmeth_project2.model.powerup.BasePowerUp;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 tests for {@link GameState}.
 *
 * <p>Verifies deck construction, score/combo mechanics, timer behaviour,
 * level progression, power-up availability after level advance, and the
 * {@link Progmeth_project2.interfaces.Resetable} contract.</p>
 */
@DisplayName("GameState Tests")
class GameStateTest {

    // ── Easy + Animals default state ──────────────────────────────────────────

    private GameState easyState;

    @BeforeEach
    void setUp() {
        easyState = new GameState(Difficulty.EASY, Theme.ANIMALS);
    }

    // ── Deck construction ─────────────────────────────────────────────────────

    @Test
    @DisplayName("Easy difficulty produces 16 cards (8 pairs)")
    void easyDeckSize() {
        assertEquals(16, easyState.getCards().size());
    }

    @Test
    @DisplayName("Medium difficulty produces 30 cards (15 pairs)")
    void mediumDeckSize() {
        GameState medium = new GameState(Difficulty.MEDIUM, Theme.FRUITS);
        assertEquals(30, medium.getCards().size());
    }

    @Test
    @DisplayName("Hard difficulty produces 30 cards (15 pairs)")
    void hardDeckSize() {
        GameState hard = new GameState(Difficulty.HARD, Theme.NUMBERS);
        assertEquals(30, hard.getCards().size());
    }

    @Test
    @DisplayName("Every symbol appears exactly twice in the deck")
    void everSymbolTwice() {
        List<BaseCard> cards = easyState.getCards();
        // Count occurrences of each symbol key
        java.util.Map<String, Long> counts = new java.util.HashMap<>();
        for (BaseCard c : cards) {
            counts.merge(c.getSymbolKey(), 1L, Long::sum);
        }
        for (var entry : counts.entrySet()) {
            assertEquals(2L, entry.getValue(),
                "Symbol '" + entry.getKey() + "' should appear exactly twice");
        }
    }

    @Test
    @DisplayName("Cards start face-down and unmatched")
    void cardsStartFaceDown() {
        for (BaseCard card : easyState.getCards()) {
            assertFalse(card.isFaceUp(),  "Card should start face-down");
            assertFalse(card.isMatched(), "Card should start unmatched");
        }
    }

    // ── Initial values ────────────────────────────────────────────────────────

    @Test
    @DisplayName("Initial score is 0")
    void initialScoreZero() {
        assertEquals(0, easyState.getScore());
    }

    @Test
    @DisplayName("Initial combo is 0")
    void initialComboZero() {
        assertEquals(0, easyState.getCombo());
    }

    @Test
    @DisplayName("Initial level is 1")
    void initialLevelOne() {
        assertEquals(1, easyState.getLevel());
    }

    @Test
    @DisplayName("Initial timer matches difficulty setting")
    void initialTimerMatchesDifficulty() {
        assertEquals(Difficulty.EASY.getTimerSeconds(), easyState.getTimeRemaining());
    }

    // ── Score / combo mechanics ────────────────────────────────────────────────

    @Test
    @DisplayName("recordMatch() increments combo and adds baseScore × combo")
    void recordMatchUpdatesScoreAndCombo() {
        easyState.recordMatch(10); // combo becomes 1, score += 10×1 = 10
        assertEquals(1,  easyState.getCombo());
        assertEquals(10, easyState.getScore());

        easyState.recordMatch(10); // combo becomes 2, score += 10×2 = 20
        assertEquals(2,  easyState.getCombo());
        assertEquals(30, easyState.getScore());
    }

    @Test
    @DisplayName("recordMismatch() resets combo to 0")
    void recordMismatchResetsCombo() {
        easyState.recordMatch(10);
        easyState.recordMatch(10);
        assertEquals(2, easyState.getCombo());
        easyState.recordMismatch();
        assertEquals(0, easyState.getCombo());
    }

    @Test
    @DisplayName("Score is not affected by recordMismatch()")
    void mismatchDoesNotChangeScore() {
        easyState.recordMatch(10); // score = 10
        int scoreBefore = easyState.getScore();
        easyState.recordMismatch();
        assertEquals(scoreBefore, easyState.getScore(),
            "Score should not change on mismatch");
    }

    // ── Timer mechanics ───────────────────────────────────────────────────────

    @Test
    @DisplayName("tickTimer() decrements timeRemaining by 1")
    void tickDecrements() {
        int before = easyState.getTimeRemaining();
        easyState.tickTimer();
        assertEquals(before - 1, easyState.getTimeRemaining());
    }

    @Test
    @DisplayName("isTimeUp() is false while time remains")
    void notTimeUpWhenTimerPositive() {
        assertFalse(easyState.isTimeUp());
    }

    @Test
    @DisplayName("Frozen timer does not decrement timeRemaining")
    void frozenTimerStaysSame() {
        int before = easyState.getTimeRemaining();
        easyState.freezeTimer(5);
        easyState.tickTimer();
        assertEquals(before, easyState.getTimeRemaining(),
            "timeRemaining must not decrease while frozen");
    }

    @Test
    @DisplayName("Timer unfreezes after freeze duration ticks")
    void timerUnfreezes() {
        easyState.freezeTimer(1); // freeze for 1 second
        easyState.tickTimer();    // tick 1: decrements frozenSeconds (0 remaining)
        // Now frozen should be false; next tick decrements timer
        assertFalse(easyState.isTimerFrozen(),
            "Timer should unfreeze after freeze duration expires");
        int before = easyState.getTimeRemaining();
        easyState.tickTimer();
        assertEquals(before - 1, easyState.getTimeRemaining());
    }

    // ── Level completion ──────────────────────────────────────────────────────

    @Test
    @DisplayName("isLevelComplete() returns true when matchesFound equals totalPairs")
    void levelCompleteWhenAllMatched() {
        int pairs = Difficulty.EASY.getTotalPairs();
        for (int i = 0; i < pairs; i++) {
            easyState.recordMatch(10);
        }
        assertTrue(easyState.isLevelComplete());
    }

    @Test
    @DisplayName("advanceLevel() increments level counter")
    void advanceLevelIncrementsLevel() {
        easyState.advanceLevel();
        assertEquals(2, easyState.getLevel());
    }

    @Test
    @DisplayName("advanceLevel() recharges all power-ups")
    void advanceLevelRechargesPowerUps() {
        // Consume all power-ups
        List<BasePowerUp> powerUps = easyState.getPowerUps();
        for (BasePowerUp pu : powerUps) {
            pu.use(easyState);
        }
        // Verify all consumed
        for (BasePowerUp pu : powerUps) {
            assertFalse(pu.isAvailable(), "Power-up should be consumed");
        }
        // Advance level
        easyState.advanceLevel();
        // All should be recharged
        for (BasePowerUp pu : easyState.getPowerUps()) {
            assertTrue(pu.isAvailable(),
                "Power-up " + pu.getName() + " should be recharged after level advance");
        }
    }

    @Test
    @DisplayName("advanceLevel() rebuilds the card deck (new shuffled set)")
    void advanceLevelRebuildsDecks() {
        List<BaseCard> before = List.copyOf(easyState.getCards());
        easyState.advanceLevel();
        List<BaseCard> after = easyState.getCards();
        assertEquals(before.size(), after.size(),
            "Deck size should remain the same after level advance");
        // Cards should be freshly reset (all face-down)
        for (BaseCard c : after) {
            assertFalse(c.isFaceUp(),  "Card should be face-down after level advance");
            assertFalse(c.isMatched(), "Card should be unmatched after level advance");
        }
    }

    // ── Resetable contract ────────────────────────────────────────────────────

    @Test
    @DisplayName("reset() restores score, combo, level, and timer to initial values")
    void resetRestoresInitialValues() {
        easyState.recordMatch(10);
        easyState.recordMatch(10);
        easyState.advanceLevel();
        easyState.reset();

        assertEquals(0, easyState.getScore(),  "Score should be 0 after reset");
        assertEquals(0, easyState.getCombo(),  "Combo should be 0 after reset");
        assertEquals(1, easyState.getLevel(),  "Level should be 1 after reset");
        assertEquals(Difficulty.EASY.getTimerSeconds(), easyState.getTimeRemaining(),
            "Timer should be restored to difficulty default after reset");
    }

    // ── Power-ups ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GameState provides 3 power-ups (Reveal, Freeze, Hint)")
    void threePowerUps() {
        List<BasePowerUp> powerUps = easyState.getPowerUps();
        assertEquals(3, powerUps.size(), "Game should have exactly 3 power-ups");
    }

    @Test
    @DisplayName("Hint card IDs are cleared after clearHint()")
    void clearHintRemovesIds() {
        easyState.setHintCardIds(List.of("a", "b"));
        assertFalse(easyState.getHintCardIds().isEmpty());
        easyState.clearHint();
        assertTrue(easyState.getHintCardIds().isEmpty());
    }
}
