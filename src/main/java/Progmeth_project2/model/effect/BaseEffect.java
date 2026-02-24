package Progmeth_project2.model.effect;

import javafx.animation.Animation;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * Abstract base class for all visual effects in the Memory Match game.
 *
 * <p>Subclasses ({@link FlipEffect}, {@link MatchEffect}, {@link ComboEffect})
 * encapsulate the construction of a JavaFX {@link Animation} for a given target
 * node, keeping animation-creation logic separate from the view layer and
 * enabling polymorphic effect dispatch through {@code List<BaseEffect>}.</p>
 *
 * <p>Each subclass implements {@link #createAnimation(Node)} to return the
 * specific transition or timeline for that effect.  Callers use
 * {@link #play(Node)} as a convenience wrapper.</p>
 */
public abstract class BaseEffect {

    /** Total wall-clock duration of this effect. */
    protected Duration duration;

    // ── Abstract contract ────────────────────────────────────────────────────

    /**
     * Constructs and returns the JavaFX {@link Animation} that implements this
     * effect on the given target node.
     *
     * @param target the JavaFX node to animate (may be {@code null} for effects
     *               that create their own nodes, e.g., {@link ComboEffect})
     * @return a ready-to-play animation (not yet started)
     */
    public abstract Animation createAnimation(Node target);

    // ── Convenience ──────────────────────────────────────────────────────────

    /**
     * Convenience method that calls {@link #createAnimation(Node)} and
     * immediately starts the returned animation.
     *
     * @param target the JavaFX node to animate
     */
    public void play(Node target) {
        Animation anim = createAnimation(target);
        if (anim != null) {
            anim.play();
        }
    }

    // ── Accessor ─────────────────────────────────────────────────────────────

    /**
     * Returns the total wall-clock duration of this effect.
     *
     * @return effect duration
     */
    public Duration getDuration() {
        return duration;
    }
}
