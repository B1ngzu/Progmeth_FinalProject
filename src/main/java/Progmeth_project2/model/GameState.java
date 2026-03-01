package Progmeth_project2.model;

import Progmeth_project2.interfaces.Resetable;
import Progmeth_project2.model.card.*;
import Progmeth_project2.model.powerup.*;

import java.util.*;

/**
 * Central model object that holds all mutable state for one game session.
 *
 * <p>Implements {@link Resetable} so the controller can restart a level by
 * calling {@link #reset()} without discarding the current difficulty/theme
 * selection.</p>
 *
 * <p><strong>Polymorphism:</strong> cards are stored as {@code List<BaseCard>}
 * and power-ups as {@code List<BasePowerUp>}.  Callers interact with these
 * collections through abstract base-class references, enabling the controller
 * to loop over them without knowing the concrete subtype.</p>
 */
public class GameState implements Resetable {

    // ── Constants ────────────────────────────────────────────────────────────

    /** Score multiplier applied for each second remaining when a level ends. */
    public static final int TIME_BONUS_PER_SECOND = 1;

    /** Bonus points added to the score for each completed level. */
    public static final int LEVEL_COMPLETION_BONUS = 50;

    /** Timer reduction (seconds) applied every time the player levels up. */
    public static final int TIMER_REDUCTION_PER_LEVEL = 10;

    // ── Configuration (fixed per game session) ────────────────────────────────

    private final Difficulty difficulty;
    private final Theme theme;

    // ── Mutable game state ───────────────────────────────────────────────────

