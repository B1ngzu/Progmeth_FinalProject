package Progmeth_project2.model.effect;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/**
 * Visual effect that displays a floating combo-multiplier text label.
 *
 * <p>When played, a "COMBO ×N" label is added to the supplied parent pane and
 * animated upward while fading out, then removed automatically on completion.</p>
 *
 * <p>Because this effect creates its own {@link Label} node, the {@code target}
 * argument of {@link #createAnimation(Node)} is ignored; callers should use
 * {@link #play(Node)} with {@code null} or call {@link #playOnPane()} directly.</p>
 */
public class ComboEffect extends BaseEffect {

    /** Duration of the floating text animation in milliseconds. */
    public static final int FLOAT_DURATION_MS = 1200;

    /** Vertical distance (pixels) the label travels upward during the effect. */
    private static final double FLOAT_DISTANCE = 90.0;

    private final Pane parent;
    private final int comboLevel;
    private final double startX;
    private final double startY;

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Creates a combo text effect.
     *
     * @param parent     the pane to which the label will be temporarily added
     * @param comboLevel the current combo multiplier value to display
     * @param startX     horizontal position of the label within {@code parent}
     * @param startY     vertical position of the label within {@code parent}
     */
    public ComboEffect(Pane parent, int comboLevel, double startX, double startY) {
        this.parent = parent;
        this.comboLevel = comboLevel;
        this.startX = startX;
        this.startY = startY;
        this.duration = Duration.millis(FLOAT_DURATION_MS);
    }

    // ── BaseEffect contract ───────────────────────────────────────────────────

    /**
     * {@inheritDoc}
     * Creates and adds a styled label to the parent pane, then returns a
     * {@link ParallelTransition} that moves it upward while fading it out.
     * The label is removed from the pane when the animation finishes.
     *
     * @param target ignored — this effect manages its own node
     * @return the parallel animation (translate + fade)
     */
    @Override
    public Animation createAnimation(Node target) {
        Label label = new Label("COMBO ×" + comboLevel + "!");
        label.setStyle(buildLabelStyle());
        label.setLayoutX(startX);
        label.setLayoutY(startY);
        label.setMouseTransparent(true);
        parent.getChildren().add(label);

        TranslateTransition move = new TranslateTransition(duration, label);
        move.setByY(-FLOAT_DISTANCE);

        FadeTransition fade = new FadeTransition(duration, label);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);

        ParallelTransition parallel = new ParallelTransition(move, fade);
        parallel.setOnFinished(e -> parent.getChildren().remove(label));
        return parallel;
    }

    /**
     * Convenience method: calls {@link #createAnimation(Node)} with {@code null}
     * and immediately plays the result.
     */
    public void playOnPane() {
        play(null);
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private String buildLabelStyle() {
        // Colour shifts from yellow at combo 2 toward orange/red at high combos
        String color = comboLevel >= 5 ? "#FF4444"
                     : comboLevel >= 3 ? "#FF8C00"
                     : "#FFD700";
        return "-fx-font-size: " + (24 + Math.min(comboLevel * 2, 16)) + ";"
             + "-fx-font-weight: bold;"
             + "-fx-text-fill: " + color + ";"
             + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 6, 0, 0, 2);";
    }
}
