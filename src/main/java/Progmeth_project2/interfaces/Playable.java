package Progmeth_project2.interfaces;

/**
 * Implemented by any game element that produces an audio output when
 * activated (e.g., a power-up playing its activation sound).
 */
public interface Playable {

    /** Plays the audio cue associated with this element's activation. */
    void playSound();
}
