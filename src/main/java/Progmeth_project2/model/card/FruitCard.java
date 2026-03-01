package Progmeth_project2.model.card;

/**
 * A card whose face displays a fruit emoji symbol.
 */
public class FruitCard extends BaseCard {

    /** CSS style-class applied to the card face for the Fruits theme. */
    private static final String STYLE_CLASS = "fruit-card";

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Constructs a {@code FruitCard} with the given identifiers.
     *
     * @param cardId    unique identifier for this card instance
     * @param symbolKey the fruit emoji used as the pair key
     */
    public FruitCard(String cardId, String symbolKey) {
        super(cardId, symbolKey);
    }

    // ── BaseCard contract ────────────────────────────────────────────────────

    /**
     * Returns the fruit emoji that identifies this card's pair.
     */
    @Override
    public String getDisplaySymbol() {
        return symbolKey;
    }

    /**
     * Returns the CSS style class for the Fruits theme.
     */
    @Override
    public String getThemeStyleClass() {
        return STYLE_CLASS;
    }

    /**
     * Fruit cards award the base default score.
     */
    @Override
    public int getScore() {
        return DEFAULT_SCORE;
    }
}
