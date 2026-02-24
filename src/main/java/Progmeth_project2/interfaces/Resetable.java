package Progmeth_project2.interfaces;

/**
 * Implemented by any game element that can be reset to its initial / default state.
 * Used to restart cards, power-ups, and the overall game state without allocating
 * new objects.
 */
public interface Resetable {

    /**
     * Restore this element to its initial state.
     */
    void reset();
}
