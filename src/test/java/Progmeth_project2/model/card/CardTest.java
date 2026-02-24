package Progmeth_project2.model.card;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 tests for the {@link BaseCard} hierarchy.
 *
 * <p>Covers {@link AnimalCard}, {@link FruitCard}, and {@link NumberCard} to
 * verify that each concrete subclass correctly implements the contracts defined
 * in {@link BaseCard}, including {@link Progmeth_project2.interfaces.Flippable},
 * {@link Progmeth_project2.interfaces.Scoreable}, and
 * {@link Progmeth_project2.interfaces.Resetable}.</p>
 *
 * <p>Tests also verify the polymorphic behaviour expected when cards are stored
 * in {@code List<BaseCard>} collections.</p>
 */
@DisplayName("BaseCard Hierarchy Tests")
class CardTest {

    // ── AnimalCard ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("AnimalCard")
    class AnimalCardTests {

        private AnimalCard card;

        @BeforeEach
        void setUp() {
            card = new AnimalCard("🐶_A", "🐶");
        }

        @Test
        @DisplayName("Starts face-down and unmatched")
        void initialStateFaceDown() {
            assertFalse(card.isFaceUp(),  "New card should be face-down");
            assertFalse(card.isMatched(), "New card should not be matched");
        }

        @Test
        @DisplayName("flip() toggles face-up state")
        void flipTogglesFaceUp() {
            card.flip();
            assertTrue(card.isFaceUp(), "Card should be face-up after flip");
            card.flip();
            assertFalse(card.isFaceUp(), "Card should be face-down after second flip");
        }

        @Test
        @DisplayName("setMatched() locks the card face-up")
        void setMatchedLocksCard() {
            card.setMatched();
            assertTrue(card.isMatched(), "Card should be marked matched");
            assertTrue(card.isFaceUp(),  "Matched card must be face-up");
            // Flip should have no effect on a matched card
            card.flip();
            assertTrue(card.isFaceUp(), "Matched card must stay face-up after flip call");
        }

        @Test
        @DisplayName("reset() restores face-down unmatched state")
        void resetRestoresInitialState() {
            card.flip();
            card.setMatched();
            card.reset();
            assertFalse(card.isFaceUp(),  "Card should be face-down after reset");
            assertFalse(card.isMatched(), "Card should be unmatched after reset");
        }

        @Test
        @DisplayName("getDisplaySymbol() returns the symbol key")
        void displaySymbolEqualsSymbolKey() {
            assertEquals("🐶", card.getDisplaySymbol());
            assertEquals("🐶", card.getSymbolKey());
        }

        @Test
        @DisplayName("getScore() returns DEFAULT_SCORE + 2 for AnimalCard")
        void scoreIsDefaultPlusBonus() {
            assertEquals(12, card.getScore(),
                "AnimalCard score should be DEFAULT_SCORE(10) + SCORE_BONUS(2)");
        }

        @Test
        @DisplayName("getThemeStyleClass() returns 'animal-card'")
        void styleClassIsAnimalCard() {
            assertEquals("animal-card", card.getThemeStyleClass());
        }

        @Test
        @DisplayName("getCardId() returns constructor-provided id")
        void cardIdPreserved() {
            assertEquals("🐶_A", card.getCardId());
        }
    }

    // ── FruitCard ─────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("FruitCard")
    class FruitCardTests {

        private FruitCard card;

        @BeforeEach
        void setUp() {
            card = new FruitCard("🍎_B", "🍎");
        }

        @Test
        @DisplayName("getScore() returns DEFAULT_SCORE (10) for FruitCard")
        void scoreIsDefault() {
            assertEquals(10, card.getScore(),
                "FruitCard score should equal DEFAULT_SCORE(10)");
        }

        @Test
        @DisplayName("getThemeStyleClass() returns 'fruit-card'")
        void styleClassIsFruitCard() {
            assertEquals("fruit-card", card.getThemeStyleClass());
        }

        @Test
        @DisplayName("setFaceUp(true) shows face; setFaceUp(false) hides face")
        void setFaceUpControl() {
            card.setFaceUp(true);
            assertTrue(card.isFaceUp());
            card.setFaceUp(false);
            assertFalse(card.isFaceUp());
        }

        @Test
        @DisplayName("setFaceUp() has no effect after setMatched()")
        void setFaceUpIgnoredWhenMatched() {
            card.setMatched();
            card.setFaceUp(false); // should be ignored
            assertTrue(card.isFaceUp(), "Matched card must remain face-up");
        }
    }

    // ── NumberCard ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("NumberCard")
    class NumberCardTests {

        private NumberCard card;

        @BeforeEach
        void setUp() {
            card = new NumberCard("7_A", "7");
        }

        @Test
        @DisplayName("getScore() returns DEFAULT_SCORE + 5 for NumberCard")
        void scoreIsDefaultPlusBonus() {
            assertEquals(15, card.getScore(),
                "NumberCard score should be DEFAULT_SCORE(10) + SCORE_BONUS(5)");
        }

        @Test
        @DisplayName("getThemeStyleClass() returns 'number-card'")
        void styleClassIsNumberCard() {
            assertEquals("number-card", card.getThemeStyleClass());
        }

        @Test
        @DisplayName("getDisplaySymbol() returns '7'")
        void displaySymbol() {
            assertEquals("7", card.getDisplaySymbol());
        }
    }

    // ── Polymorphism ──────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Polymorphism via BaseCard reference")
    class PolymorphismTests {

        @Test
        @DisplayName("Different card types return different scores via BaseCard reference")
        void polymorphicGetScore() {
            BaseCard animal = new AnimalCard("a", "🐶");
            BaseCard fruit  = new FruitCard("b",  "🍎");
            BaseCard number = new NumberCard("c", "5");

            // Different subclasses return different scores
            assertNotEquals(animal.getScore(), fruit.getScore(),
                "AnimalCard and FruitCard should have different scores");
            assertNotEquals(fruit.getScore(), number.getScore(),
                "FruitCard and NumberCard should have different scores");
        }

        @Test
        @DisplayName("Flip state is polymorphically consistent across types")
        void polymorphicFlip() {
            BaseCard[] cards = {
                new AnimalCard("a1", "🐶"),
                new FruitCard("b1",  "🍎"),
                new NumberCard("c1", "3")
            };
            for (BaseCard c : cards) {
                assertFalse(c.isFaceUp(), "Card should start face-down: " + c);
                c.flip();
                assertTrue(c.isFaceUp(), "Card should be face-up after flip: " + c);
            }
        }
    }
}
