package Progmeth_project2.view;

import Progmeth_project2.controller.MenuController;
import Progmeth_project2.model.Difficulty;
import Progmeth_project2.model.Theme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * The application's main-menu scene.
 *
 * <p>Provides difficulty and theme selection, and navigates to the game scene,
 * leaderboard scene, and settings scene via {@link MenuController}.</p>
 */
public class MainMenuScene extends BaseScene {

    // ── Controller ────────────────────────────────────────────────────────────

    private final MenuController controller;

    // ── UI nodes ─────────────────────────────────────────────────────────────

    private ComboBox<Difficulty> difficultyBox;
    private ComboBox<Theme>      themeBox;
    private Button playButton;
    private Button leaderboardButton;
    private Button settingsButton;
    private Button exitButton;
    private Label  playerNameLabel;
    private TextField playerNameField;

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Creates the main-menu scene.
     *
     * @param stage      the application stage
     * @param controller the menu controller handling navigation
     */
    public MainMenuScene(Stage stage, MenuController controller) {
        super(stage);
        this.controller = controller;
    }

    // ── BaseScene lifecycle ───────────────────────────────────────────────────

    /** {@inheritDoc} */
    @Override
    protected void setupLayout() {
        VBox root = new VBox(24);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #0D1B2A, #1A3A6B);");

        // Title
        Label title = new Label("MEMORY MATCH");
        title.setStyle("-fx-font-size: 52; -fx-font-weight: bold;"
                + "-fx-text-fill: linear-gradient(#FFD700, #FFA500);"
                + "-fx-effect: dropshadow(gaussian, rgba(255,215,0,0.7), 20, 0.5, 0, 0);");

        Label subtitle = new Label("Card Matching Game");
        subtitle.setStyle("-fx-font-size: 20; -fx-text-fill: rgba(255,255,255,0.7);");

        // Player name
        playerNameLabel = new Label("Player Name:");
        playerNameLabel.setStyle("-fx-text-fill: #BDC3C7; -fx-font-size: 14;");
        playerNameField = new TextField("Player");
        playerNameField.setMaxWidth(220);
        styleTextField(playerNameField);

        VBox nameBox = new VBox(6, playerNameLabel, playerNameField);
        nameBox.setAlignment(Pos.CENTER);

        // Difficulty selection
        Label diffLabel = new Label("Difficulty:");
        diffLabel.setStyle("-fx-text-fill: #BDC3C7; -fx-font-size: 14;");
        difficultyBox = new ComboBox<>();
        difficultyBox.getItems().addAll(Difficulty.values());
        difficultyBox.setValue(Difficulty.EASY);
        styleComboBox(difficultyBox);

        // Theme selection
        Label themeLabel = new Label("Card Theme:");
        themeLabel.setStyle("-fx-text-fill: #BDC3C7; -fx-font-size: 14;");
        themeBox = new ComboBox<>();
        themeBox.getItems().addAll(Theme.values());
        themeBox.setValue(Theme.ANIMALS);
        styleComboBox(themeBox);

        // Difficulty / theme row
        HBox selectionRow = new HBox(40,
                new VBox(6, diffLabel, difficultyBox),
                new VBox(6, themeLabel, themeBox));
        selectionRow.setAlignment(Pos.CENTER);

        // Buttons
        playButton = createMenuButton("▶  Play Game", "#27AE60", "#1E8449");
        leaderboardButton = createMenuButton("🏆  Leaderboard", "#2980B9", "#1A6FA8");
        settingsButton = createMenuButton("⚙  Settings", "#7D3C98", "#6C3483");
        exitButton = createMenuButton("✕  Exit", "#C0392B", "#A93226");

        VBox buttons = new VBox(12,
                playButton, leaderboardButton, settingsButton, exitButton);
        buttons.setAlignment(Pos.CENTER);

        // Difficulty info
        Label diffInfo = buildDifficultyInfo();

        root.getChildren().addAll(
                title, subtitle,
                new Separator(),
                nameBox,
                selectionRow,
                diffInfo,
                new Separator(),
                buttons
        );

        // No explicit width/height: the scene fills whatever size the stage has
        // (maximized on startup), preventing a native resize that would undo the
        // maximized state when stage.setScene() is called.
        scene = new Scene(root);
    }

    /** {@inheritDoc} */
    @Override
    protected void bindEvents() {
        playButton.setOnAction(e -> controller.onPlay(
                playerNameField.getText(),
                difficultyBox.getValue(),
                themeBox.getValue()));

        leaderboardButton.setOnAction(e -> controller.onLeaderboard());
        settingsButton.setOnAction(e -> controller.onSettings());
        exitButton.setOnAction(e -> controller.onExit());
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private Button createMenuButton(String text, String colorA, String colorB) {
        Button btn = new Button(text);
        btn.setPrefWidth(260);
        btn.setPrefHeight(48);
        btn.setStyle(
            "-fx-background-color: linear-gradient(to bottom, " + colorA + ", " + colorB + ");"
          + "-fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold;"
          + "-fx-background-radius: 24; -fx-cursor: hand;"
          + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 8, 0, 0, 3);"
        );
        btn.setOnMouseEntered(e -> btn.setOpacity(0.85));
        btn.setOnMouseExited(e -> btn.setOpacity(1.0));
        return btn;
    }

    private void styleTextField(TextField tf) {
        tf.setStyle("-fx-background-color: rgba(255,255,255,0.1);"
                + "-fx-text-fill: white; -fx-font-size: 14;"
                + "-fx-border-color: rgba(255,255,255,0.3); -fx-border-radius: 6;"
                + "-fx-background-radius: 6; -fx-padding: 6 10;");
    }

    private <T> void styleComboBox(ComboBox<T> box) {
        box.setPrefWidth(160);
        box.setStyle("-fx-background-color: rgba(255,255,255,0.1);"
                + "-fx-text-fill: white; -fx-font-size: 14;"
                + "-fx-border-color: rgba(255,255,255,0.3); -fx-border-radius: 6;"
                + "-fx-background-radius: 6;");
    }

    private Label buildDifficultyInfo() {
        Label info = new Label(
            "Easy: 4×4 grid  ·  Medium: 5×6 grid  ·  Hard: 6×6 grid");
        info.setStyle("-fx-text-fill: rgba(255,255,255,0.5); -fx-font-size: 12;");
        return info;
    }
}
