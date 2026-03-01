package Progmeth_project2.util;

import Progmeth_project2.model.effect.*;
import Progmeth_project2.view.CardView;
import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/**
 * Factory and helper for creating and playing game animations.
 *
 * <p>The controller layer calls methods on this class instead of constructing
 * animation objects directly, keeping animation logic in one place and making
 * it easy to swap implementations without touching controller code.</p>
 */
public final class AnimationManager {

    // ── Singleton ─────────────────────────────────────────────────────────────

    private static final AnimationManager INSTANCE = new AnimationManager();

    /**
     * Returns the application-wide {@code AnimationManager} singleton.
     *
     * @return singleton instance
     */
    public static AnimationManager getInstance() { return INSTANCE; }

    private AnimationManager() {}

    // ── Card flip ─────────────────────────────────────────────────────────────

    /**
     * Plays a flip animation on the given {@link CardView}.
     *
     * <p>Uses {@link FlipEffect}: the card collapses on its X axis, the
     * {@code onHalfway} callback is invoked (allowing the caller to swap the
     * displayed face), then the card expands back to full size.</p>
     *
     * @param cardView  the card view to animate
     * @param onHalfway callback invoked at the invisible midpoint of the flip
     * @param onFinish  callback invoked when the full animation completes
     */
    public void playFlip(CardView cardView, Runnable onHalfway, Runnable onFinish) {
        FlipEffect effect = new FlipEffect(onHalfway);
        Animation anim = effect.createAnimation(cardView);
        if (onFinish != null) {
            anim.setOnFinished(e -> onFinish.run());
        }
        anim.play();
    }

    // ── Match glow ────────────────────────────────────────────────────────────

    /**
     * Plays the match-glow animation on both cards of a matched pair.
     *
     * @param cardA    first card view
     * @param cardB    second card view
     * @param onFinish callback invoked after the animation completes
     */
    public void playMatch(CardView cardA, CardView cardB, Runnable onFinish) {
        MatchEffect effect = new MatchEffect();
        Animation animA = effect.createAnimation(cardA);
        Animation animB = effect.createAnimation(cardB);

        ParallelTransition both = new ParallelTransition(animA, animB);
        if (onFinish != null) {
            both.setOnFinished(e -> onFinish.run());
        }
        both.play();
    }

    // ── Mismatch shake ────────────────────────────────────────────────────────

    /**
     * Plays a brief horizontal shake on both cards of a mismatched pair.
     *
     * @param cardA first card view
     * @param cardB second card view
     */
    public void playMismatch(CardView cardA, CardView cardB) {
        Animation shakeA = buildShake(cardA);
        Animation shakeB = buildShake(cardB);
        new ParallelTransition(shakeA, shakeB).play();
    }

    // ── Combo text ────────────────────────────────────────────────────────────

    /**
     * Plays a floating "COMBO ×N!" text animation at the given scene coordinates.
     *
     * @param parent     pane that will temporarily host the label
     * @param comboLevel the current combo multiplier to display
     * @param x          horizontal position within {@code parent}
     * @param y          vertical position within {@code parent}
     */
    public void playCombo(Pane parent, int comboLevel, double x, double y) {
        ComboEffect effect = new ComboEffect(parent, comboLevel, x, y);
        effect.playOnPane();
    }

    // ── Power-up flash ────────────────────────────────────────────────────────

    /**
     * Flashes a node with a brief scale-up/down pulse to signal power-up
     * activation.
     *
     * @param node the node to pulse
     */
    public void playPowerUpFlash(Node node) {
        ScaleTransition st = new ScaleTransition(Duration.millis(300), node);
        st.setFromX(1.0); st.setToX(1.3);
        st.setFromY(1.0); st.setToY(1.3);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.play();
    }

    // ── Level transition ──────────────────────────────────────────────────────

    /**
     * Fades a node out, runs the {@code action} callback (e.g., rebuild grid),
     * then fades the node back in.
     *
     * @param node   the node to fade
     * @param action callback invoked while the node is invisible
     */
    public void playLevelTransition(Node node, Runnable action) {
        FadeTransition out = new FadeTransition(Duration.millis(400), node);
        out.setFromValue(1.0);
        out.setToValue(0.0);
        out.setOnFinished(e -> {
            if (action != null) action.run();
            FadeTransition in = new FadeTransition(Duration.millis(400), node);
            in.setFromValue(0.0);
            in.setToValue(1.0);
            in.play();
        });
        out.play();
    }

    // ── Hint pulse ────────────────────────────────────────────────────────────

    /**
     * Pulses the highlight on a hinted card pair.
     *
     * @param cardA first card view
     * @param cardB second card view
     */
    public void playHintPulse(CardView cardA, CardView cardB) {
        pulseNode(cardA);
        pulseNode(cardB);
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private Animation buildShake(Node node) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(60), node);
        tt.setByX(8);
        tt.setAutoReverse(true);
        tt.setCycleCount(6);
        // Reset to original position after shake
        tt.setOnFinished(e -> node.setTranslateX(0));
        return tt;
    }

    private void pulseNode(Node node) {
        ScaleTransition st = new ScaleTransition(Duration.millis(400), node);
        st.setFromX(1.0); st.setToX(1.15);
        st.setFromY(1.0); st.setToY(1.15);
        st.setAutoReverse(true);
        st.setCycleCount(4);
        st.play();
    }
}
