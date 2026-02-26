package Progmeth_project2.model.card;

import Progmeth_project2.interfaces.Flippable;
import Progmeth_project2.interfaces.Resetable;
import Progmeth_project2.interfaces.Scoreable;

/**
 * Abstract base for all card types in the Memory Match game.
 *
 * <p>Implements {@link Flippable} (cards can be flipped face-up / face-down),
 * {@link Scoreable} (each matched pair contributes points), and
 * {@link Resetable} (cards can be reset between levels).</p>
 *
 * <p>Concrete subclasses ({@link AnimalCard}, {@link FruitCard},
 * {@link NumberCard}),({@link AnimalCard} must supply a display symbol via {@link #getDisplaySymbol()}
 * and may override {@link #getScore()} to differentiate point values.</p>
 */
public abstract class BaseCard implements Flippable, Scoreable, Resetable {

    /** Base score awarded when this card forms part of a matched pair. */
    protected static final int DEFAULT_SCORE = 10;

    /** Unique identifier for this card instance (used for grid positioning). */
    protected final String cardId;

    /**
     * The pair key shared by exactly two cards in the deck.
     * Cards with the same {@code symbolKey} constitute a matching pair.
     */
    protected final String symbolKey;

    /** Whether this card is currently showing its face (symbol side). */
    protected boolean faceUp;

    /** Whether this card has been permanently matched and is locked face-up. */
    protected boolean matched;

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Constructs a card with the given identifier and symbol key.
     *
     * @param cardId    unique identifier for this card instance
     * @param symbolKey key shared by both cards of the same pair
     */
    protected BaseCard(String cardId, String symbolKey) {
        this.cardId = cardId;
        this.symbolKey = symbolKey;
        this.faceUp = false;
        this.matched = false;
    }

    // ── Abstract contract ────────────────────────────────────────────────────

    /**
     * Returns the visual symbol (emoji or text) displayed on the card face.
     *
     * @return non-null display symbol string
     */
    public abstract String getDisplaySymbol();

    /**
     * Returns a CSS style class that colours the card face according to its
     * theme (e.g., {@code "animal-card"}, {@code "fruit-card"}).
     *
     * @return CSS style class name
     */
    public abstract String getThemeStyleClass();

    // ── Flippable ────────────────────────────────────────────────────────────

    /**
     * {@inheritDoc}
     * Has no effect if the card is already matched (locked face-up).
     */
    @Override
    public void flip() {
        if (!matched) {
            faceUp = !faceUp;
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean isFaceUp() {
        return faceUp;
    }

    // ── Scoreable ────────────────────────────────────────────────────────────

    /**
     * {@inheritDoc}
     * Subclasses may override to return theme-specific or difficulty-adjusted
     * values.
     */
    @Override
    public int getScore() {
        return DEFAULT_SCORE;
    }

    // ── Resetable ────────────────────────────────────────────────────────────

    /**
     * {@inheritDoc}
     * Resets the card to face-down and unmatched state.
     */
    @Override
    public void reset() {
        faceUp = false;
        matched = false;
    }

    // ── Game-specific helpers ────────────────────────────────────────────────

    /**
     * Returns the pair key shared by exactly two cards in the deck.
     * Two cards match when their symbol keys are equal.
     *
     * @return symbol key string
     */
    public String getSymbolKey() {
        return symbolKey;
    }

    /**
     * Returns the unique identifier for this card instance.
     *
     * @return card id string
     */
    public String getCardId() {
        return cardId;
    }

    /**
     * Returns {@code true} if this card has been permanently matched.
     *
     * @return matched flag
     */
    public boolean isMatched() {
        return matched;
    }

    /**
     * Marks this card as permanently matched (locks it face-up).
     */
    public void setMatched() {
        this.matched = true;
        this.faceUp = true;
    }

    /**
     * Force-sets the face-up state (used by the Reveal power-up).
     *
     * @param faceUp {@code true} to show the face
     */
    public void setFaceUp(boolean faceUp) {
        if (!matched) {
            this.faceUp = faceUp;
        }
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "Card[" + cardId + ", sym=" + symbolKey
               + ", faceUp=" + faceUp + ", matched=" + matched + "]";
    }
}
