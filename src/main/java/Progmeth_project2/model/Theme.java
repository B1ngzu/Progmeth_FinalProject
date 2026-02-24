package Progmeth_project2.model;

/**
 * Represents the card-face theme the player chooses before a game.
 * Each theme supplies an ordered array of unique symbols (emoji or text) large
 * enough to cover the hardest difficulty (18 pairs).
 */
public enum Theme {

    /** Emoji animals — 18 unique symbols. */
    ANIMALS("Animals", new String[]{
        "🐶", "🐱", "🐭", "🐹", "🐰", "🦊",
        "🐻", "🐼", "🐨", "🐯", "🦁", "🐮",
        "🐷", "🐸", "🐵", "🐔", "🐧", "🦄"
    }),

    /** Emoji fruits — 18 unique symbols. */
    FRUITS("Fruits", new String[]{
        "🍎", "🍊", "🍋", "🍇", "🍓", "🫐",
        "🍑", "🍒", "🍍", "🥭", "🍏", "🍐",
        "🍌", "🍉", "🍈", "🥝", "🍆", "🌽"
    }),

    /** Numeric symbols 1-18. */
    NUMBERS("Numbers", new String[]{
        "1",  "2",  "3",  "4",  "5",  "6",
        "7",  "8",  "9",  "10", "11", "12",
        "13", "14", "15", "16", "17", "18"
    });

    // ── Fields ──────────────────────────────────────────────────────────────

    private final String displayName;
    private final String[] symbols;

    // ── Constructor ──────────────────────────────────────────────────────────

    Theme(String displayName, String[] symbols) {
        this.displayName = displayName;
        this.symbols = symbols;
    }

    // ── Accessors ────────────────────────────────────────────────────────────

    /**
     * Returns the human-readable label for use in the UI.
     *
     * @return display name string
     */
    public String getDisplayName() { return displayName; }

    /**
     * Returns a copy of the ordered symbol array for this theme.
     * Index {@code i} corresponds to card pair {@code i}.
     *
     * @return symbol array (length 18)
     */
    public String[] getSymbols() { return symbols.clone(); }

    /**
     * Returns the symbol at the given index (0-based).
     *
     * @param index pair index in [0, 17]
     * @return symbol string
     * @throws ArrayIndexOutOfBoundsException if index is out of range
     */
    public String getSymbol(int index) { return symbols[index]; }

    /** {@inheritDoc} */
    @Override
    public String toString() { return displayName; }
}
