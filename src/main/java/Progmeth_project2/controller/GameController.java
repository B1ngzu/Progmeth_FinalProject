package Progmeth_project2.controller;

import Progmeth_project2.model.GameState;
import Progmeth_project2.model.card.BaseCard;
import Progmeth_project2.model.powerup.BasePowerUp;
import Progmeth_project2.model.powerup.FreezePowerUp;
import Progmeth_project2.model.powerup.HintPowerUp;
import Progmeth_project2.model.powerup.RevealPowerUp;
import Progmeth_project2.util.AnimationManager;
import Progmeth_project2.util.SoundManager;
import Progmeth_project2.view.CardView;
import Progmeth_project2.view.GameScene;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller for the game-play scene.
 *
 * <p>Sits between the {@link GameState} model and the {@link GameScene} view,
 * implementing the MVC controller role.  Responsibilities include:</p>
 * <ul>
 *   <li>Handling card-flip events from the view.</li>
 *   <li>Evaluating match / mismatch and triggering animations via
 *       {@link AnimationManager}.</li>
 *   <li>Managing the countdown timer via a JavaFX {@link Timeline}.</li>
 *   <li>Activating power-ups and updating the view accordingly.</li>
 *   <li>Detecting level completion and triggering level advancement.</li>
 *   <li>Detecting game-over (timer expired) and persisting the score.</li>
 * </ul>
 */
public class GameController {

    // ── Dependencies ──────────────────────────────────────────────────────────

    private final Stage stage;
    private final GameState gameState;
    private final GameScene gameScene;
    private final String playerName;

    // ── State ─────────────────────────────────────────────────────────────────

    /** Cards currently face-up but not yet matched (max 2 at a time). */
    private final List<CardView> selectedCards = new ArrayList<>();

    /** True while a flip/match/mismatch animation is in progress. */
    private boolean animating = false;

