package Progmeth_project2;

import Progmeth_project2.controller.MenuController;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * JavaFX application entry point for the Memory Card Matching Game.
 *
 * <p>Extends {@link Application} as required by the JavaFX framework.
 * The actual JVM entry point is {@link Launcher#main(String[])} (which calls
 * {@code Application.launch}) to satisfy the fat-JAR module-system requirement.</p>
 *
 * <p>On startup, configures the primary stage and delegates to
 * {@link MenuController} to display the main menu.</p>
 */
public class Main extends Application {

    /**
     * No-arg constructor required by the JavaFX framework to instantiate the
     * {@link Application} subclass via reflection.
     */
    public Main() {}

    /** Application title shown in the window title bar. */
    private static final String APP_TITLE = "Memory Match — Card Game";

    /**
     * Minimum window width — prevents the stage from being resized below the
     * baseline game dimensions even if the OS tries to de-maximize it.
     */
    private static final double MIN_WIDTH  = 960.0;

    /** Minimum window height. */
    private static final double MIN_HEIGHT = 720.0;

    // ── Application lifecycle ─────────────────────────────────────────────────

    /**
     * JavaFX application start method.
     * Configures the primary stage and shows the main-menu scene.
     *
     * @param primaryStage the primary application stage provided by JavaFX
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle(APP_TITLE);
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(MIN_WIDTH);
        primaryStage.setMinHeight(MIN_HEIGHT);

        // Backstop: whenever any scene is swapped via stage.setScene(), the
        // sceneProperty fires synchronously.  Re-applying setMaximized(true)
        // here covers any code path that does not go through BaseScene.show().
        primaryStage.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null && !primaryStage.isFullScreen()) {
                primaryStage.setMaximized(true);
            }
        });

        primaryStage.setMaximized(true);

        MenuController menuController = new MenuController(primaryStage);
        menuController.showMenu();
    }

    /**
     * Called by {@link Launcher#main(String[])} to launch the JavaFX application.
     *
     * @param args command-line arguments (currently unused)
     */
    public static void main(String[] args) {
        launch(args);
    }
}

