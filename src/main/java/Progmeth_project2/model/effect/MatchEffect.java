package Progmeth_project2.model.effect;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.util.Duration;

/**
 * Visual effect played when two cards form a matching pair.
 *
 * <p>The animation runs a {@link ScaleTransition} (card scales up then back
 * down) in parallel with a {@link Timeline} that brightens the card using a
 * {@link ColorAdjust} effect and then returns to normal, producing a satisfying
 * "glow-pulse" result.</p>
 */
public class MatchEffect extends BaseEffect {

    /** Total duration of the match animation in milliseconds. */
    public static final int TOTAL_DURATION_MS = 600;

    /** Peak brightness applied at the midpoint of the animation (0.0–1.0). */
    private static final double PEAK_BRIGHTNESS = 0.6;

    /** Peak scale factor applied to the card at the animation midpoint. */
    private static final double PEAK_SCALE = 1.2;

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Creates a new {@code MatchEffect}.
     */
    public MatchEffect() {
        this.duration = Duration.millis(TOTAL_DURATION_MS);
    }

    // ── BaseEffect contract ───────────────────────────────────────────────────

    /**
     * {@inheritDoc}
     * Returns a {@link ParallelTransition} combining a scale pulse and a
     * brightness glow on the target node.
     */
    @Override
    public Animation createAnimation(Node target) {
        Duration half = Duration.millis(TOTAL_DURATION_MS / 2.0);

        // Scale pulse: 1.0 → PEAK_SCALE → 1.0
        ScaleTransition scale = new ScaleTransition(duration, target);
        scale.setFromX(1.0);
        scale.setToX(PEAK_SCALE);
        scale.setFromY(1.0);
        scale.setToY(PEAK_SCALE);
        scale.setAutoReverse(true);
        scale.setCycleCount(2);
        scale.setDuration(half);

        // Brightness glow
        ColorAdjust adjust = new ColorAdjust();
        target.setEffect(adjust);

        Timeline glow = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(adjust.brightnessProperty(), 0.0)),
                new KeyFrame(half,
                        new KeyValue(adjust.brightnessProperty(), PEAK_BRIGHTNESS)),
                new KeyFrame(duration,
                        new KeyValue(adjust.brightnessProperty(), 0.0))
        );

        ParallelTransition parallel = new ParallelTransition(scale, glow);
        // Remove the ColorAdjust effect when done so it doesn't persist
        parallel.setOnFinished(e -> target.setEffect(null));
        return parallel;
    }
}
