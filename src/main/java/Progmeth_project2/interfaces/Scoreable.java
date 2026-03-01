package Progmeth_project2.interfaces;

/**
 * Implemented by any game element that contributes a numeric score value.
 */
public interface Scoreable {

    /**
     * Returns the score value associated with this element.
     *
     * @return score value (non-negative)
     */
    int getScore();
}
