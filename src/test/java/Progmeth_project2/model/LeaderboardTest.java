package Progmeth_project2.model;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 tests for {@link Leaderboard} and {@link ScoreEntry}.
 *
 * <p>Covers:</p>
 * <ul>
 *   <li>Adding entries and maintaining descending order.</li>
 *   <li>Capacity limit ({@link Leaderboard#MAX_ENTRIES}).</li>
 *   <li>{@link Leaderboard#qualifies(int)} boundary conditions.</li>
 *   <li>Persist/load round-trip using a temporary directory.</li>
 *   <li>{@link ScoreEntry#compareTo(ScoreEntry)} ordering.</li>
 * </ul>
 */
@DisplayName("Leaderboard and ScoreEntry Tests")
class LeaderboardTest {

    private Leaderboard leaderboard;

    @BeforeEach
    void setUp() {
        leaderboard = new Leaderboard();
    }

    // ── Adding entries ────────────────────────────────────────────────────────

    @Test
    @DisplayName("Empty leaderboard has 0 entries")
    void emptyLeaderboard() {
        assertEquals(0, leaderboard.getEntryCount());
        assertTrue(leaderboard.getTopEntries().isEmpty());
    }

    @Test
    @DisplayName("Adding one entry stores it")
    void addOneEntry() {
        leaderboard.addEntry(new ScoreEntry("Alice", 500, Difficulty.EASY, 1));
        assertEquals(1, leaderboard.getEntryCount());
        assertEquals("Alice", leaderboard.getTopEntries().get(0).getPlayerName());
    }

    @Test
    @DisplayName("Entries are sorted in descending score order")
    void entriesDescendingOrder() {
        leaderboard.addEntry(new ScoreEntry("Alice", 300, Difficulty.EASY,   1));
        leaderboard.addEntry(new ScoreEntry("Bob",   500, Difficulty.MEDIUM, 2));
        leaderboard.addEntry(new ScoreEntry("Carol", 100, Difficulty.HARD,   3));

        List<ScoreEntry> top = leaderboard.getTopEntries();
        assertEquals(500, top.get(0).getScore(), "Highest score should be first");
        assertEquals(300, top.get(1).getScore());
        assertEquals(100, top.get(2).getScore());
    }

    @Test
    @DisplayName("Leaderboard retains at most MAX_ENTRIES entries")
    void capacityLimit() {
        for (int i = 1; i <= Leaderboard.MAX_ENTRIES + 5; i++) {
            leaderboard.addEntry(new ScoreEntry("P" + i, i * 10, Difficulty.EASY, 1));
        }
        assertEquals(Leaderboard.MAX_ENTRIES, leaderboard.getEntryCount(),
            "Leaderboard must not exceed MAX_ENTRIES");
    }

    @Test
    @DisplayName("Lowest-scoring entry is evicted when board is full")
    void lowestEntryEvicted() {
        // Fill board with scores 10..100
        for (int i = 1; i <= Leaderboard.MAX_ENTRIES; i++) {
            leaderboard.addEntry(new ScoreEntry("P" + i, i * 10, Difficulty.EASY, 1));
        }
        // Minimum score on board is 10
        leaderboard.addEntry(new ScoreEntry("High", 9999, Difficulty.HARD, 5));

        // Score 9999 should be first
        assertEquals(9999, leaderboard.getTopEntries().get(0).getScore());
        // Score 10 (the original minimum) should have been evicted
        boolean hasMin = leaderboard.getTopEntries().stream()
                .anyMatch(e -> e.getScore() == 10 && e.getPlayerName().equals("P1"));
        assertFalse(hasMin, "Score 10 should have been evicted");
    }

    // ── qualifies() ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("qualifies() returns true on an empty board")
    void qualifiesOnEmpty() {
        assertTrue(leaderboard.qualifies(0));
    }

    @Test
    @DisplayName("qualifies() returns true when board is not full")
    void qualifiesWhenNotFull() {
        leaderboard.addEntry(new ScoreEntry("A", 100, Difficulty.EASY, 1));
        assertTrue(leaderboard.qualifies(1), "Any score qualifies when board not full");
    }

    @Test
    @DisplayName("qualifies() returns false for score below minimum when board is full")
    void doesNotQualifyBelowMinimum() {
        for (int i = 1; i <= Leaderboard.MAX_ENTRIES; i++) {
            leaderboard.addEntry(new ScoreEntry("P" + i, i * 100, Difficulty.EASY, 1));
        }
        // Minimum score is 100
        assertFalse(leaderboard.qualifies(50),
            "Score below current minimum should not qualify");
    }

    @Test
    @DisplayName("qualifies() returns true for score above minimum when board is full")
    void qualifiesAboveMinimum() {
        for (int i = 1; i <= Leaderboard.MAX_ENTRIES; i++) {
            leaderboard.addEntry(new ScoreEntry("P" + i, i * 100, Difficulty.EASY, 1));
        }
        assertTrue(leaderboard.qualifies(9999));
    }

    // ── clear() ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("clear() removes all entries")
    void clearRemovesAll() {
        leaderboard.addEntry(new ScoreEntry("A", 100, Difficulty.EASY, 1));
        leaderboard.clear();
        assertEquals(0, leaderboard.getEntryCount());
        assertTrue(leaderboard.getTopEntries().isEmpty());
    }

    // ── Persistence round-trip ────────────────────────────────────────────────

    @Test
    @DisplayName("save() then load() preserves all entries in order")
    void saveLoadRoundTrip(@TempDir Path tempDir) throws IOException {
        leaderboard.addEntry(new ScoreEntry("Alice", 800, Difficulty.HARD,   3));
        leaderboard.addEntry(new ScoreEntry("Bob",   500, Difficulty.MEDIUM, 2));
        leaderboard.addEntry(new ScoreEntry("Carol", 200, Difficulty.EASY,   1));

        String path = tempDir.resolve("leaderboard.dat").toString();
        leaderboard.save(path);

        Leaderboard loaded = new Leaderboard();
        loaded.load(path);

        assertEquals(3, loaded.getEntryCount(), "Loaded board should have 3 entries");
        assertEquals("Alice", loaded.getTopEntries().get(0).getPlayerName(),
            "Highest scorer should be first after reload");
        assertEquals(800, loaded.getTopEntries().get(0).getScore());
    }

    @Test
    @DisplayName("load() on non-existent file leaves leaderboard empty")
    void loadNonExistentFile(@TempDir Path tempDir) throws IOException {
        String noFile = tempDir.resolve("does_not_exist.dat").toString();
        assertDoesNotThrow(() -> leaderboard.load(noFile));
        assertEquals(0, leaderboard.getEntryCount());
    }

    @Test
    @DisplayName("save() creates parent directories if they do not exist")
    void saveCreatesParentDir(@TempDir Path tempDir) throws IOException {
        String nestedPath = tempDir.resolve("subdir/sub2/lb.dat").toString();
        leaderboard.addEntry(new ScoreEntry("X", 42, Difficulty.EASY, 1));
        assertDoesNotThrow(() -> leaderboard.save(nestedPath));
        assertTrue(new File(nestedPath).exists(), "Leaderboard file should have been created");
    }

    // ── ScoreEntry ordering ────────────────────────────────────────────────────

    @Test
    @DisplayName("ScoreEntry compareTo: higher score sorts first (ascending sort yields highest last)")
    void scoreEntryOrdering() {
        ScoreEntry low  = new ScoreEntry("Low",  100, Difficulty.EASY, 1);
        ScoreEntry high = new ScoreEntry("High", 900, Difficulty.HARD, 5);

        // compareTo returns negative when this > other (we want desc)
        assertTrue(high.compareTo(low) < 0,
            "High score should come before low score in natural order");
        assertTrue(low.compareTo(high) > 0);
    }

    @Test
    @DisplayName("ScoreEntry toString contains player name and score")
    void toStringContainsKeyInfo() {
        ScoreEntry e = new ScoreEntry("TestPlayer", 1234, Difficulty.MEDIUM, 2);
        String s = e.toString();
        assertTrue(s.contains("TestPlayer"), "toString should include player name");
        assertTrue(s.contains("1234"),       "toString should include score");
    }
}
