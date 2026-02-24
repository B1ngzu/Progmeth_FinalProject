package Progmeth_project2.model.powerup;

import Progmeth_project2.model.GameState;

/**
 * Power-up that temporarily pauses the countdown timer.
 *
 * <p>When activated, the game state's timer-frozen flag is set for
 * {@value #FREEZE_DURATION_SECONDS} seconds. The controller checks this flag
 * each timer tick and skips decrementing the counter while it is active.</p>
 */
public class FreezePowerUp extends BasePowerUp {

    /** Number of seconds the countdown timer is paused after activation. */
    public static final int FREEZE_DURATION_SECONDS = 10;

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Creates a new Freeze Timer power-up.
     */
    public FreezePowerUp() {
        super("Freeze", "Pauses the timer for 10 seconds", "❄");
    }

    // ── BasePowerUp contract ─────────────────────────────────────────────────

    /**
     * {@inheritDoc}
     * Instructs the game state to freeze the countdown for
     * {@value #FREEZE_DURATION_SECONDS} seconds.
     */
    @Override
    public void activate(GameState gameState) {
        gameState.freezeTimer(FREEZE_DURATION_SECONDS);
    }
}
