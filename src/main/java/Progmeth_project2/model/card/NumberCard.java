package Progmeth_project2.model.card;

/**
 * A card whose face displays a numeric symbol (1–18).
 */
public class NumberCard extends BaseCard {

    private static final String STYLE_CLASS = "number-card";

    /**
     * Additional points awarded for Number cards to compensate for the
     * reduced visual distinctiveness compared with emoji cards.
     */
    private static final int SCORE_BONUS = 5;


    /**
     * Constructs a {@code NumberCard} with the given identifiers.
     *
     * @param cardId    unique identifier for this card instance
     * @param symbolKey numeric string (e.g., "1"–"18") used as the pair key
     */
    public NumberCard(String cardId, String symbolKey) {
        super(cardId, symbolKey);
    }

    /**
     * Returns the numeric string that identifies this card's pair.
     */
    @Override
    public String getDisplaySymbol() {
        return symbolKey;
    }

    /**
     * Returns the CSS style class for the Numbers theme.
     */
    @Override
    public String getThemeStyleClass() {
        return STYLE_CLASS;
    }

    /**
     * Number cards award the default score plus a bonus.
     */
    @Override
    public int getScore() {
        return DEFAULT_SCORE + SCORE_BONUS;
    }
}
