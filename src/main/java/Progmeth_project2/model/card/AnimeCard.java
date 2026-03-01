package Progmeth_project2.model.card;

/** A card whose face displays an anime character image loaded from the classpath. */
public class AnimeCard extends BaseCard {

    private static final String STYLE_CLASS = "animal-card";

    /**
     * Score bonus applied on top of the default value for animal cards,
     * reflecting the slightly higher visual complexity of emoji symbols.
     */
    private static final int SCORE_BONUS = 2;

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Constructs an {@code AnimeCard} with the given identifiers.
     *
     * @param cardId    unique identifier for this card instance
     * @param symbolKey classpath image path (e.g. {@code "/images/frieren.png"}) used as the pair key
     */
    public AnimeCard(String cardId, String symbolKey) {
        super(cardId, symbolKey);
    }

    // ── BaseCard contract ────────────────────────────────────────────────────

    /**
     * Returns the animal emoji that identifies this card's pair.
     */
    @Override
    public String getDisplaySymbol() {
        return symbolKey;
    }

    /**
     * Returns the CSS style class for the Animals theme.
     */
    @Override
    public String getThemeStyleClass() {
        return STYLE_CLASS;
    }

    /**
     * Animal cards award the default score plus a small bonus.
     */
    @Override
    public int getScore() {
        return DEFAULT_SCORE + SCORE_BONUS;
    }
}
