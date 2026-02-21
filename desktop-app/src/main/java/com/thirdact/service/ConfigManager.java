package com.thirdact.service;

import java.io.*;
import java.nio.file.*;
import java.util.Properties;

/**
 * Singleton that reads and writes API keys and app config.
 *
 * Resolution order for API keys:
 * 1. Environment variable (e.g. TMDB_API_KEY, GEMINI_API_KEY)
 * 2. .env file in CWD (local development)
 * 3. ~/.thirdact/config.properties (packaged app — written by SettingsView)
 * 4. classpath config.properties (last-resort fallback)
 */
public class ConfigManager {

    private static ConfigManager instance;

    private static final Path USER_CONFIG_PATH = Path.of(System.getProperty("user.home"), ".thirdact",
            "config.properties");

    private ConfigManager() {
    }

    public static synchronized ConfigManager getInstance() {
        if (instance == null)
            instance = new ConfigManager();
        return instance;
    }

    // -----------------------------------------------------------------------
    // Public API key accessors
    // -----------------------------------------------------------------------

    public String getTmdbApiKey() {
        return resolveKey("TMDB_API_KEY", "tmdb.api_key");
    }

    public String getGeminiApiKey() {
        return resolveKey("GEMINI_API_KEY", "gemini.api_key");
    }

    /**
     * Persists both API keys to ~/.thirdact/config.properties.
     * Existing properties in that file are preserved.
     */
    public void saveKeys(String tmdbKey, String geminiKey) throws IOException {
        Properties props = loadUserConfig();
        if (tmdbKey != null)
            props.setProperty("tmdb.api_key", tmdbKey.trim());
        if (geminiKey != null)
            props.setProperty("gemini.api_key", geminiKey.trim());
        writeUserConfig(props);
    }

    /**
     * Returns the saved theme preference: "dark", "light", or "system".
     * Defaults to "system" if not set.
     */
    public String getTheme() {
        Properties props = loadUserConfig();
        return props.getProperty("app.theme", "system");
    }

    /**
     * Persists the theme preference to ~/.thirdact/config.properties.
     *
     * @param theme one of "dark", "light", or "system"
     */
    public void saveTheme(String theme) throws IOException {
        Properties props = loadUserConfig();
        props.setProperty("app.theme", theme);
        writeUserConfig(props);
    }

    // -----------------------------------------------------------------------
    // Private helpers
    // -----------------------------------------------------------------------

    /**
     * Resolves a key using the 4-step lookup chain.
     */
    private String resolveKey(String envName, String propName) {
        // 1. Environment variable
        String env = System.getenv(envName);
        if (env != null && !env.isBlank())
            return env.trim();

        // 2. .env file in CWD
        String fromDotEnv = readDotEnv(envName);
        if (fromDotEnv != null)
            return fromDotEnv;

        // 3. ~/.thirdact/config.properties
        Properties userProps = loadUserConfig();
        String userVal = userProps.getProperty(propName, "").trim();
        if (!userVal.isBlank() && !userVal.startsWith("YOUR_"))
            return userVal;

        // 4. Classpath config.properties
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (is != null) {
                Properties p = new Properties();
                p.load(is);
                String v = p.getProperty(propName, "").trim();
                if (!v.isBlank() && !v.startsWith("YOUR_"))
                    return v;
            }
        } catch (IOException e) {
            System.err.println("[ConfigManager] Error reading classpath config: " + e.getMessage());
        }

        return "";
    }

    private String readDotEnv(String envName) {
        Path dotEnv = Path.of(".env");
        if (!Files.exists(dotEnv))
            return null;
        try (BufferedReader r = Files.newBufferedReader(dotEnv)) {
            String prefix = envName + "=";
            String line;
            while ((line = r.readLine()) != null) {
                line = line.trim();
                if (line.startsWith(prefix)) {
                    String val = line.substring(prefix.length()).trim();
                    if (!val.isBlank())
                        return val;
                }
            }
        } catch (IOException e) {
            System.err.println("[ConfigManager] Error reading .env: " + e.getMessage());
        }
        return null;
    }

    private Properties loadUserConfig() {
        Properties props = new Properties();
        if (Files.exists(USER_CONFIG_PATH)) {
            try (InputStream is = Files.newInputStream(USER_CONFIG_PATH)) {
                props.load(is);
            } catch (IOException e) {
                System.err.println("[ConfigManager] Error reading user config: " + e.getMessage());
            }
        }
        return props;
    }

    private void writeUserConfig(Properties props) throws IOException {
        Files.createDirectories(USER_CONFIG_PATH.getParent());
        try (OutputStream os = Files.newOutputStream(USER_CONFIG_PATH)) {
            props.store(os, "The Third Act — user configuration");
        }
        System.out.println("[ConfigManager] Config saved to " + USER_CONFIG_PATH);
    }
}
