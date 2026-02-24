package Progmeth_project2.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * An immutable record of a single completed game session stored in the
 * {@link Leaderboard}.
 *
 * <p>Implements {@link Comparable} so that entries can be sorted in descending
 * score order (highest score first) for display purposes.</p>
 *
 * <p>Implements {@link Serializable} to support Java-object serialisation used
 * by {@link Leaderboard#save(String)} and {@link Leaderboard#load(String)}.</p>
 */
public class ScoreEntry implements Comparable<ScoreEntry>, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // ── Fields ──────────────────────────────────────────────────────────────

    private final String playerName;
    private final int score;
    private final Difficulty difficulty;
    private final int level;
    private final LocalDateTime timestamp;

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Creates a new score entry with the current timestamp.
     *
     * @param playerName player's display name (trimmed, non-null)
     * @param score      final score achieved
     * @param difficulty difficulty level played
     * @param level      highest level reached
     */
    public ScoreEntry(String playerName, int score, Difficulty difficulty, int level) {
        this.playerName = playerName == null ? "Player" : playerName.trim();
        this.score = Math.max(0, score);
        this.difficulty = difficulty;
        this.level = Math.max(1, level);
        this.timestamp = LocalDateTime.now();
    }

    // ── Accessors ────────────────────────────────────────────────────────────

    /**
     * Returns the player's display name.
     *
     * @return player name string
     */
    public String getPlayerName() { return playerName; }

    /**
     * Returns the final score achieved.
     *
     * @return score value (≥ 0)
     */
    public int getScore() { return score; }

    /**
     * Returns the difficulty level selected for this game.
     *
     * @return difficulty enum constant
     */
    public Difficulty getDifficulty() { return difficulty; }

    /**
     * Returns the highest level the player reached.
     *
     * @return level number (≥ 1)
     */
    public int getLevel() { return level; }

    /**
     * Returns the date and time when this entry was recorded.
     *
     * @return timestamp
     */
    public LocalDateTime getTimestamp() { return timestamp; }

    /**
     * Returns a formatted date/time string suitable for UI display.
     *
     * @return formatted timestamp
     */
    public String getFormattedTimestamp() {
        return timestamp.format(DISPLAY_FORMATTER);
    }

    // ── Comparable ───────────────────────────────────────────────────────────

    /**
     * {@inheritDoc}
     * Higher scores sort before lower scores (descending order).
     * Entries with equal scores are ordered by timestamp (most recent first).
     */
    @Override
    public int compareTo(ScoreEntry other) {
        int cmp = Integer.compare(other.score, this.score); // descending
        if (cmp != 0) return cmp;
        return other.timestamp.compareTo(this.timestamp);   // newer first
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format("%-15s %6d  %-6s  Lv%d  %s",
                playerName, score, difficulty.getDisplayName(),
                level, getFormattedTimestamp());
    }
}
