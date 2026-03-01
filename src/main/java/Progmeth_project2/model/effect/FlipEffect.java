package Progmeth_project2.model.effect;

import javafx.animation.Animation;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * Visual effect that simulates flipping a card over.
 */
public class FlipEffect extends BaseEffect {

    /** Duration of each half of the flip animation in milliseconds. */
    public static final int HALF_DURATION_MS = 150;

    /** Callback invoked when the card is fully "flat" (invisible) — swap faces here. */
    private final Runnable onHalfway;

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Creates a {@code FlipEffect} with the given mid-point callback.
     *
     * @param onHalfway runnable executed when the card reaches the invisible
     *                  mid-point of the animation; use this to change card content
     */
    public FlipEffect(Runnable onHalfway) {
        this.onHalfway = onHalfway;
        this.duration = Duration.millis(HALF_DURATION_MS * 2);
    }

    // ── BaseEffect contract ───────────────────────────────────────────────────

    /**
     * Returns a {@link SequentialTransition} consisting of the two scale stages.
     */
    @Override
    public Animation createAnimation(Node target) {
        // Stage 1: collapse card (X: 1 → 0)
        ScaleTransition collapse = new ScaleTransition(Duration.millis(HALF_DURATION_MS), target);
        collapse.setFromX(1.0);
        collapse.setToX(0.0);
        collapse.setOnFinished(e -> {
            if (onHalfway != null) onHalfway.run();
        });

        // Stage 2: expand card (X: 0 → 1) with the new face already shown
        ScaleTransition expand = new ScaleTransition(Duration.millis(HALF_DURATION_MS), target);
        expand.setFromX(0.0);
        expand.setToX(1.0);

        return new SequentialTransition(collapse, expand);
    }
}
