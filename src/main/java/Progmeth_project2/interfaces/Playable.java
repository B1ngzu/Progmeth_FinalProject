package Progmeth_project2.interfaces;

/**
 * Implemented by any game element that produces an audio output when
 * activated (e.g., a power-up playing its activation sound).
 */
public interface Playable {

    /**
     * Play the audio associated with this element.
     * Implementations must not block the calling thread; audio should
     * be dispatched to a daemon thread internally.
     */
    void playSound();
}