    /** The JavaFX Timeline used as the countdown timer. */
    private Timeline countdownTimer;

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Creates a {@code GameController}.
     * Call {@link #start()} after the associated {@link GameScene} has been
     * shown so that all HUD labels are initialised before the first
     * {@code updateHud()} call.
     *
     * @param stage      the application stage
     * @param gameState  the current game state
     * @param gameScene  the game view
     * @param playerName the player's display name
     */
    public GameController(Stage stage, GameState gameState,
                          GameScene gameScene, String playerName) {
        this.stage = stage;
        this.gameState = gameState;
        this.gameScene = gameScene;
        this.playerName = playerName;
    }

    /**
     * Populates the initial HUD values and starts the countdown timer.
     * Must be called <em>after</em> {@link GameScene#show()} so that all
     * JavaFX {@link javafx.scene.control.Label} fields have been created by
     * {@code setupLayout()}.
     */
    public void start() {
        gameScene.updateHud();
        startTimer();
        SoundManager.getInstance().startBackgroundMusic();

    }

    // ── Card click handler ────────────────────────────────────────────────────

    /**
     * Invoked by the view when the player clicks a card.
     * Ignores the click if the game is animating, the card is already matched,
     * or the card is already face-up in the current selection.
     *
     * @param cardView the clicked card view
     */
    public void onCardClicked(CardView cardView) {
        if (animating) return;
        BaseCard model = cardView.getModel();
        if (model.isMatched()) return;
        if (model.isFaceUp()) return;
        if (selectedCards.contains(cardView)) return;

        // Flip the card (model + view)
        model.flip();
        SoundManager.getInstance().playCardFlip();
        animating = true;

        AnimationManager.getInstance().playFlip(cardView,
            cardView::showFront,   // swap face at midpoint
            () -> {                // after flip animation
                animating = false;
                selectedCards.add(cardView);
                if (selectedCards.size() == 2) {
                    evaluatePair();
                }
            }
        );
    }

    // ── Power-up handler ──────────────────────────────────────────────────────

    /**
     * Invoked by the view when the player clicks a power-up button.
     * Delegates activation to the power-up model and handles the resulting
     * state change in the view.
     *
     * @param powerUp the power-up to activate
     */
    public void onPowerUpClicked(BasePowerUp powerUp) {
        if (!powerUp.isAvailable()) return;

        // Activate through model
        powerUp.use(gameState);
        gameScene.refreshPowerUps();
        AnimationManager.getInstance().playPowerUpFlash(
                gameScene.getScene().getRoot());

        // Handle view side-effects per power-up type
        if (powerUp instanceof RevealPowerUp) {
            handleReveal();
        } else if (powerUp instanceof HintPowerUp) {
            handleHint();
        }
        // FreezePowerUp only modifies game state; timer logic reads the flag
    }

    // ── Menu handler ─────────────────────────────────────────────────────────

    /**
     * Called when the player clicks the Menu button during play.
     * Pauses the timer and shows a confirmation dialog.
     */
    public void onMenuRequested() {
        stopTimer();

        Stage dialog = new Stage();
        dialog.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dialog.initOwner(stage);
        dialog.initStyle(javafx.stage.StageStyle.TRANSPARENT);
        dialog.setTitle("Return to Menu");

        Label title = new Label("Quit this game?");
        title.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: white;");

        Label sub = new Label("Your current score will be lost.");
        sub.setStyle("-fx-font-size: 13; -fx-text-fill: rgba(255,255,255,0.7);");

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setPrefWidth(120);
        cancelBtn.setStyle(
                "-fx-background-color: #4A4A6A; -fx-text-fill: white;"
                        + "-fx-font-size: 14; -fx-background-radius: 12; -fx-cursor: hand;"
        );

        Button okBtn = new Button("Quit");
        okBtn.setPrefWidth(120);
        okBtn.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #C0392B, #A93226);"
                        + "-fx-text-fill: white; -fx-font-size: 14; -fx-font-weight: bold;"
                        + "-fx-background-radius: 12; -fx-cursor: hand;"
        );

        cancelBtn.setOnAction(e -> {
            dialog.close();
            startTimer();
        });

        okBtn.setOnAction(e -> {
            dialog.close();
            navigateToMenu();
        });

        HBox buttons = new HBox(16, cancelBtn, okBtn);
        buttons.setAlignment(javafx.geometry.Pos.CENTER);

        VBox layout = new VBox(16, title, sub, buttons);
        layout.setAlignment(javafx.geometry.Pos.CENTER);
        layout.setPadding(new javafx.geometry.Insets(30));
        layout.setStyle("-fx-background-color: #1A2A3A; -fx-background-radius: 16;");

        javafx.scene.Scene s = new javafx.scene.Scene(layout, 320, 180);
        s.setFill(javafx.scene.paint.Color.TRANSPARENT);
        dialog.setScene(s);
        dialog.show();
    }

    // ── Private: pair evaluation ───────────────────────────────────────────────

