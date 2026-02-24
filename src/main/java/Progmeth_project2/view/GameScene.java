package Progmeth_project2.view;

import Progmeth_project2.controller.GameController;
import Progmeth_project2.model.GameState;
import Progmeth_project2.model.card.BaseCard;
import Progmeth_project2.model.powerup.BasePowerUp;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The primary game-play scene.
 *
 * <p>Displays the card grid, HUD (score, combo, timer, level), and power-up
 * buttons.  A transparent overlay pane sits above the grid to host floating
 * combo-text animations without interfering with card click events.</p>
 *
 * <p>The view is intentionally "dumb": it exposes methods for the
 * {@link GameController} to call, but does not contain game logic.</p>
 */
public class GameScene extends BaseScene {

    // ── Model reference (read-only) ───────────────────────────────────────────

    private final GameState gameState;

    // ── Controller (set after construction) ───────────────────────────────────

    private GameController controller;

    // ── UI nodes ─────────────────────────────────────────────────────────────

    private Label scoreLabel;
    private Label comboLabel;
    private Label timerLabel;
    private Label levelLabel;
    private Label matchesLabel;
    private Label freezeIndicator;

    private GridPane cardGrid;
    private Pane overlayPane;
    private HBox powerUpBar;
    private Button menuButton;

    // ── Card views ────────────────────────────────────────────────────────────

    private final List<CardView> cardViews = new ArrayList<>();
    private final Map<String, CardView> cardViewById = new HashMap<>();
    private final List<PowerUpView> powerUpViews = new ArrayList<>();

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Creates a new {@code GameScene} for the given game state.
     *
     * @param stage     the application stage
     * @param gameState the current game state (used to build the initial grid)
     */
    public GameScene(Stage stage, GameState gameState) {
        super(stage);
        this.gameState = gameState;
    }

    // ── Controller injection ──────────────────────────────────────────────────

    /**
     * Sets the controller that handles card-click and power-up events.
     * Must be called before {@link #show()}.
     *
     * @param controller the game controller
     */
    public void setController(GameController controller) {
        this.controller = controller;
    }

    // ── BaseScene lifecycle ───────────────────────────────────────────────────

    /** {@inheritDoc} */
    @Override
    protected void setupLayout() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0D1B2A;");

        // Header HUD
        root.setTop(buildHeader());

        // Card grid area with overlay pane
        StackPane center = new StackPane();
        center.setPadding(new Insets(10));

        cardGrid = new GridPane();
        cardGrid.setAlignment(Pos.CENTER);
        cardGrid.setHgap(8);
        cardGrid.setVgap(6);

        overlayPane = new Pane();
        overlayPane.setMouseTransparent(true); // clicks fall through to cards

        center.getChildren().addAll(cardGrid, overlayPane);
        root.setCenter(center);

        // Footer: power-ups + menu button
        root.setBottom(buildFooter());

        buildCardGrid();

