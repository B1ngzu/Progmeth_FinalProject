package Progmeth_project2.model.effect;

import javafx.animation.Animation;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * Visual effect that simulates flipping a card over.
 *
 * <p>The animation comprises two {@link ScaleTransition} stages applied to the
 * card's X axis:</p>
 * <ol>
 *   <li>Scale from 1.0 → 0.0 over {@value #HALF_DURATION_MS} ms (card face
 *       appears to fold away).</li>
 *   <li>A mid-point callback ({@link #onHalfway}) swaps the visible face.</li>
 *   <li>Scale from 0.0 → 1.0 over {@value #HALF_DURATION_MS} ms (new face
 *       appears to unfold).</li>
 * </ol>
 *
 * <p>This technique avoids the need for a full 3-D scene/camera setup while
 * still producing a convincing coin-flip illusion.</p>
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
     * {@inheritDoc}
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
