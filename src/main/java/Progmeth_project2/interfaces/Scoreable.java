package Progmeth_project2.interfaces;

/**
 * Implemented by any game element that contributes a numeric score value.
 * Allows polymorphic score queries via {@code List<Scoreable>}.
 */
public interface Scoreable {

    /**
     * Returns the score value associated with this element.
     *
     * @return non-negative score value
     */
    int getScore();
}
