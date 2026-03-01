package Progmeth_project2.model.card;


/** A card whose face displays an animal emoji symbol. */
public class AnimalCard extends BaseCard {

    private static final String STYLE_CLASS = "animal-card";

    /**
     * Score bonus applied on top of the default value for animal cards,
     * reflecting the slightly higher visual complexity of emoji symbols.
     */
    private static final int SCORE_BONUS = 2;

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Constructs an {@code AnimalCard} with the given identifiers.
     *
     * @param cardId      unique identifier for this card instance
     * @param symbolKey   the animal emoji used as the pair key
     */
    public AnimalCard(String cardId, String symbolKey) {
        super(cardId, symbolKey);
    }

    // ── BaseCard contract ────────────────────────────────────────────────────

    /**
     * {@inheritDoc}
     * Returns the animal emoji that identifies this card's pair.
     */
    @Override
    public String getDisplaySymbol() {
        return symbolKey;
    }

    /**
     * {@inheritDoc}
     * Returns the CSS style class for the Animals theme.
     */
    @Override
    public String getThemeStyleClass() {
        return STYLE_CLASS;
    }

    /**
     * {@inheritDoc}
     * Animal cards award the default score plus a small bonus.
     */
    @Override
    public int getScore() {
        return DEFAULT_SCORE + SCORE_BONUS;
    }
}
