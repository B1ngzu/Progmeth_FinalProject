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
    }),

    /** Anime theme -18 unique char. */
    ANIME("Anime", new String[]{
                "/images/asuka-removebg-preview.png",
                "/images/asuka-removebg-preview.png",
                "/images/chinatsu-removebg-preview.png",
                "/images/elysia-removebg-preview.png",
                "/images/frieren-removebg-preview.png",
                "/images/gojo-removebg-preview.png",
                "/images/heero-removebg-preview.png",
                "/images/kaguya-removebg-preview.png",
                "/images/lena-removebg-preview.png",
                "/images/mai-removebg-preview.png",
                "/images/mikasa-removebg-preview.png",
                "/images/nesekoi-removebg-preview.png",
                "/images/punpun-removebg-preview.png",
                "/images/reiji-removebg-preview.png",
                "/images/reze-removebg-preview.png",
                "/images/ruby-removebg-preview.png",
                "/images/sasuke-removebg-preview.png",
                "/images/waguri-removebg-preview.png",
                "/images/yukino-removebg-preview.png"
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
