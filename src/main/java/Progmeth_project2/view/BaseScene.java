package Progmeth_project2.view;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Abstract base class for every full-window scene in the Memory Match game.
 *
 * <p>Each concrete subclass ({@link MainMenuScene}, {@link GameScene},
 * {@link SettingsScene}) is responsible for building
 * its own layout and event bindings.  The lifecycle is:</p>
 * <ol>
 *   <li>{@link #setupLayout()} — create and arrange all UI nodes; must set
 *       {@link #scene} before returning.</li>
 *   <li>{@link #bindEvents()} — attach event handlers and property listeners to
 *       the nodes created in the previous step.</li>
 *   <li>{@link #show()} — calls the two methods above and then makes the stage
 *       visible.</li>
 * </ol>
 *
 * <p>Subclasses have protected access to {@link #stage} and {@link #scene} so
 * they can register keyboard shortcuts or query scene properties without
 * exposing them publicly.</p>
 */
public abstract class BaseScene {

    /** Window width used for all scenes. */
    protected static final double SCENE_WIDTH  = 960.0;

    /** Window height used for all scenes. */
    protected static final double SCENE_HEIGHT = 720.0;

    /** The application stage shared across all scenes. */
    protected final Stage stage;

    /** The JavaFX scene managed by this view object. */
    protected Scene scene;

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Constructs a {@code BaseScene} bound to the given stage.
     *
     * @param stage the primary application stage (non-null)
     */
    protected BaseScene(Stage stage) {
        this.stage = stage;
    }

    // ── Abstract lifecycle methods ────────────────────────────────────────────

    /**
     * Create and arrange all UI nodes for this scene, then assign
     * {@link #scene} with a newly constructed {@link Scene}.
     * Must be called before {@link #bindEvents()}.
     */
    protected abstract void setupLayout();

    /**
     * Attach event handlers, property listeners, and controller callbacks to
     * the nodes created by {@link #setupLayout()}.
     * Called immediately after {@link #setupLayout()}.
     */
    protected abstract void bindEvents();

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    /**
     * Initialises the scene ({@link #setupLayout()} then {@link #bindEvents()})
     * and displays it on the stage.
     *
     * <p>On the first call, sets a new {@link Scene} on the stage. On subsequent
     * calls, swaps only the scene root into the existing scene to preserve the
     * window's maximized/size state.</p>
     */
    public void show() {
        setupLayout();

        Scene currentScene = stage.getScene();
        boolean isFirstTimeScene = (currentScene == null);

        if (!isFirstTimeScene) {
            // Detach the new root from its temporary scene and attach it to the
            // existing scene so the OS window size is never touched.
            javafx.scene.Parent newRoot = this.scene.getRoot();
            this.scene.setRoot(new javafx.scene.layout.Pane());
            currentScene.setRoot(newRoot);
            this.scene = currentScene;
        } else {
            stage.setScene(this.scene);
        }

        bindEvents();
        applyStylesheet();

        if (isFirstTimeScene) {
            bindGlobalKeys();
        }

        stage.show();
    }

    /**
     * Binds global keyboard shortcuts available in every scene.
     * F11 toggles fullscreen mode; Escape exits fullscreen.
     * Uses {@code addEventHandler} so that subclass key handlers set via
     * {@code scene.setOnKeyPressed} are not replaced.
     */
    protected void bindGlobalKeys() {
        if (scene == null) return;
        scene.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.F11) {
                stage.setFullScreen(!stage.isFullScreen());
                e.consume();
            } else if (e.getCode() == KeyCode.ESCAPE && stage.isFullScreen()) {
                stage.setFullScreen(false);
                e.consume();
            }
        });
    }

    // ── Protected helpers ─────────────────────────────────────────────────────

    /**
     * Attempts to load and apply the project's CSS stylesheet to {@link #scene}.
     * Fails silently if the stylesheet resource is not found.
     */
    protected void applyStylesheet() {
        if (scene == null) return;
        try {
            String css = getClass()
                    .getResource("/Progmeth_project2/styles.css")
                    .toExternalForm();
            if (css != null) {
                scene.getStylesheets().add(css);
            }
        } catch (NullPointerException ignored) {
            // Resource not found — continue without stylesheet
        }
    }

    // ── Accessor ─────────────────────────────────────────────────────────────

    /**
     * Returns the underlying JavaFX {@link Scene}.
     * Will be {@code null} before {@link #setupLayout()} has been called.
     *
     * @return the JavaFX scene
     */
    public Scene getScene() {
        return scene;
    }
}
