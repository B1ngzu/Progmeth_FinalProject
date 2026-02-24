package Progmeth_project2.controller;

import Progmeth_project2.model.Difficulty;
import Progmeth_project2.model.Leaderboard;
import Progmeth_project2.model.ScoreEntry;
import Progmeth_project2.util.FileManager;
import Progmeth_project2.view.LeaderboardScene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller for the {@link LeaderboardScene}.
 *
 * <p>Loads the persisted leaderboard on construction, provides a method for
 * the game controller to add new scores, and handles the "clear" action from
 * the view.</p>
 */
public class LeaderboardController {

    // ── Fields ────────────────────────────────────────────────────────────────

    private final Stage stage;
    private final Leaderboard leaderboard;
    private final String savePath;
    private LeaderboardScene view;

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Creates a {@code LeaderboardController} and loads the persisted leaderboard
     * from disk.  If the file does not exist or is unreadable, the leaderboard
     * starts empty.
     *
     * @param stage the application stage (non-null)
     */
    public LeaderboardController(Stage stage) {
        this.stage = stage;
        this.leaderboard = new Leaderboard();
        this.savePath = FileManager.getLeaderboardPath();
        loadLeaderboard();
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    /**
     * Shows the leaderboard scene populated with the current top entries.
     */
    public void show() {
        view = new LeaderboardScene(stage, this);
        view.show();
        view.setEntries(leaderboard.getTopEntries());
    }

    /**
     * Adds a new score entry to the leaderboard and saves it to disk.
     * Can be called from the {@link GameController} after a game ends.
     *
     * @param playerName the player's display name
     * @param score      the final score achieved
     * @param difficulty the difficulty played
     * @param level      the highest level reached
     */
    public void addScore(String playerName, int score,
                         Difficulty difficulty, int level) {
        ScoreEntry entry = new ScoreEntry(playerName, score, difficulty, level);
        leaderboard.addEntry(entry);
        saveLeaderboard();
    }

    /**
     * Called when the player clicks "Back" on the leaderboard scene.
     */
    public void onBack() {
        MenuController menuCtrl = new MenuController(stage);
        menuCtrl.showMenu();
    }

    /**
     * Called when the player clicks "Clear Board" on the leaderboard scene.
     * Clears the in-memory leaderboard, saves the empty state, and refreshes
     * the view.
     */
    public void onClear() {
        leaderboard.clear();
        saveLeaderboard();
        if (view != null) {
            view.setEntries(leaderboard.getTopEntries());
        }
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void loadLeaderboard() {
        try {
            leaderboard.load(savePath);
        } catch (IOException e) {
            // No saved data yet; start with an empty leaderboard
            System.err.println("Leaderboard load skipped: " + e.getMessage());
        }
    }

    private void saveLeaderboard() {
        try {
            leaderboard.save(savePath);
        } catch (IOException e) {
            System.err.println("Leaderboard save failed: " + e.getMessage());
        }
    }
}
