package Progmeth_project2.controller;

import Progmeth_project2.model.Difficulty;
import Progmeth_project2.model.GameState;
import Progmeth_project2.model.Theme;
import Progmeth_project2.view.*;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * Controller for the main-menu and settings scenes.
 *
 * <p>Handles navigation between the main menu, game scene, and
 * settings.  Wires up each scene's view with the appropriate controller before
 * calling {@link BaseScene#show()}.</p>
 */
public class MenuController {



    private final Stage stage;



    /**
     * Creates a {@code MenuController} bound to the given stage.
     *
     * @param stage the application stage (non-null)
     */
    public MenuController(Stage stage) {
        this.stage = stage;
    }



    /** Shows the main-menu scene. */
    public void showMenu() {
        MainMenuScene menu = new MainMenuScene(stage, this);
        menu.show();
    }

    /**
     * Called when the player clicks "Play Game".
     * Creates a {@link GameState}, constructs the {@link GameScene}, wires the
     * {@link GameController}, and transitions to the game scene.
     *
     * @param playerName the name entered by the player
     * @param difficulty the chosen difficulty level
     * @param theme      the chosen card theme
     */
    public void onPlay(String playerName, Difficulty difficulty, Theme theme) {
        String name = (playerName == null || playerName.isBlank()) ? "Player" : playerName.trim();
        GameState state = new GameState(difficulty, theme);
        GameScene gameScene = new GameScene(stage, state);
        GameController gameCtrl = new GameController(stage, state, gameScene, name);
        gameScene.setController(gameCtrl);
        gameScene.show();
        gameCtrl.start();
    }




    /** Called when the player clicks "Settings". Navigates to the settings scene. */
    public void onSettings() {
        SettingsScene settings = new SettingsScene(stage, this);
        settings.show();
    }


    /** Called when the player clicks "Back" in any scene that delegates back to the menu. */
    public void onBack() {
        showMenu();
    }


    /** Called when the player clicks "Exit". Terminates the JavaFX application. */
    public void onExit() {
        Platform.exit();
    }
}
