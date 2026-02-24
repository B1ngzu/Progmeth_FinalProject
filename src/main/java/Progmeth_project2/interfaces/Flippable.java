package Progmeth_project2.interfaces;

/**
 * Implemented by any game element that can be flipped between two visible states
 * (e.g., a card with a face-up and a face-down side).
 */
public interface Flippable {

    /**
     * Toggle the element between its two states (face-up / face-down).
     */
    void flip();

    /**
     * Returns whether the element is currently showing its face-up side.
     *
     * @return {@code true} if face-up
     */
    boolean isFaceUp();
}
