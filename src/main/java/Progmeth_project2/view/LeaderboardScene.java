package Progmeth_project2.view;

import Progmeth_project2.controller.LeaderboardController;
import Progmeth_project2.model.ScoreEntry;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

/**
 * Scene that displays the top-10 {@link ScoreEntry} records from the
 * persisted leaderboard.
 *
 * <p>The controller loads entries before calling {@link #show()}; the scene
 * simply renders whatever entries are provided via {@link #setEntries(List)}.</p>
 */
public class LeaderboardScene extends BaseScene {

    // ── Controller ────────────────────────────────────────────────────────────

    private final LeaderboardController controller;

    // ── UI nodes ─────────────────────────────────────────────────────────────

    private VBox entriesBox;
    private Button backButton;
    private Button clearButton;

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Creates the leaderboard scene.
     *
     * @param stage      the application stage
     * @param controller the leaderboard controller
     */
    public LeaderboardScene(Stage stage, LeaderboardController controller) {
        super(stage);
        this.controller = controller;
    }

    // ── BaseScene lifecycle ───────────────────────────────────────────────────

    /** {@inheritDoc} */
    @Override
    protected void setupLayout() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #0D1B2A, #1A3A6B);");

        // Title
        Label title = new Label("🏆  Leaderboard");
        title.setStyle("-fx-font-size: 40; -fx-font-weight: bold;"
                + "-fx-text-fill: #FFD700;"
                + "-fx-effect: dropshadow(gaussian, rgba(255,215,0,0.5), 12, 0.4, 0, 0);");

        Label subtitle = new Label("Top 10 Scores");
        subtitle.setStyle("-fx-font-size: 16; -fx-text-fill: rgba(255,255,255,0.6);");

        // Table header
        HBox header = buildRowHeader();

        // Entries
        entriesBox = new VBox(6);
        entriesBox.setAlignment(Pos.TOP_CENTER);

        ScrollPane scroll = new ScrollPane(entriesBox);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scroll.setPrefHeight(380);

        // Buttons
        backButton  = createButton("◀  Back to Menu", "#2980B9", "#1A6FA8");
        clearButton = createButton("🗑  Clear Board",  "#C0392B", "#A93226");

        HBox buttonRow = new HBox(20, backButton, clearButton);
        buttonRow.setAlignment(Pos.CENTER);

        root.getChildren().addAll(title, subtitle, header, scroll, buttonRow);
        // No explicit width/height — see MainMenuScene for rationale.
        scene = new Scene(root);
    }

    /** {@inheritDoc} */
    @Override
    protected void bindEvents() {
        backButton.setOnAction(e -> controller.onBack());
        clearButton.setOnAction(e -> controller.onClear());
    }

    // ── Data binding ──────────────────────────────────────────────────────────

    /**
     * Populates the leaderboard view with the given list of entries.
     * The list is expected to be sorted in descending score order.
     *
     * @param entries score entries to display
     */
    public void setEntries(List<ScoreEntry> entries) {
        entriesBox.getChildren().clear();
        if (entries.isEmpty()) {
            Label empty = new Label("No scores yet — go play!");
            empty.setStyle("-fx-text-fill: rgba(255,255,255,0.5); -fx-font-size: 18;");
            empty.setPadding(new Insets(30));
            entriesBox.getChildren().add(empty);
            return;
        }
        for (int i = 0; i < entries.size(); i++) {
            entriesBox.getChildren().add(buildEntryRow(i + 1, entries.get(i)));
        }
    }

    // ── Private builders ──────────────────────────────────────────────────────

    private HBox buildRowHeader() {
        HBox row = new HBox();
        row.setPadding(new Insets(6, 16, 6, 16));
        row.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-background-radius: 8;");

        String hStyle = "-fx-text-fill: #BDC3C7; -fx-font-size: 13; -fx-font-weight: bold;";
        Label rank  = styledLabel("#", hStyle, 50);
        Label name  = styledLabel("Player", hStyle, 160);
        Label score = styledLabel("Score", hStyle, 100);
        Label diff  = styledLabel("Difficulty", hStyle, 100);
        Label level = styledLabel("Level", hStyle, 70);
        Label date  = styledLabel("Date", hStyle, 160);

        row.getChildren().addAll(rank, name, score, diff, level, date);
        return row;
    }

    private HBox buildEntryRow(int rank, ScoreEntry entry) {
        HBox row = new HBox();
        row.setPadding(new Insets(8, 16, 8, 16));
        row.setAlignment(Pos.CENTER_LEFT);

        String bgColor = rank == 1 ? "rgba(255,215,0,0.12)"
                       : rank == 2 ? "rgba(192,192,192,0.10)"
                       : rank == 3 ? "rgba(205,127,50,0.10)"
                       : "rgba(255,255,255,0.04)";
        String textColor = rank == 1 ? "#FFD700"
                         : rank == 2 ? "#C0C0C0"
                         : rank == 3 ? "#CD7F32"
                         : "white";
        row.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 8;");

        String base = "-fx-text-fill: " + textColor + "; -fx-font-size: 14;";
        String bold = base + "-fx-font-weight: bold;";

        Label rankLbl  = styledLabel(rank + ".", bold,   50);
        Label nameLbl  = styledLabel(entry.getPlayerName(), base, 160);
        Label scoreLbl = styledLabel(String.format("%,d", entry.getScore()), bold, 100);
        Label diffLbl  = styledLabel(entry.getDifficulty().getDisplayName(), base, 100);
        Label levelLbl = styledLabel("Lv " + entry.getLevel(), base, 70);
        Label dateLbl  = styledLabel(entry.getFormattedTimestamp(), base, 160);

        row.getChildren().addAll(rankLbl, nameLbl, scoreLbl, diffLbl, levelLbl, dateLbl);
        return row;
    }

    private Label styledLabel(String text, String style, double width) {
        Label l = new Label(text);
        l.setStyle(style);
        l.setMinWidth(width);
        l.setPrefWidth(width);
        return l;
    }

    private Button createButton(String text, String colorA, String colorB) {
        Button btn = new Button(text);
        btn.setPrefWidth(200);
        btn.setPrefHeight(42);
        btn.setStyle(
            "-fx-background-color: linear-gradient(to bottom, " + colorA + ", " + colorB + ");"
          + "-fx-text-fill: white; -fx-font-size: 15; -fx-font-weight: bold;"
          + "-fx-background-radius: 21; -fx-cursor: hand;"
        );
        return btn;
    }
}
