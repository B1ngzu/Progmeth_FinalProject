package Progmeth_project2;

/**
 * Fat-JAR entry point for the Memory Card Matching Game.
 *
 * <p>This class deliberately does <em>not</em> extend
 * {@link javafx.application.Application}.  When the JVM launches a class that
 * <em>does</em> extend {@code Application} directly from the module path, the
 * JavaFX module system requires the owning module to export the package — a
 * constraint that breaks most shadow/fat-JAR setups.  By using a plain
 * {@code main} method here that delegates to {@link Main#main(String[])}, we
 * avoid that restriction while keeping the actual application class clean.</p>
 *
 * <p>The {@code manifest.attributes('Main-Class')} in {@code build.gradle}
 * points to this class, not to {@link Main}.</p>
 */
public class Launcher {

    /** Private utility-class constructor — not instantiated. */
    private Launcher() {}

    /**
     * JVM entry point.  Delegates immediately to {@link Main#main(String[])}.
     *
     * @param args command-line arguments forwarded to JavaFX
     */
    public static void main(String[] args) {
        Main.main(args);
    }
}
