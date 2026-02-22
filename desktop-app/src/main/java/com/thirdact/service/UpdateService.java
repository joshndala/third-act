package com.thirdact.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class UpdateService {

    private static final String REPO_URL = "https://api.github.com/repos/joshndala/third-act/releases/latest";
    private static final int TIMEOUT_SECONDS = 5;

    /**
     * Checks the GitHub Releases API for a newer version asynchronously.
     * 
     * @param currentVersion The active app version (e.g., "1.0.0").
     * @param onUpdateFound  Callback triggered if a newer version is discovered.
     *                       Passes the remote version string (e.g. "1.1.0").
     */
    public static void checkForUpdates(String currentVersion, Consumer<String> onUpdateFound) {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(REPO_URL))
                .header("Accept", "application/vnd.github.v3+json")
                .GET()
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(responseBody -> {
                    try {
                        JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();
                        if (json.has("tag_name")) {
                            String latestVersion = json.get("tag_name").getAsString();

                            // Strip 'v' prefix if present (e.g., 'v1.1.0' -> '1.1.0')
                            if (latestVersion.startsWith("v")) {
                                latestVersion = latestVersion.substring(1);
                            }

                            if (isNewerVersion(currentVersion, latestVersion)) {
                                onUpdateFound.accept(latestVersion);
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("[UpdateService] Failed to parse release data: " + e.getMessage());
                    }
                })
                .exceptionally(ex -> {
                    System.err.println("[UpdateService] Could not check for updates: " + ex.getMessage());
                    return null;
                });
    }

    /**
     * Extremely simple semantic version comparison.
     * Assumes formatting like "x.y.z".
     */
    private static boolean isNewerVersion(String current, String remote) {
        try {
            String[] currentParts = current.split("\\.");
            String[] remoteParts = remote.split("\\.");

            int length = Math.max(currentParts.length, remoteParts.length);
            for (int i = 0; i < length; i++) {
                int c = i < currentParts.length ? Integer.parseInt(currentParts[i]) : 0;
                int r = i < remoteParts.length ? Integer.parseInt(remoteParts[i]) : 0;
                if (r > c)
                    return true;
                if (r < c)
                    return false;
            }
            return false;
        } catch (NumberFormatException e) {
            // Fallback strategy: just string inequality
            return !current.equals(remote);
        }
    }
}
