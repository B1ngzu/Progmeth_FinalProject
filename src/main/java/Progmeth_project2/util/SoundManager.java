package Progmeth_project2.util;

import javax.sound.sampled.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Singleton that synthesises and plays all in-game sounds programmatically
 * using {@code javax.sound.sampled}.  No audio files are required for SFX.
 *
 * <p>All sound playback is dispatched to a shared daemon-thread executor so
 * that the JavaFX Application Thread is never blocked.</p>
 */
public class SoundManager {

    // Field

    private static final int SAMPLE_RATE = 44100;
    private static SoundManager instance;

    private final ExecutorService executor =
            Executors.newCachedThreadPool(r -> {
                Thread t = new Thread(r, "SoundManager");
                t.setDaemon(true);
                return t;
            });

        // SFX
    private boolean muted     = false;
    private float   sfxVolume = 0.5f;

        // BGM
    private Clip    bgClip    = null;
    private volatile boolean bgPlaying = false;
    private boolean bgMuted   = false;
    private float   bgVolume  = 0.5f;

    // ── Constructor ──────────────────────────────────────────────────────────

    private SoundManager() {}

    // ── Singleton accessor ────────────────────────────────────────────────────

    /**
     * Returns the application-wide {@code SoundManager} singleton.
     *
     * @return singleton instance
     */
    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    // ── Methods ───────────────────────────────────────────────────────────────

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
     * Starts background music from /sounds/01.wav (loops continuously).
     */
    public void startBackgroundMusic() {
        if (bgPlaying) return;
        bgPlaying = true;
        executor.submit(() -> {
            try {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(
                        getClass().getResource("/sounds/01.wav")
                );
                bgClip = AudioSystem.getClip();
                bgClip.open(audioIn);

                // Apply initial volume
                applyBgGain();

                if (!bgMuted) {
                    bgClip.loop(Clip.LOOP_CONTINUOUSLY);
                    bgClip.start();
                }
            } catch (Exception e) {
                System.err.println("BGM load failed: " + e.getMessage());
            }
        });
    }

    /**
     * Stops and closes the background music clip.
     */
    public void stopBackgroundMusic() {
        bgPlaying = false;
        if (bgClip != null) {
            bgClip.stop();
            bgClip.close();
            bgClip = null;
        }
    }



    /**
     * Sets the SFX muted state.
     *
     * @param muted {@code true} to silence all SFX
     */
    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    /**
     * Returns whether SFX is currently muted.
     *
     * @return muted flag
     */
    public boolean isMuted() {
        return muted;
    }

    /**
     * Sets the master SFX volume.
     *
     * @param volume value in [0.0, 1.0]
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

    /**
     * Sets the BGM muted state.
     *
     * @param muted {@code true} to silence background music
     */
    public void setBgMuted(boolean muted) {
        this.bgMuted = muted;
        if (bgClip != null) {
            if (muted) {
                bgClip.stop();
            } else {
                bgClip.loop(Clip.LOOP_CONTINUOUSLY);
                bgClip.start();
            }
        }
    }

    /**
     * Returns whether BGM is currently muted.
     *
     * @return bgMuted flag
     */
    public boolean isBgMuted() {
        return bgMuted;
    }

    /**
     * Sets the BGM volume level.
     *
     * @param volume value in [0.0, 1.0]
     */
    public void setBgVolume(float volume) {
        this.bgVolume = Math.max(0.0f, Math.min(1.0f, volume));
        applyBgGain();
    }

    /**
     * Returns the current BGM volume level.
     *
     * @return value in [0.0, 1.0]
     */
    public float getBgVolume() {
        return bgVolume;
    }



    /**
     * Applies the current bgVolume to the bgClip via MASTER_GAIN FloatControl.
     */
    private void applyBgGain() {
        if (bgClip == null || !bgClip.isOpen()) return;
        try {
            FloatControl gain = (FloatControl) bgClip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = bgVolume <= 0.0001f
                    ? gain.getMinimum()
                    : (float) (Math.log10(bgVolume) * 20);
            gain.setValue(Math.max(gain.getMinimum(), Math.min(gain.getMaximum(), dB)));
        } catch (IllegalArgumentException e) {
            System.err.println("Volume control not supported: " + e.getMessage());
        }
    }

    /**
     * Waveform shapes available for tone synthesis.
     */
    public enum Waveform { SINE, SQUARE, SAWTOOTH }

    /**
     * Synthesises and immediately plays a single tone.
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
                if (ev.getType() == LineEvent.Type.STOP) clip.close();
            });
            clip.start();
        } catch (Exception ignored) {}
    }

    /**
     * Generates PCM audio samples for the given tone parameters.
     */
    private byte[] synthesise(double frequency, int durationMs, float volume, Waveform waveform) {
        int totalSamples = SAMPLE_RATE * durationMs / 1000;
        byte[] data = new byte[totalSamples];

        double attackSamples  = SAMPLE_RATE * 0.005;
        double releaseSamples = SAMPLE_RATE * 0.05;

        for (int i = 0; i < totalSamples; i++) {
            double t = (double) i / SAMPLE_RATE;
            double sample = switch (waveform) {
                case SINE     -> Math.sin(2 * Math.PI * frequency * t);
                case SQUARE   -> Math.signum(Math.sin(2 * Math.PI * frequency * t));
                case SAWTOOTH -> 2.0 * (frequency * t - Math.floor(0.5 + frequency * t));
            };

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