    private List<BaseCard> cards;
    private List<BasePowerUp> powerUps;
    private int score;
    private int combo;
    private int level;
    private int timeRemaining;
    private int frozenSeconds;
    private boolean timerFrozen;
    private boolean revealing;
    private int matchesFound;
    private List<String> hintCardIds;

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Creates a new game state for the given difficulty and theme.
     * Calls {@link #reset()} to initialise all mutable fields.
     *
     * @param difficulty the difficulty level chosen by the player
     * @param theme      the card-face theme chosen by the player
     */
    public GameState(Difficulty difficulty, Theme theme) {
        this.difficulty = difficulty;
        this.theme = theme;
        this.cards = new ArrayList<>();
        this.powerUps = new ArrayList<>();
        this.hintCardIds = new ArrayList<>();
        reset();
    }

    // ── Resetable ────────────────────────────────────────────────────────────

    /**
     * {@inheritDoc}
     * Resets score, combo, level, timer, and power-up availability.
     * Also rebuilds and shuffles the card deck for the current difficulty/theme.
     */
    @Override
    public void reset() {
        score = 0;
        combo = 0;
        level = 1;
        matchesFound = 0;
        timerFrozen = false;
        frozenSeconds = 0;
        revealing = false;
        hintCardIds.clear();
        timeRemaining = difficulty.getTimerSeconds();
        buildDeck();
        buildPowerUps();
    }

    // ── Deck building ────────────────────────────────────────────────────────

    /**
     * Builds a shuffled card deck appropriate for the current difficulty and theme.
     * Each symbol appears exactly twice (one pair).
     */
    private void buildDeck() {
        cards.clear();
        int pairs = difficulty.getTotalPairs();
        String[] symbols = theme.getSymbols();

        for (int i = 0; i < pairs; i++) {
            String sym = symbols[i];
            String id1 = sym + "_A";
            String id2 = sym + "_B";
            cards.add(createCard(id1, sym));
            cards.add(createCard(id2, sym));
        }
        Collections.shuffle(cards);
    }

    /**
     * Factory method that creates the correct concrete {@link BaseCard} subtype
     * for the current theme.
     *
     * <p>Demonstrates polymorphism: the caller stores the result as
     * {@code BaseCard} regardless of which subtype is returned.</p>
     *
     * @param cardId    unique card identifier
     * @param symbolKey shared pair key
     * @return a new {@link AnimalCard}, {@link FruitCard}, or {@link NumberCard}
     */
    private BaseCard createCard(String cardId, String symbolKey) {
        return switch (theme) {
            case ANIMALS -> new AnimalCard(cardId, symbolKey);
            case FRUITS  -> new FruitCard(cardId, symbolKey);
            case NUMBERS -> new NumberCard(cardId, symbolKey);
            case ANIME -> new AnimeCard(cardId, symbolKey);
        };
    }

    /**
     * Initialises one of each power-up type, each available at the start of
     * a level.
     */
    private void buildPowerUps() {
        powerUps.clear();
        powerUps.add(new RevealPowerUp());
        powerUps.add(new FreezePowerUp());
        powerUps.add(new HintPowerUp());
    }

    // ── Level progression ────────────────────────────────────────────────────

    /**
     * Advances to the next level: awards time bonus and level-completion bonus,
     * increments the level counter, rebuilds and shuffles the deck, and recharges
     * power-ups. The timer is slightly shorter than the previous level.
     */
    public void advanceLevel() {
        // Award time bonus
        score += timeRemaining * TIME_BONUS_PER_SECOND;
        // Award level-completion bonus
        score += LEVEL_COMPLETION_BONUS * level;

        level++;
        matchesFound = 0;
        revealing = false;
        hintCardIds.clear();

        // Slightly shorter timer each level, minimum 30 s
        int newTimer = difficulty.getTimerSeconds() - (level - 1) * TIMER_REDUCTION_PER_LEVEL;
        timeRemaining = Math.max(30, newTimer);

        buildDeck();
        // Recharge all power-ups
        for (BasePowerUp pu : powerUps) {
            pu.reset();
        }
    }

    // ── Score / combo ────────────────────────────────────────────────────────

    /**
     * Records a successful match: increments the combo counter and adds
     * {@code baseScore × comboMultiplier} to the total score.
     *
     * @param baseScore the per-card base score (from the matched {@link BaseCard})
     */
    public void recordMatch(int baseScore) {
        combo++;
        score += baseScore * combo;
        matchesFound++;
    }

    /**
     * Records a failed match: resets the combo counter to zero.
     */
    public void recordMismatch() {
        combo = 0;
    }

    // ── Timer helpers ─────────────────────────────────────────────────────────

    /**
     * Decrements the countdown timer by one second, unless the timer is
     * currently frozen.
     */
    public void tickTimer() {
        if (timerFrozen) {
            if (frozenSeconds > 0) {
                frozenSeconds--;
            }
            // Unfreeze as soon as the counter reaches zero (same tick)
            if (frozenSeconds == 0) {
                timerFrozen = false;
            }
            return;
        }
        if (timeRemaining > 0) {
            timeRemaining--;
        }
    }

    /**
     * Freezes the timer for the specified number of seconds.
     *
     * @param seconds number of seconds to freeze
     */
    public void freezeTimer(int seconds) {
        timerFrozen = true;
        frozenSeconds = seconds;
    }

    // ── State queries ─────────────────────────────────────────────────────────

    /**
     * Returns {@code true} if all pairs have been matched on the current level.
     *
     * @return whether the level is complete
     */
    public boolean isLevelComplete() {
        return matchesFound >= difficulty.getTotalPairs();
    }

    /**
     * Returns {@code true} if the countdown has reached zero.
     *
     * @return whether the timer has expired
     */
    public boolean isTimeUp() {
        return timeRemaining <= 0;
    }

    // ── Power-up state ─────────────────────────────────────────────────────────

    /**
     * Sets the global reveal flag (used by {@link RevealPowerUp}).
     *
     * @param revealing {@code true} to reveal all cards
     */
    public void setRevealing(boolean revealing) {
        this.revealing = revealing;
    }

    /**
     * Stores the card IDs to highlight (used by {@link HintPowerUp}).
     *
     * @param ids list of two card ID strings
     */
    public void setHintCardIds(List<String> ids) {
        hintCardIds.clear();
        if (ids != null) hintCardIds.addAll(ids);
    }

    /**
     * Clears any active hint highlight.
     */
    public void clearHint() {
        hintCardIds.clear();
    }

    // ── Accessors ────────────────────────────────────────────────────────────

    /** Returns the difficulty for this game session. @return difficulty */
    public Difficulty getDifficulty() { return difficulty; }

    /** Returns the card theme for this game session. @return theme */
    public Theme getTheme() { return theme; }

    /** Returns the current shuffled card list. @return list of {@code BaseCard} */
    public List<BaseCard> getCards() { return cards; }

    /** Returns the list of power-ups. @return list of {@code BasePowerUp} */
    public List<BasePowerUp> getPowerUps() { return powerUps; }

    /** Returns the current accumulated score. @return score */
    public int getScore() { return score; }

    /** Returns the current combo multiplier (consecutive matches). @return combo count */
    public int getCombo() { return combo; }

    /** Returns the current level number (1-based). @return level */
    public int getLevel() { return level; }

    /** Returns seconds remaining on the countdown timer. @return seconds remaining */
    public int getTimeRemaining() { return timeRemaining; }

    /** Returns whether the timer is currently frozen. @return {@code true} if frozen */
    public boolean isTimerFrozen() { return timerFrozen; }

    /** Returns whether the Reveal power-up is currently active. @return {@code true} if revealing */
    public boolean isRevealing() { return revealing; }

    /** Returns the number of pairs matched on the current level. @return matches found */
    public int getMatchesFound() { return matchesFound; }

    /** Returns an unmodifiable list of card IDs to highlight for the Hint power-up. @return hint card IDs */
    public List<String> getHintCardIds() { return Collections.unmodifiableList(hintCardIds); }
}
