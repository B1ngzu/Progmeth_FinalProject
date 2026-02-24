package Progmeth_project2.model;

/**
 * Represents the three difficulty levels available in the game.
 * Each level defines grid dimensions, a countdown timer (seconds), and a
 * per-pair base score that is multiplied by the current combo multiplier.
 */
public enum Difficulty {

    /** 4 × 4 grid — 8 pairs, 120 s, 10 pts per pair. */
    EASY(4, 4, 120, 10, "Easy"),

    /** 6 × 5 grid — 15 pairs, 180 s, 15 pts per pair. */
    MEDIUM(6, 5, 180, 15, "Medium"),

    /** 6 × 5 grid — 15 pairs, 240 s, 20 pts per pair. */
    HARD(6, 5, 240, 20, "Hard");

    // ── Fields ──────────────────────────────────────────────────────────────

    private final int columns;
    private final int rows;
    private final int timerSeconds;
    private final int baseScore;
    private final String displayName;

    // ── Constructor ──────────────────────────────────────────────────────────

    Difficulty(int columns, int rows, int timerSeconds, int baseScore, String displayName) {
        this.columns = columns;
        this.rows = rows;
        this.timerSeconds = timerSeconds;
        this.baseScore = baseScore;
        this.displayName = displayName;
    }

    // ── Accessors ────────────────────────────────────────────────────────────

    /**
     * Returns the number of columns in the card grid.
     *
     * @return column count
     */
    public int getColumns() { return columns; }

    /**
     * Returns the number of rows in the card grid.
     *
     * @return row count
     */
    public int getRows() { return rows; }

    /**
     * Returns the total number of cards ({@code columns × rows}).
     *
     * @return total card count
     */
    public int getTotalCards() { return columns * rows; }

    /**
     * Returns the total number of unique pairs ({@code columns × rows / 2}).
     *
     * @return pair count
     */
    public int getTotalPairs() { return (columns * rows) / 2; }

    /**
     * Returns the starting countdown value in seconds.
     *
     * @return seconds on the timer at level start
     */
    public int getTimerSeconds() { return timerSeconds; }

    /**
     * Returns the base points awarded for each matched pair before combo
     * multiplication.
     *
     * @return base score per match
     */
    public int getBaseScore() { return baseScore; }

    /**
     * Returns a human-readable label suitable for display in the UI.
     *
     * @return display name string
     */
    public String getDisplayName() { return displayName; }

    /** {@inheritDoc} */
    @Override
    public String toString() { return displayName; }
}
