package com.thirdact;

/**
 * Indirection launcher — the real entry point registered in the JAR manifest.
 *
 * Why this exists:
 * When the JDK launcher detects a Main-Class that extends Application,
 * it requires JavaFX to be on the module path. Since we use a fat JAR
 * (JavaFX is on the classpath, not as modules), that check fails with
 * "JavaFX runtime components are missing".
 *
 * By registering this non-Application class as the manifest Main-Class,
 * the JDK skips the module check. JavaFX then bootstraps normally from
 * the classpath when Application.launch() is called.
 */
public class Launcher {
    public static void main(String[] args) {
        Main.main(args);
    }
}
