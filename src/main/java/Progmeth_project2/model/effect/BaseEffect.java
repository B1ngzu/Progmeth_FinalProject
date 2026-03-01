package Progmeth_project2.model.effect;

import javafx.animation.Animation;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * Abstract base class for all visual effects in the Memory Match game.
 */
public abstract class BaseEffect {

    /** Total wall-clock duration of this effect. */
    protected Duration duration;

    /** Default constructor for subclass use. */
    protected BaseEffect() {}

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
