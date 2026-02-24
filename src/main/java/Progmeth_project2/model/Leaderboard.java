package Progmeth_project2.model;

import Progmeth_project2.interfaces.Persistable;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Maintains an ordered list of the top {@value #MAX_ENTRIES} {@link ScoreEntry}
 * objects and persists them to disk using Java object serialisation.
 *
 * <p>Implements {@link Persistable}: {@link #save(String)} writes the list to a
 * binary file; {@link #load(String)} reads it back. Callers should use
 * {@link Progmeth_project2.util.FileManager} to resolve the platform-appropriate
 * save directory before calling these methods.</p>
 */
public class Leaderboard implements Persistable {

    /** Maximum number of entries retained on the leaderboard. */
    public static final int MAX_ENTRIES = 10;

    private final List<ScoreEntry> entries;

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Creates an empty leaderboard.
     */
    public Leaderboard() {
        this.entries = new ArrayList<>();
    }

    // ── Public API ───────────────────────────────────────────────────────────

    /**
     * Adds a new score entry to the leaderboard, maintaining descending order.
     * If the leaderboard already contains {@value #MAX_ENTRIES} entries and the
     * new score does not qualify, it is silently ignored.
     *
     * @param entry the score entry to add (non-null)
     */
    public void addEntry(ScoreEntry entry) {
        if (entry == null) return;
        entries.add(entry);
        Collections.sort(entries);
        if (entries.size() > MAX_ENTRIES) {
            entries.subList(MAX_ENTRIES, entries.size()).clear();
        }
    }

    /**
     * Returns an unmodifiable view of the current top entries in descending
     * score order.
     *
     * @return unmodifiable list of score entries
     */
    public List<ScoreEntry> getTopEntries() {
        return Collections.unmodifiableList(entries);
    }

    /**
     * Returns the number of entries currently on the leaderboard.
     *
     * @return entry count in [0, {@value #MAX_ENTRIES}]
     */
    public int getEntryCount() {
        return entries.size();
    }

    /**
     * Returns {@code true} if the given score qualifies for a leaderboard
     * position (either the board is not yet full or the score beats the
     * current lowest entry).
     *
     * @param score candidate score
     * @return {@code true} if the score would appear on the leaderboard
     */
    public boolean qualifies(int score) {
        if (entries.size() < MAX_ENTRIES) return true;
        return score > entries.get(entries.size() - 1).getScore();
    }

    /**
     * Removes all entries from the leaderboard.
     */
    public void clear() {
        entries.clear();
    }

    // ── Persistable ──────────────────────────────────────────────────────────

    /**
     * {@inheritDoc}
     * Serialises the list of {@link ScoreEntry} objects to the specified file
     * path. Parent directories are created automatically if they do not exist.
     *
     * @param path absolute or relative file path
     * @throws IOException if the file cannot be written
     */
    @Override
    public void save(String path) throws IOException {
        File file = new File(path);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            if (!parent.mkdirs()) {
                throw new IOException("Could not create directory: " + parent.getAbsolutePath());
            }
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(file)))) {
            oos.writeObject(new ArrayList<>(entries));
        }
    }

    /**
     * {@inheritDoc}
     * Deserialises the list of {@link ScoreEntry} objects from the specified
     * file path. If the file does not exist, this method returns without error
     * and the leaderboard remains unchanged.
     *
     * @param path absolute or relative file path
     * @throws IOException if the file exists but cannot be read
     */
    @Override
    @SuppressWarnings("unchecked")
    public void load(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(file)))) {
            Object raw = ois.readObject();
            // Guard against ClassCastException from corrupted / mismatched data
            if (raw instanceof List<?> rawList) {
                entries.clear();
                for (Object item : rawList) {
                    if (item instanceof ScoreEntry se) {
                        entries.add(se);
                    }
                }
                Collections.sort(entries);
            }
        } catch (ClassNotFoundException | ClassCastException e) {
            throw new IOException("Leaderboard data is corrupted: " + e.getMessage(), e);
        }
    }
}
