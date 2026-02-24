package Progmeth_project2.view;

import Progmeth_project2.model.card.BaseCard;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/**
 * Visual representation of a single {@link BaseCard} in the card grid.
 *
 * <p>Extends {@link StackPane} to layer a face-down back and a face-up front
 * inside a single node.  The controller calls {@link #showFront()} /
 * {@link #showBack()} at the midpoint of a {@link Progmeth_project2.model.effect.FlipEffect}
 * to swap the displayed face without requiring two separate nodes.</p>
 *
 * <p>The highlight state ({@link #setHighlighted(boolean)}) is used by the Hint
 * power-up to outline a suggested matching pair.</p>
 */
public class CardView extends StackPane {

    // ── Constants ────────────────────────────────────────────────────────────

    /** Preferred width of a card tile in pixels. */
    public static final double CARD_WIDTH  = 90.0;

    /** Preferred height of a card tile in pixels. */
    public static final double CARD_HEIGHT = 110.0;

    // ── State ────────────────────────────────────────────────────────────────

    private final BaseCard model;
    private boolean showingFront;
    private boolean highlighted;

    // ── Nodes ────────────────────────────────────────────────────────────────

    private final Label symbolLabel;
    private final Label backLabel;

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Creates a {@code CardView} backed by the given model card.
     * Initially displays the card back.
     *
     * @param model the model card this view represents (non-null)
     */
    public CardView(BaseCard model) {
        this.model = model;
        this.showingFront = false;
        this.highlighted = false;

        setPrefSize(CARD_WIDTH, CARD_HEIGHT);
        setMinSize(CARD_WIDTH, CARD_HEIGHT);
        setMaxSize(CARD_WIDTH, CARD_HEIGHT);
        setAlignment(Pos.CENTER);

        // Back face label
        backLabel = new Label("?");
        backLabel.setStyle(buildBackLabelStyle());

        // Front face label (symbol)
        symbolLabel = new Label(model.getDisplaySymbol());
        symbolLabel.setStyle(buildSymbolLabelStyle());
        symbolLabel.setVisible(false);

        getChildren().addAll(backLabel, symbolLabel);
        applyBaseStyle(false);
    }

    // ── Face control ──────────────────────────────────────────────────────────

    /**
     * Switches the view to show the card's symbol face (face-up).
     * Called at the invisible midpoint of the flip animation.
     */
    public void showFront() {
        showingFront = true;
        backLabel.setVisible(false);
        symbolLabel.setVisible(true);
        applyBaseStyle(true);
    }

    /**
     * Switches the view to show the card back face (face-down).
     * Called at the invisible midpoint of the flip animation.
     */
    public void showBack() {
        showingFront = false;
        backLabel.setVisible(true);
        symbolLabel.setVisible(false);
        applyBaseStyle(false);
    }

    /**
     * Updates the view to reflect the current state of the model card
     * without any animation.  Useful after a Reveal power-up ends.
     */
    public void syncWithModel() {
        if (model.isFaceUp() || model.isMatched()) {
            showFront();
        } else {
            showBack();
        }
        if (model.isMatched()) {
            applyMatchedStyle();
        }
    }

    /**
     * Applies the "matched" visual style (green background) to lock the card
     * in its face-up state.
     */
    public void applyMatchedStyle() {
        setStyle(
            "-fx-background-color: linear-gradient(to bottom, #27AE60, #1E8449);"
          + "-fx-background-radius: 12;"
          + "-fx-border-color: #52BE80;"
          + "-fx-border-width: 2.5;"
          + "-fx-border-radius: 12;"
          + "-fx-effect: dropshadow(gaussian, rgba(46,204,113,0.6), 12, 0.4, 0, 0);"
        );
    }

    // ── Highlight / hint ─────────────────────────────────────────────────────

    /**
     * Sets the highlighted (hint) visual state of this card.
     *
     * @param highlighted {@code true} to show the hint outline
     */
    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
        if (highlighted) {
            setStyle(getStyle()
                + "-fx-border-color: #FFD700;"
                + "-fx-border-width: 3.5;"
                + "-fx-effect: dropshadow(gaussian, #FFD700, 18, 0.6, 0, 0);");
        } else {
            syncWithModel();
        }
    }

    // ── Hover effects ─────────────────────────────────────────────────────────

    /**
     * Enables or disables the hover-lift effect on this card.
     * Should be disabled once the card is matched or the game is animating.
     *
     * @param enabled {@code true} to enable hover interactivity
     */
    public void setHoverEnabled(boolean enabled) {
        if (enabled) {
            setOnMouseEntered(e -> {
                if (!model.isMatched() && !showingFront) {
                    setTranslateY(-4);
                }
            });
            setOnMouseExited(e -> setTranslateY(0));
        } else {
            setOnMouseEntered(null);
            setOnMouseExited(null);
            setTranslateY(0);
        }
    }

    // ── Accessors ─────────────────────────────────────────────────────────────

    /**
     * Returns the backing model card.
     *
     * @return the {@link BaseCard} this view represents
     */
    public BaseCard getModel() { return model; }

    /**
     * Returns whether this view is currently showing the front face.
     *
     * @return {@code true} if face-up
     */
    public boolean isShowingFront() { return showingFront; }

    /**
     * Returns whether the hint highlight is currently active.
     *
     * @return highlighted flag
     */
    public boolean isHighlighted() { return highlighted; }

    // ── Private style helpers ─────────────────────────────────────────────────

    private void applyBaseStyle(boolean faceUp) {
        if (faceUp) {
            setStyle(
                "-fx-background-color: linear-gradient(to bottom, #EAF4FB, #FFFFFF);"
              + "-fx-background-radius: 12;"
              + "-fx-border-color: #5DADE2;"
              + "-fx-border-width: 2;"
              + "-fx-border-radius: 12;"
              + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 8, 0, 0, 3);"
              + "-fx-cursor: default;"
            );
        } else {
            setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #1A3A6B, #2E6DB4);"
              + "-fx-background-radius: 12;"
              + "-fx-border-color: #4A90D9;"
              + "-fx-border-width: 2;"
              + "-fx-border-radius: 12;"
              + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 3);"
              + "-fx-cursor: hand;"
            );
        }
    }

    private String buildBackLabelStyle() {
        return "-fx-font-size: 32;"
             + "-fx-font-weight: bold;"
             + "-fx-text-fill: rgba(255,255,255,0.6);";
    }

    private String buildSymbolLabelStyle() {
        return "-fx-font-size: 34;"
             + "-fx-text-fill: #1A1A2E;";
    }
}