    /**
     * Evaluates the two face-up selected cards.
     * If they match, records the match, plays the match animation, and checks
     * for level completion.  If they don't match, plays the mismatch animation
     * and flips them back after a short delay.
     */
    private void evaluatePair() {
        CardView cvA = selectedCards.get(0);
        CardView cvB = selectedCards.get(1);
        BaseCard mA  = cvA.getModel();
        BaseCard mB  = cvB.getModel();

        gameScene.setGridLocked(true);
        animating = true;

        if (mA.getSymbolKey().equals(mB.getSymbolKey())) {
            // MATCH
            SoundManager.getInstance().playMatch();
            gameState.recordMatch(Math.max(mA.getScore(), mB.getScore()));
            mA.setMatched();
            mB.setMatched();

            // Play combo text if combo ≥ 2
            if (gameState.getCombo() >= 2) {
                SoundManager.getInstance().playCombo();
                playComboAnimation(cvA);
            }

            AnimationManager.getInstance().playMatch(cvA, cvB, () -> {
                cvA.applyMatchedStyle();
                cvB.applyMatchedStyle();
                selectedCards.clear();
                animating = false;
                gameScene.setGridLocked(false);
                gameScene.updateHud();
                checkLevelComplete();
            });
        } else {
            // MISMATCH
            SoundManager.getInstance().playMismatch();
            gameState.recordMismatch();

            AnimationManager.getInstance().playMismatch(cvA, cvB);

            // Flip back after 900 ms
            PauseTransition pause = new PauseTransition(Duration.millis(900));
            pause.setOnFinished(e -> {
                mA.flip(); // flip back in model
                mB.flip();

                AnimationManager.getInstance().playFlip(cvA, cvA::showBack, null);
                AnimationManager.getInstance().playFlip(cvB, cvB::showBack, () -> {
                    selectedCards.clear();
                    animating = false;
                    gameScene.setGridLocked(false);
                    gameScene.updateHud();
                });
            });
            pause.play();
        }
    }

    // ── Private: level complete ───────────────────────────────────────────────

    private void checkLevelComplete() {
        if (!gameState.isLevelComplete()) return;

        stopTimer();
        SoundManager.getInstance().playWin();

        PauseTransition pause = new PauseTransition(Duration.millis(800));
        pause.setOnFinished(e -> showLevelCompleteDialog());
        pause.play();
    }

    private void showLevelCompleteDialog() {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle("Level Complete!");
        dialog.setHeaderText("🎉 Level " + gameState.getLevel() + " Complete!");
        dialog.setContentText(
            "Score: " + String.format("%,d", gameState.getScore()) + "\n"
          + "Time bonus: " + gameState.getTimeRemaining() + " pts\n\n"
          + "Get ready for Level " + (gameState.getLevel() + 1) + "!"
        );

        // Use setOnHidden + show() via Platform.runLater to avoid the
        // "showAndWait is not allowed during animation or layout processing"
        // IllegalStateException that occurs when called from an animation callback.
        dialog.setOnHidden(ev -> advanceToNextLevel());
        Platform.runLater(dialog::show);
    }

    private void advanceToNextLevel() {
        gameState.advanceLevel();
        AnimationManager.getInstance().playLevelTransition(
                gameScene.getScene().getRoot(), () -> {
                    gameScene.buildCardGrid();
                    gameScene.updateHud();
                    gameScene.refreshPowerUps();
                    // Rebind events after grid rebuild
                    for (CardView cv : gameScene.getCardViews()) {
                        cv.setOnMouseClicked(e -> onCardClicked(cv));
                        cv.setHoverEnabled(true);
                    }
                    gameScene.setGridLocked(false);
                    selectedCards.clear();
                    animating = false;
                });
        startTimer();
    }

    // ── Private: game over ────────────────────────────────────────────────────

    private void handleGameOver() {
        stopTimer();
        SoundManager.getInstance().playGameOver();
        gameScene.setGridLocked(true);

        PauseTransition pause = new PauseTransition(Duration.millis(400));
        pause.setOnFinished(e -> showGameOverDialog());
        pause.play();
    }

    private void showGameOverDialog() {
        int finalScore = gameState.getScore();

        // Persist score before showing the dialog so it is saved even if the
        // user force-closes the window.

        Alert dialog = new Alert(Alert.AlertType.NONE);
        dialog.setTitle("Game Over");
        dialog.setHeaderText("⏱ Time's Up!");
        dialog.setContentText(
            "Player: " + playerName + "\n"
          + "Final Score: " + String.format("%,d", finalScore) + "\n"
          + "Level reached: " + gameState.getLevel() + "\n"
          + "Difficulty: " + gameState.getDifficulty().getDisplayName()
        );

        ButtonType retryType = new ButtonType("▶  Retry");
        ButtonType menuType  = new ButtonType("↩  Main Menu");
        dialog.getButtonTypes().setAll(retryType, menuType);

        // Use setOnHidden + show() via Platform.runLater to avoid the
        // "showAndWait is not allowed during animation or layout processing"
        // IllegalStateException.
        dialog.setOnHidden(ev -> {
            if (retryType.equals(dialog.getResult())) {
                retryGame();
            } else {
                navigateToMenu();
            }
        });
        Platform.runLater(dialog::show);
    }

