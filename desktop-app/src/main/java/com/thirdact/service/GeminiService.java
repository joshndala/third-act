package com.thirdact.service;

import com.google.gson.*;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service that sends uploaded images/PDFs to the Gemini API and extracts
 * movie journal field content word-for-word.
 *
 * Model: gemini-3.0-flash-preview
 * Transport: REST/JSON with inline base64 file parts — no extra dependencies.
 */
public class GeminiService {

    private static final String MODEL = "gemini-3-flash-preview";
    private static final String API_BASE = "https://generativelanguage.googleapis.com/v1beta/models/";

    private final HttpClient httpClient;

    public GeminiService() {
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(30))
                .build();
    }

    /**
     * Sends one or more image/PDF files to Gemini and extracts movie journal text.
     *
     * @param files List of image (jpg, png, webp) or PDF files to process
     * @return Map with keys: "summary", "vibe", "peakMoment", "extraNotes"
     *         Values are the verbatim text found in the source material.
     */
    public Map<String, String> analyzeNotes(List<File> files)
            throws IOException, InterruptedException {

        String apiKey = ConfigManager.getInstance().getGeminiApiKey();
        if (apiKey.isBlank()) {
            throw new IllegalStateException(
                    "No Gemini API key configured. Add GEMINI_API_KEY to your .env or set it in Settings.");
        }

        String requestBody = buildRequestJson(files);
        String url = API_BASE + MODEL + ":generateContent?key=" + apiKey;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(120))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Gemini API error " + response.statusCode() + ": " + extractError(response.body()));
        }

        return parseResponse(response.body());
    }

    // -----------------------------------------------------------------------
    // Request builder
    // -----------------------------------------------------------------------

    private String buildRequestJson(List<File> files) throws IOException {
        JsonObject root = new JsonObject();

        // System instruction: strict word-for-word extraction
        JsonObject sysInstruction = new JsonObject();
        JsonArray sysParts = new JsonArray();
        JsonObject sysPart = new JsonObject();
        sysPart.addProperty("text",
                "You are a precise text transcription assistant. Your sole job is to read handwritten or " +
                        "printed notes in the provided images or PDFs and extract them WORD FOR WORD — " +
                        "do NOT summarize, paraphrase, or add your own commentary. " +
                        "If a section has no content, return an empty string for that field.");
        sysParts.add(sysPart);
        sysInstruction.add("parts", sysParts);
        root.add("system_instruction", sysInstruction);

        // Contents array
        JsonArray contents = new JsonArray();
        JsonObject userContent = new JsonObject();
        userContent.addProperty("role", "user");
        JsonArray parts = new JsonArray();

        // Add each file as an inline base64 part
        for (File file : files) {
            String mimeType = detectMimeType(file.getName());
            byte[] bytes = Files.readAllBytes(file.toPath());
            String b64 = Base64.getEncoder().encodeToString(bytes);

            JsonObject inlineData = new JsonObject();
            inlineData.addProperty("mime_type", mimeType);
            inlineData.addProperty("data", b64);

            JsonObject part = new JsonObject();
            part.add("inline_data", inlineData);
            parts.add(part);
        }

        // Instruction part
        JsonObject instructionPart = new JsonObject();
        instructionPart.addProperty("text",
                "The notes I've uploaded are from my movie journal. " +
                        "Please extract the text word-for-word and fill in the following fields. " +
                        "Return ONLY a JSON object with exactly these four keys:\n" +
                        "{\n" +
                        "  \"summary\": \"<word-for-word text for: what the movie is about>\",\n" +
                        "  \"vibe\": \"<word-for-word text for: what it felt like to watch>\",\n" +
                        "  \"peakMoment\": \"<word-for-word text for: the most impactful scene or moment>\",\n" +
                        "  \"extraNotes\": \"<word-for-word text for: extra thoughts, trivia, or context>\"\n" +
                        "}\n" +
                        "If you cannot determine which section a piece of text belongs to, put it in \"extraNotes\". " +
                        "If a field has no content, use an empty string. " +
                        "Return ONLY the JSON object — no markdown fences, no explanation.");
        parts.add(instructionPart);

        userContent.add("parts", parts);
        contents.add(userContent);
        root.add("contents", contents);

        // Generation config: low temperature for faithful extraction
        JsonObject genConfig = new JsonObject();
        genConfig.addProperty("temperature", 0.1);
        genConfig.addProperty("response_mime_type", "application/json");
        root.add("generationConfig", genConfig);

        return new Gson().toJson(root);
    }

    // -----------------------------------------------------------------------
    // Response parser
    // -----------------------------------------------------------------------

    private Map<String, String> parseResponse(String json) {
        Map<String, String> result = new HashMap<>();
        result.put("summary", "");
        result.put("vibe", "");
        result.put("peakMoment", "");
        result.put("extraNotes", "");

        try {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            JsonArray candidates = root.getAsJsonArray("candidates");
            if (candidates == null || candidates.isEmpty())
                return result;

            JsonObject candidate = candidates.get(0).getAsJsonObject();
            JsonObject content = candidate.getAsJsonObject("content");
            JsonArray parts = content.getAsJsonArray("parts");
            if (parts == null || parts.isEmpty())
                return result;

            String text = parts.get(0).getAsJsonObject().get("text").getAsString().trim();

            // Strip markdown fences if model added them despite instructions
            if (text.startsWith("```")) {
                text = text.replaceAll("^```[a-zA-Z]*\\n?", "").replaceAll("```$", "").trim();
            }

            JsonObject fields = JsonParser.parseString(text).getAsJsonObject();
            result.put("summary", getStringSafe(fields, "summary"));
            result.put("vibe", getStringSafe(fields, "vibe"));
            result.put("peakMoment", getStringSafe(fields, "peakMoment"));
            result.put("extraNotes", getStringSafe(fields, "extraNotes"));

        } catch (Exception e) {
            System.err.println("[GeminiService] Error parsing response: " + e.getMessage());
        }

        return result;
    }

    // -----------------------------------------------------------------------
    // Utilities
    // -----------------------------------------------------------------------

    private String detectMimeType(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".pdf"))
            return "application/pdf";
        if (lower.endsWith(".png"))
            return "image/png";
        if (lower.endsWith(".webp"))
            return "image/webp";
        if (lower.endsWith(".gif"))
            return "image/gif";
        // default: jpeg
        return "image/jpeg";
    }

    private String extractError(String body) {
        try {
            JsonObject obj = JsonParser.parseString(body).getAsJsonObject();
            JsonObject error = obj.getAsJsonObject("error");
            if (error != null)
                return error.get("message").getAsString();
        } catch (Exception ignored) {
        }
        return body;
    }

    private String getStringSafe(JsonObject obj, String key) {
        JsonElement el = obj.get(key);
        return (el != null && !el.isJsonNull()) ? el.getAsString() : "";
    }
}
