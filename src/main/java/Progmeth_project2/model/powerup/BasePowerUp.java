package Progmeth_project2.model.powerup;

import Progmeth_project2.interfaces.Playable;
import Progmeth_project2.interfaces.Resetable;
import Progmeth_project2.model.GameState;
import Progmeth_project2.util.SoundManager;

/**
 * Abstract base class for all power-ups in the Memory Match game.
 *
 * <p>Implements {@link Resetable} (power-ups are recharged at the start of each
 * level) and {@link Playable} (each power-up plays a sound on activation).</p>
 *
 * <p>Concrete subclasses ({@link RevealPowerUp}, {@link FreezePowerUp},
 * {@link HintPowerUp}) must implement {@link #activate(GameState)} to define
 * the effect applied to the game state.</p>
 */
public abstract class BasePowerUp implements Resetable, Playable {

    // ── Fields ──────────────────────────────────────────────────────────────

    private final String name;
    private final String description;
    private final String iconText;

    /** Whether this power-up is currently available for use. */
    protected boolean available;

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Constructs a power-up with the given metadata.
     *
     * @param name        display name shown on the button
     * @param description tooltip/description of the effect
     * @param iconText    emoji or short text used as the button icon
     */
    protected BasePowerUp(String name, String description, String iconText) {
        this.name = name;
        this.description = description;
        this.iconText = iconText;
        this.available = true;
    }

    // ── Abstract contract ────────────────────────────────────────────────────

    /**
     * Apply this power-up's effect to the supplied game state.
     * Called by the controller after the user clicks the power-up button.
     *
     * @param gameState the current game state to modify (non-null)
     */
    public abstract void activate(GameState gameState);

    // ── Playable ─────────────────────────────────────────────────────────────

    /**
     * {@inheritDoc}
     * Plays the shared power-up activation sound. Subclasses may override to
     * play a more specific sound.
     */
    @Override
    public void playSound() {
        SoundManager.getInstance().playPowerUp();
    }

    // ── Resetable ────────────────────────────────────────────────────────────

    /**
     * {@inheritDoc}
     * Marks this power-up as available again (called at the start of each level).
     */
    @Override
    public void reset() {
        available = true;
    }

    // ── Convenience helpers ──────────────────────────────────────────────────

    /**
     * Uses the power-up: activates the effect and marks it as consumed.
     * Has no effect if the power-up is already consumed.
     *
     * @param gameState current game state
     */
    public void use(GameState gameState) {
        if (!available) return;
        available = false;
        playSound();
        activate(gameState);
    }

    // ── Accessors ────────────────────────────────────────────────────────────

    /**
     * Returns the display name of this power-up.
     *
     * @return name string
     */
    public String getName() { return name; }

    /**
     * Returns the descriptive tooltip text.
     *
     * @return description string
     */
    public String getDescription() { return description; }

    /**
     * Returns the icon text (emoji or short label) for the UI button.
     *
     * @return icon string
     */
    public String getIconText() { return iconText; }

    /**
     * Returns whether this power-up is currently available.
     *
     * @return {@code true} if not yet consumed this level
     */
    public boolean isAvailable() { return available; }
}