    /**
     * Resets the game state to level 1 and restarts the timer so the player
     * can try again with the same difficulty and theme.
     */
    private void retryGame() {
        gameState.reset();
        gameScene.buildCardGrid();
        gameScene.updateHud();
        gameScene.refreshPowerUps();
        for (CardView cv : gameScene.getCardViews()) {
            cv.setOnMouseClicked(e -> onCardClicked(cv));
            cv.setHoverEnabled(true);
        }
        gameScene.setGridLocked(false);
        selectedCards.clear();
        animating = false;
        startTimer();
    }

    // ── Private: power-up side-effects ───────────────────────────────────────

    private void handleReveal() {
        // Show all unmatched cards face-up in the view
        for (CardView cv : gameScene.getCardViews()) {
            if (!cv.getModel().isMatched()) {
                cv.showFront();
            }
        }
        // Hide them again after RevealPowerUp.REVEAL_DURATION_MS
        PauseTransition pause = new PauseTransition(
                Duration.millis(RevealPowerUp.REVEAL_DURATION_MS));
        pause.setOnFinished(e -> {
            gameState.setRevealing(false);
            for (CardView cv : gameScene.getCardViews()) {
                if (!cv.getModel().isMatched()) {
                    cv.syncWithModel(); // restores face-down for unselected cards
                }
            }
            // Restore selected cards face-up
            for (CardView sel : selectedCards) {
                if (!sel.getModel().isMatched()) sel.showFront();
            }
        });
        pause.play();
    }

    private void handleHint() {
        gameScene.setHintHighlight(true);
        AnimationManager.getInstance().playHintPulse(
                findHintCard(0), findHintCard(1));
        PauseTransition pause = new PauseTransition(
                Duration.millis(HintPowerUp.HINT_DURATION_MS));
        pause.setOnFinished(e -> {
            gameState.clearHint();
            gameScene.setHintHighlight(false);
        });
        pause.play();
    }

    private CardView findHintCard(int index) {
        List<String> ids = gameState.getHintCardIds();
        if (ids.size() > index) {
            CardView cv = gameScene.getCardViewById(ids.get(index));
            if (cv != null) return cv;
        }
        // Fallback: return first card in grid (won't be null)
        return gameScene.getCardViews().isEmpty() ? null
                : gameScene.getCardViews().get(0);
    }

    // ── Private: timer ────────────────────────────────────────────────────────

    private void startTimer() {
        stopTimer();
        countdownTimer = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> onTimerTick()));
        countdownTimer.setCycleCount(Timeline.INDEFINITE);
        countdownTimer.play();
    }

    private void stopTimer() {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
    }

    private void onTimerTick() {
        gameState.tickTimer();
        gameScene.updateHud();
        if (gameState.isTimeUp()) {
            handleGameOver();
        }
    }

    // ── Private: combo animation ─────────────────────────────────────────────

    private void playComboAnimation(CardView near) {
        Bounds bounds = near.localToScene(near.getBoundsInLocal());
        double x = bounds.getMinX();
        double y = bounds.getMinY() - 20;
        AnimationManager.getInstance().playCombo(
                gameScene.getOverlayPane(), gameState.getCombo(), x, y);
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    private void navigateToMenu() {
        stopTimer();
        SoundManager.getInstance().stopBackgroundMusic();
        MenuController menuCtrl = new MenuController(stage);
        menuCtrl.showMenu();
    }
}
