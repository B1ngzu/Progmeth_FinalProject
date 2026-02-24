package Progmeth_project2.model.card;

/**
 * A card whose face displays a numeric symbol (1–18).
 *
 * <p>Concrete subclass of {@link BaseCard}; demonstrates polymorphism by
 * overriding {@link #getDisplaySymbol()}, {@link #getThemeStyleClass()}, and
 * {@link #getScore()} while being stored in {@code List<BaseCard>}
 * collections.</p>
 *
 * <p>Number cards award a higher score than other themes to reward players who
 * choose a less visually distinct theme.</p>
 */
public class NumberCard extends BaseCard {

    /** CSS style-class applied to the card face for the Numbers theme. */
    private static final String STYLE_CLASS = "number-card";

    /**
     * Additional points awarded for Number cards to compensate for the
     * reduced visual distinctiveness compared with emoji cards.
     */
    private static final int SCORE_BONUS = 5;

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Constructs a {@code NumberCard} with the given identifiers.
     *
     * @param cardId    unique identifier for this card instance
     * @param symbolKey the numeric string (e.g., {@code "7"}) used as the pair key
     */
    public NumberCard(String cardId, String symbolKey) {
        super(cardId, symbolKey);
    }

    // ── BaseCard contract ────────────────────────────────────────────────────

    /**
     * {@inheritDoc}
     * Returns the numeric string that identifies this card's pair.
     */
    @Override
    public String getDisplaySymbol() {
        return symbolKey;
    }

    /**
     * {@inheritDoc}
     * Returns the CSS style class for the Numbers theme.
     */
    @Override
    public String getThemeStyleClass() {
        return STYLE_CLASS;
    }

    /**
     * {@inheritDoc}
     * Number cards award the default score plus a bonus.
     */
    @Override
    public int getScore() {
        return DEFAULT_SCORE + SCORE_BONUS;
    }
}
