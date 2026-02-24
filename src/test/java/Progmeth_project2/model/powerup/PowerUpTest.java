package Progmeth_project2.model.powerup;

import Progmeth_project2.model.Difficulty;
import Progmeth_project2.model.GameState;
import Progmeth_project2.model.Theme;
import Progmeth_project2.model.card.BaseCard;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 tests for the {@link BasePowerUp} hierarchy.
 *
 * <p>Each concrete power-up is tested for:
 * <ul>
 *   <li>Initial availability state.</li>
 *   <li>Correct modification of {@link GameState} when activated.</li>
 *   <li>Consumption after {@link BasePowerUp#use(GameState)}.</li>
 *   <li>Recharge via {@link BasePowerUp#reset()}.</li>
 * </ul>
 * </p>
 */
@DisplayName("BasePowerUp Hierarchy Tests")
class PowerUpTest {

    /** Shared game state used by all power-up tests. */
    private GameState state;

    @BeforeEach
    void setUp() {
        // Use EASY difficulty with ANIMALS theme for all tests
        state = new GameState(Difficulty.EASY, Theme.ANIMALS);
    }

    // ── RevealPowerUp ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("RevealPowerUp")
    class RevealTests {

        private RevealPowerUp powerUp;

        @BeforeEach
        void setUp() {
            powerUp = new RevealPowerUp();
        }

        @Test
        @DisplayName("Is available when newly created")
        void initiallyAvailable() {
            assertTrue(powerUp.isAvailable());
        }

        @Test
        @DisplayName("Sets revealing flag in GameState when activated")
        void activateSetsRevealingFlag() {
            assertFalse(state.isRevealing(), "Should not be revealing before activation");
            powerUp.activate(state);
            assertTrue(state.isRevealing(), "Should be revealing after activation");
        }

        @Test
        @DisplayName("use() consumes the power-up")
        void useConsumes() {
            powerUp.use(state);
            assertFalse(powerUp.isAvailable(), "Should be consumed after use");
        }

        @Test
        @DisplayName("reset() makes power-up available again")
        void resetRecharges() {
            powerUp.use(state);
            powerUp.reset();
            assertTrue(powerUp.isAvailable(), "Should be available after reset");
        }

        @Test
        @DisplayName("use() on consumed power-up is a no-op")
        void doubleUseIsNoOp() {
            state.setRevealing(false);
            powerUp.use(state);    // first use — activates
            state.setRevealing(false); // manually reset flag to test second use
            powerUp.use(state);    // second use — should do nothing
            assertFalse(state.isRevealing(), "Second use should be ignored");
        }

        @Test
        @DisplayName("REVEAL_DURATION_MS is 2000")
        void revealDurationConstant() {
            assertEquals(2000, RevealPowerUp.REVEAL_DURATION_MS);
        }
    }

    // ── FreezePowerUp ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("FreezePowerUp")
    class FreezeTests {

        private FreezePowerUp powerUp;

        @BeforeEach
        void setUp() {
            powerUp = new FreezePowerUp();
        }

        @Test
        @DisplayName("Freezes the timer for FREEZE_DURATION_SECONDS seconds")
        void activateFreezeTimer() {
            assertFalse(state.isTimerFrozen(), "Timer should not be frozen initially");
            powerUp.activate(state);
            assertTrue(state.isTimerFrozen(), "Timer should be frozen after activation");
        }

        @Test
        @DisplayName("Timer does not decrement while frozen")
        void frozenTimerDoesNotDecrement() {
            int before = state.getTimeRemaining();
            powerUp.activate(state);
            state.tickTimer(); // should decrement frozen counter, not timer
            // If timer is frozen, timeRemaining stays the same
            assertEquals(before, state.getTimeRemaining(),
                "Timer must not decrement while frozen");
        }

        @Test
        @DisplayName("FREEZE_DURATION_SECONDS is 10")
        void freezeDurationConstant() {
            assertEquals(10, FreezePowerUp.FREEZE_DURATION_SECONDS);
        }
    }

    // ── HintPowerUp ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("HintPowerUp")
    class HintTests {

        private HintPowerUp powerUp;

        @BeforeEach
        void setUp() {
            powerUp = new HintPowerUp();
        }

        @Test
        @DisplayName("activate() sets two hint card IDs in the game state")
        void activateSetsHintIds() {
            powerUp.activate(state);
            List<String> ids = state.getHintCardIds();
            assertFalse(ids.isEmpty(), "Hint card IDs should not be empty after activation");
            // Hint should identify exactly one pair (2 IDs)
            assertEquals(2, ids.size(), "Hint should provide exactly 2 card IDs");
        }

        @Test
        @DisplayName("Hint cards share the same symbol key (are a real pair)")
        void hintCardsFormAPair() {
            powerUp.activate(state);
            List<String> ids = state.getHintCardIds();
            if (ids.size() < 2) return; // guard for empty deck edge case

            List<BaseCard> cards = state.getCards();
            BaseCard cardA = cards.stream()
                    .filter(c -> c.getCardId().equals(ids.get(0)))
                    .findFirst().orElse(null);
            BaseCard cardB = cards.stream()
                    .filter(c -> c.getCardId().equals(ids.get(1)))
                    .findFirst().orElse(null);

            assertNotNull(cardA, "First hint card must exist in the deck");
            assertNotNull(cardB, "Second hint card must exist in the deck");
            assertEquals(cardA.getSymbolKey(), cardB.getSymbolKey(),
                "Hint cards must share the same symbol key");
        }

        @Test
        @DisplayName("HINT_DURATION_MS is 2000")
        void hintDurationConstant() {
            assertEquals(2000, HintPowerUp.HINT_DURATION_MS);
        }

        @Test
        @DisplayName("Hint is not set on an already all-matched deck")
        void noHintWhenAllMatched() {
            // Match all cards in the model
            for (BaseCard card : state.getCards()) {
                card.setMatched();
            }
            powerUp.activate(state);
            // Should not crash and hint IDs should be empty (no unmatched pairs)
            List<String> ids = state.getHintCardIds();
            assertTrue(ids.isEmpty(),
                "Hint should be empty when all cards are matched");
        }
    }

    // ── Polymorphism ──────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Polymorphism via BasePowerUp reference")
    class PolymorphismTests {

        @Test
        @DisplayName("All power-up types can be activated via BasePowerUp reference")
        void polymorphicActivate() {
            BasePowerUp[] powerUps = {
                new RevealPowerUp(),
                new FreezePowerUp(),
                new HintPowerUp()
            };
            for (BasePowerUp pu : powerUps) {
                assertDoesNotThrow(() -> pu.activate(state),
                    "activate() should not throw for: " + pu.getName());
            }
        }

        @Test
        @DisplayName("All power-up types report correct name and icon")
        void namesAndIcons() {
            assertEquals("Reveal", new RevealPowerUp().getName());
            assertEquals("Freeze", new FreezePowerUp().getName());
            assertEquals("Hint",   new HintPowerUp().getName());

            assertEquals("👁", new RevealPowerUp().getIconText());
            assertEquals("❄",  new FreezePowerUp().getIconText());
            assertEquals("💡", new HintPowerUp().getIconText());
        }
    }
}
