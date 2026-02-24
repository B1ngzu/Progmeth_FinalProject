package Progmeth_project2.util;

import javax.sound.sampled.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Singleton that synthesises and plays all in-game sounds programmatically
 * using {@code javax.sound.sampled}.  No audio files are required.
 *
 * <p>All sound playback is dispatched to a shared daemon-thread executor so
 * that the JavaFX Application Thread is never blocked.</p>
 *
 * <p>Sound generation uses a simple additive-synthesis approach: a base sine
 * wave with an amplitude envelope is written to an in-memory byte buffer and
 * played via a {@link Clip}.</p>
 */
public class SoundManager {

    // ── Singleton ─────────────────────────────────────────────────────────────

    private static SoundManager instance;

    /** Returns the application-wide {@code SoundManager} singleton. */
    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    // ── Fields ──────────────────────────────────────────────────────────────

    private static final int SAMPLE_RATE = 44100;
    private final ExecutorService executor =
            Executors.newCachedThreadPool(r -> {
                Thread t = new Thread(r, "SoundManager");
                t.setDaemon(true);
                return t;
            });

    private boolean muted = false;

    /** Master SFX volume in [0.0, 1.0]. Default 0.8. */
    private float sfxVolume = 0.8f;

    // ── Constructor ──────────────────────────────────────────────────────────

    private SoundManager() {}

    // ── Public sound API ─────────────────────────────────────────────────────

    /**
     * Plays a short "tick" sound for a card flip action.
     */
    public void playCardFlip() {
        if (muted) return;
        executor.submit(() -> playTone(880, 70, 0.35f, Waveform.SQUARE));
    }

    /**
     * Plays a two-tone ascending chime for a successful card match.
     */
    public void playMatch() {
        if (muted) return;
        executor.submit(() -> {
            playTone(523, 180, 0.4f, Waveform.SINE);
            sleep(160);
            playTone(659, 220, 0.4f, Waveform.SINE);
        });
    }

    /**
     * Plays a two-tone descending buzz for a failed card match.
     */
    public void playMismatch() {
        if (muted) return;
        executor.submit(() -> {
            playTone(330, 200, 0.4f, Waveform.SAWTOOTH);
            sleep(180);
            playTone(220, 250, 0.4f, Waveform.SAWTOOTH);
        });
    }

    /**
     * Plays an ascending multi-tone arpeggio for a combo multiplier increase.
     */
    public void playCombo() {
        if (muted) return;
        executor.submit(() -> {
            int[] freqs = {523, 659, 784, 1047};
            for (int f : freqs) {
                playTone(f, 130, 0.45f, Waveform.SINE);
                sleep(110);
            }
        });
    }

    /**
     * Plays a distinctive tone sequence when a power-up is activated.
     */
    public void playPowerUp() {
        if (muted) return;
        executor.submit(() -> {
            playTone(440, 120, 0.5f, Waveform.SINE);
            sleep(100);
            playTone(550, 120, 0.5f, Waveform.SINE);
            sleep(100);
            playTone(660, 200, 0.5f, Waveform.SINE);
        });
    }

    /**
     * Plays a victory fanfare when the player wins a level.
     */
    public void playWin() {
        if (muted) return;
        executor.submit(() -> {
            int[] freqs = {523, 659, 784, 1047, 1047};
            int[] durs  = {180, 180, 180, 180,  360};
            for (int i = 0; i < freqs.length; i++) {
                playTone(freqs[i], durs[i], 0.55f, Waveform.SINE);
                sleep(durs[i] - 20);
            }
        });
    }

    /**
     * Plays a short descending tone when the timer runs out (game over).
     */
    public void playGameOver() {
        if (muted) return;
        executor.submit(() -> {
            int[] freqs = {440, 330, 220, 147};
            for (int f : freqs) {
                playTone(f, 200, 0.5f, Waveform.SAWTOOTH);
                sleep(180);
            }
        });
    }

    /**
     * Toggles the muted state.
     *
     * @param muted {@code true} to silence all sounds
     */
    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    /**
     * Returns whether sound is currently muted.
     *
     * @return muted flag
     */
    public boolean isMuted() {
        return muted;
    }

    /**
     * Sets the master SFX volume.  Changes take effect on the next sound played.
     *
     * @param volume value in [0.0, 1.0]; clamped automatically
     */
    public void setSfxVolume(float volume) {
        this.sfxVolume = Math.max(0.0f, Math.min(1.0f, volume));
    }

    /**
     * Returns the current SFX volume level.
     *
     * @return value in [0.0, 1.0]
     */
    public float getSfxVolume() {
        return sfxVolume;
    }

    // ── Core synthesis ────────────────────────────────────────────────────────

    /**
     * Waveform shapes available for tone synthesis.
     */
    public enum Waveform { SINE, SQUARE, SAWTOOTH }

    /**
     * Synthesises and immediately plays a single tone.
     *
     * @param frequency  fundamental frequency in Hz
     * @param durationMs playback duration in milliseconds
     * @param volume     amplitude in [0.0, 1.0]
     * @param waveform   waveform shape
     */
    private void playTone(double frequency, int durationMs, float volume, Waveform waveform) {
        try {
            byte[] data = synthesise(frequency, durationMs, volume * sfxVolume, waveform);
            AudioFormat format = new AudioFormat(SAMPLE_RATE, 8, 1, true, false);
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            if (!AudioSystem.isLineSupported(info)) return;

            Clip clip = (Clip) AudioSystem.getLine(info);
            clip.open(format, data, 0, data.length);
            clip.addLineListener(ev -> {
                if (ev.getType() == LineEvent.Type.STOP) {
                    clip.close();
                }
            });
            clip.start();
        } catch (Exception ignored) {
            // Silently degrade if audio system is unavailable
        }
    }

    /**
     * Generates a byte array of PCM audio samples for the given tone parameters.
     *
     * @param frequency  fundamental frequency in Hz
     * @param durationMs duration in milliseconds
     * @param volume     peak amplitude [0.0, 1.0]
     * @param waveform   synthesis waveform
     * @return signed 8-bit mono PCM data at {@value #SAMPLE_RATE} Hz
     */
    private byte[] synthesise(double frequency, int durationMs, float volume, Waveform waveform) {
        int totalSamples = SAMPLE_RATE * durationMs / 1000;
        byte[] data = new byte[totalSamples];

        double attackSamples  = SAMPLE_RATE * 0.005; // 5 ms attack
        double releaseSamples = SAMPLE_RATE * 0.05;  // 50 ms release

        for (int i = 0; i < totalSamples; i++) {
            double t = (double) i / SAMPLE_RATE;
            double sample = switch (waveform) {
                case SINE     -> Math.sin(2 * Math.PI * frequency * t);
                case SQUARE   -> Math.signum(Math.sin(2 * Math.PI * frequency * t));
                case SAWTOOTH -> 2.0 * (frequency * t - Math.floor(0.5 + frequency * t));
            };

            // Envelope: linear attack and release to avoid clicks
            double env = 1.0;
            if (i < attackSamples) {
                env = i / attackSamples;
            } else if (i > totalSamples - releaseSamples) {
                env = (totalSamples - i) / releaseSamples;
            }

            data[i] = (byte) (sample * env * volume * 127);
        }
        return data;
    }

    private void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}
