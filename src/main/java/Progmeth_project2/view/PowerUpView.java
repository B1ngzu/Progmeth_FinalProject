package Progmeth_project2.view;

import Progmeth_project2.model.powerup.BasePowerUp;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * A styled {@link Button} that represents a single {@link BasePowerUp} in the
 * game UI.
 *
 * <p>Extends {@link Button} to benefit from all standard button event handling
 * while adding game-specific functionality: it automatically disables itself
 * when the backing power-up is consumed ({@link #refresh()}), and applies
 * distinct colour schemes per power-up type.</p>
 */
public class PowerUpView extends Button {

    // ── Constants ────────────────────────────────────────────────────────────

    private static final double BUTTON_WIDTH  = 110.0;
    private static final double BUTTON_HEIGHT =  70.0;

    // ── State ────────────────────────────────────────────────────────────────

    private final BasePowerUp powerUp;
    private final String activeStyle;
    private final String disabledStyle;

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Creates a power-up button bound to the given {@link BasePowerUp} model.
     *
     * @param powerUp the power-up this button activates (non-null)
     */
    public PowerUpView(BasePowerUp powerUp) {
        this.powerUp = powerUp;
        this.activeStyle   = buildActiveStyle(powerUp.getName());
        this.disabledStyle = buildDisabledStyle();

        setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        setMinSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        setMaxSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        setAlignment(Pos.CENTER);
        setWrapText(true);

        // Tooltip
        Tooltip tip = new Tooltip(powerUp.getDescription());
        setTooltip(tip);

        refresh();
    }

    // ── State refresh ─────────────────────────────────────────────────────────

    /**
     * Synchronises this button's visual state with the backing power-up model.
     * Should be called after each power-up use and at level start.
     */
    public void refresh() {
        boolean avail = powerUp.isAvailable();
        setDisable(!avail);
        setStyle(avail ? activeStyle : disabledStyle);

        if (avail) {
            setText(powerUp.getIconText() + "\n" + powerUp.getName());
        } else {
            setText("✗\n" + powerUp.getName());
        }
    }

    // ── Accessor ─────────────────────────────────────────────────────────────

    /**
     * Returns the backing power-up model.
     *
     * @return the {@link BasePowerUp} this button controls
     */
    public BasePowerUp getPowerUp() { return powerUp; }

    // ── Private style helpers ─────────────────────────────────────────────────

    private String buildActiveStyle(String name) {
        String gradient = switch (name) {
            case "Reveal" -> "linear-gradient(to bottom, #F39C12, #D68910)";
            case "Freeze" -> "linear-gradient(to bottom, #2980B9, #1A6FA8)";
            case "Hint"   -> "linear-gradient(to bottom, #E74C3C, #C0392B)";
            default       -> "linear-gradient(to bottom, #7D3C98, #6C3483)";
        };
        return "-fx-background-color: " + gradient + ";"
             + "-fx-text-fill: white;"
             + "-fx-font-size: 13;"
             + "-fx-font-weight: bold;"
             + "-fx-background-radius: 10;"
             + "-fx-border-color: rgba(255,255,255,0.4);"
             + "-fx-border-width: 1.5;"
             + "-fx-border-radius: 10;"
             + "-fx-cursor: hand;"
             + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 6, 0, 0, 2);";
    }

    private String buildDisabledStyle() {
        return "-fx-background-color: linear-gradient(to bottom, #4A4A5A, #3A3A4A);"
             + "-fx-text-fill: rgba(255,255,255,0.35);"
             + "-fx-font-size: 13;"
             + "-fx-font-weight: bold;"
             + "-fx-background-radius: 10;"
             + "-fx-border-color: rgba(255,255,255,0.15);"
             + "-fx-border-width: 1.5;"
             + "-fx-border-radius: 10;"
             + "-fx-cursor: default;";
    }
}
