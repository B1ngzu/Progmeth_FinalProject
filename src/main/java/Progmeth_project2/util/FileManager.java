package Progmeth_project2.util;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class that resolves platform-appropriate file-system paths for
 * persistent game data (leaderboard, settings).
 *
 * <p>All methods are static; no instances of this class should be created.</p>
 */
public final class FileManager {

    /** Sub-directory name used under the user data directory. */
    private static final String APP_DIR_NAME = ".progmeth_project2";

    /** File name for the leaderboard data. */
    public static final String LEADERBOARD_FILE = "leaderboard.dat";

    // ── Constructor ──────────────────────────────────────────────────────────

    /** Private constructor — utility class, not instantiable. */
    private FileManager() {}

    // ── Path resolution ──────────────────────────────────────────────────────

    /**
     * Returns the {@link Path} to the application's data directory.
     *
     * <p>On Windows the directory is {@code %APPDATA%\.progmeth_project2\};
     * on other platforms it is {@code ~/.progmeth_project2/}.</p>
     *
     * @return absolute path to the application data directory
     */
    public static Path getAppDataDirectory() {
        String base;
        String os = System.getProperty("os.name", "").toLowerCase();
        if (os.contains("win")) {
            String appdata = System.getenv("APPDATA");
            base = (appdata != null) ? appdata : System.getProperty("user.home");
        } else {
            base = System.getProperty("user.home");
        }
        return Paths.get(base, APP_DIR_NAME);
    }

    /**
     * Returns the absolute path to the leaderboard data file.
     *
     * @return leaderboard file path string
     */
    public static String getLeaderboardPath() {
        return getAppDataDirectory().resolve(LEADERBOARD_FILE).toString();
    }

    /**
     * Ensures the application data directory exists, creating it (and any
     * parent directories) if necessary.
     *
     * @return the application data directory {@link File}
     */
    public static File ensureAppDataDirectory() {
        File dir = getAppDataDirectory().toFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }
}
