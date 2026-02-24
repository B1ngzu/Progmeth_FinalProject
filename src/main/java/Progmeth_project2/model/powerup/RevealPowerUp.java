package Progmeth_project2.model.powerup;

import Progmeth_project2.model.GameState;

/**
 * Power-up that briefly reveals all unmatched card faces.
 *
 * <p>When activated, every face-down, unmatched card is set to face-up for
 * {@value #REVEAL_DURATION_MS} milliseconds. The controller is responsible for
 * scheduling the re-hide after this duration using the game state's reveal
 * flag.</p>
 */
public class RevealPowerUp extends BasePowerUp {

    /** Duration (ms) that all cards are kept face-up after activation. */
    public static final int REVEAL_DURATION_MS = 2000;

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Creates a new Reveal power-up.
     */
    public RevealPowerUp() {
        super("Reveal", "Shows all cards for 2 seconds", "👁");
    }

    // ── BasePowerUp contract ─────────────────────────────────────────────────

    /**
     * {@inheritDoc}
     * Sets the game-state reveal flag. The controller observes this flag,
     * flips all cards in the view face-up, then schedules a callback after
     * {@value #REVEAL_DURATION_MS} ms to flip them back.
     */
    @Override
    public void activate(GameState gameState) {
        gameState.setRevealing(true);
    }
}