        // No explicit width/height — see MainMenuScene for rationale.
        scene = new Scene(root);
    }

    /** {@inheritDoc} */
    @Override
    protected void bindEvents() {
        // Card clicks
        for (CardView cv : cardViews) {
            cv.setOnMouseClicked(e -> {
                if (controller != null) controller.onCardClicked(cv);
            });
            cv.setHoverEnabled(true);
        }

        // Power-up clicks
        for (PowerUpView pv : powerUpViews) {
            pv.setOnAction(e -> {
                if (controller != null) controller.onPowerUpClicked(pv.getPowerUp());
            });
        }

        // Menu button
        menuButton.setOnAction(e -> {
            if (controller != null) controller.onMenuRequested();
        });
    }

    // ── Grid construction ─────────────────────────────────────────────────────

    /**
     * Populates the card grid from the game state's card list.
     * Clears any existing cards first (used when rebuilding for a new level).
     */
    public void buildCardGrid() {
        cardGrid.getChildren().clear();
        cardViews.clear();
        cardViewById.clear();

        List<BaseCard> cards = gameState.getCards();
        int cols = gameState.getDifficulty().getColumns();
        int rows = gameState.getDifficulty().getRows();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int index = r * cols + c;
                if (index >= cards.size()) break;
                BaseCard model = cards.get(index);
                CardView cv = new CardView(model);
                cardViews.add(cv);
                cardViewById.put(model.getCardId(), cv);
                cardGrid.add(cv, c, r);
            }
        }
    }

    // ── HUD update methods ────────────────────────────────────────────────────

    /**
     * Updates all HUD labels to reflect the current game state values.
     * Safe to call on any thread (delegates to {@link javafx.application.Platform#runLater}
     * via the controller if needed; direct call assumed to be on FX thread here).
     */
    public void updateHud() {
        scoreLabel.setText(String.format("Score: %,d", gameState.getScore()));
        int combo = gameState.getCombo();
        comboLabel.setText("Combo: ×" + (combo == 0 ? 1 : combo));
        comboLabel.setStyle(buildComboStyle(combo));

        int t = gameState.getTimeRemaining();
        timerLabel.setText(String.format("⏱ %02d:%02d", t / 60, t % 60));
        timerLabel.setStyle(buildTimerStyle(t));

        levelLabel.setText("Level " + gameState.getLevel());
        matchesLabel.setText(gameState.getMatchesFound()
                + " / " + gameState.getDifficulty().getTotalPairs() + " pairs");

        freezeIndicator.setVisible(gameState.isTimerFrozen());
    }

    /**
     * Refreshes all power-up button states (available / consumed).
     */
    public void refreshPowerUps() {
        for (PowerUpView pv : powerUpViews) {
            pv.refresh();
        }
    }

    /**
     * Locks the card grid so no further card-click events are delivered to the
     * controller.  Used while a pair is being evaluated or during level transitions.
     *
     * @param locked {@code true} to prevent card clicks
     */
    public void setGridLocked(boolean locked) {
        for (CardView cv : cardViews) {
            cv.setHoverEnabled(!locked);
            cv.setMouseTransparent(locked);
        }
    }

    /**
     * Shows or hides the hint highlight on the pair of cards identified by the
     * game state's hint card IDs.
     *
     * @param show {@code true} to highlight; {@code false} to clear
     */
    public void setHintHighlight(boolean show) {
        List<String> ids = gameState.getHintCardIds();
        for (CardView cv : cardViews) {
            boolean shouldHighlight = show && ids.contains(cv.getModel().getCardId());
            cv.setHighlighted(shouldHighlight);
        }
    }

    // ── Accessors ─────────────────────────────────────────────────────────────

    /**
     * Returns all card views currently in the grid.
     *
     * @return unmodifiable list of card views
     */
    public List<CardView> getCardViews() {
        return List.copyOf(cardViews);
    }

    /**
     * Looks up a card view by the model card's ID.
     *
     * @param cardId the card ID to look up
     * @return the matching {@link CardView}, or {@code null} if not found
     */
    public CardView getCardViewById(String cardId) {
        return cardViewById.get(cardId);
    }

    /**
     * Returns the overlay pane used for floating text animations (combo text).
     *
     * @return the overlay pane
     */
    public Pane getOverlayPane() {
        return overlayPane;
    }

    // ── Private builders ──────────────────────────────────────────────────────

    private HBox buildHeader() {
        scoreLabel  = new Label("Score: 0");
        comboLabel  = new Label("Combo: ×1");
        timerLabel  = new Label("⏱ 02:00");
        levelLabel  = new Label("Level 1");
        matchesLabel = new Label("0 / 0 pairs");
        freezeIndicator = new Label("❄ FROZEN");

        styleHudLabel(scoreLabel, 16);
        styleHudLabel(comboLabel, 16);
        styleHudLabel(timerLabel, 18);
        styleHudLabel(levelLabel, 16);
        styleHudLabel(matchesLabel, 14);

        freezeIndicator.setStyle("-fx-text-fill: #00BFFF; -fx-font-size: 14;"
                + "-fx-font-weight: bold;");
        freezeIndicator.setVisible(false);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(16,
                levelLabel, matchesLabel,
                spacer,
                freezeIndicator, comboLabel, scoreLabel, timerLabel);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(12, 20, 12, 20));
        header.setStyle("-fx-background-color: #0A1628;"
                + "-fx-border-color: #1E3A5F; -fx-border-width: 0 0 2 0;");
        return header;
    }

    private Region buildFooter() {
        powerUpBar = new HBox(12);
        powerUpBar.setAlignment(Pos.CENTER);
        // Stretch to fill the full width so the HBox centres its children
        // against the whole footer, not just the space left over by the menu button.
        powerUpBar.setMaxWidth(Double.MAX_VALUE);

        for (BasePowerUp pu : gameState.getPowerUps()) {
            PowerUpView pv = new PowerUpView(pu);
            powerUpViews.add(pv);
            powerUpBar.getChildren().add(pv);
        }

        menuButton = new Button("☰ Menu");
        menuButton.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #4A4A6A, #3A3A5A);"
          + "-fx-text-fill: white; -fx-font-size: 13; -fx-font-weight: bold;"
          + "-fx-background-radius: 8; -fx-cursor: hand;"
          + "-fx-border-color: rgba(255,255,255,0.2); -fx-border-width: 1;"
          + "-fx-border-radius: 8;"
        );

        // Layer 1 (bottom): power-up bar stretched to full width, centred.
        // Layer 2 (top):    menu button pinned to the right edge.
        //
        // StackPane overlays the two layers so powerUpBar centres against the
        // *entire* footer width, completely independent of the menu button.
        HBox menuLayer = new HBox(menuButton);
        menuLayer.setAlignment(Pos.CENTER_RIGHT);
        menuLayer.setPickOnBounds(false); // let clicks pass through empty areas
        menuLayer.setMouseTransparent(false);

        StackPane footer = new StackPane(powerUpBar, menuLayer);
        footer.setPadding(new Insets(12, 20, 16, 20));
        footer.setStyle("-fx-background-color: #0A1628;"
                + "-fx-border-color: #1E3A5F; -fx-border-width: 2 0 0 0;");
        return footer;
    }

    private void styleHudLabel(Label label, int fontSize) {
        label.setStyle("-fx-text-fill: white; -fx-font-size: " + fontSize + ";"
                + "-fx-font-weight: bold;");
    }

    private String buildComboStyle(int combo) {
        String color = combo >= 4 ? "#FF4444"
                     : combo >= 2 ? "#FFD700"
                     : "white";
        return "-fx-text-fill: " + color + "; -fx-font-size: 16; -fx-font-weight: bold;";
    }

    private String buildTimerStyle(int seconds) {
        String color = seconds <= 10 ? "#FF4444"
                     : seconds <= 30 ? "#FF8C00"
                     : "white";
        return "-fx-text-fill: " + color + "; -fx-font-size: 18; -fx-font-weight: bold;";
    }
}
