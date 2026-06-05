package com.aistudy.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class GeminiAPI {

    private static String API_KEY;
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=";

    static {
        try (var input = GeminiAPI.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input != null) {
                prop.load(input);
                API_KEY = getConfigValue("GEMINI_API_KEY", prop.getProperty("gemini.api.key"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static String getConfigValue(String envName, String fallback) {
        String value = System.getenv(envName);
        return value == null || value.isBlank() ? fallback : value;
    }

    public static String generateContent(String prompt) throws Exception {
        if (API_KEY == null || API_KEY.isEmpty() || API_KEY.equals("YOUR_API_KEY")) {
            throw new Exception("Gemini API Key is not configured. Set GEMINI_API_KEY or update config.properties.");
        }

        URL url = new URL(API_URL + API_KEY);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // Build JSON request using Gson
        JsonObject messageBody = new JsonObject();
        JsonArray contents = new JsonArray();
        JsonObject partsObj = new JsonObject();
        JsonArray parts = new JsonArray();
        JsonObject textObj = new JsonObject();
        
        textObj.addProperty("text", prompt);
        parts.add(textObj);
        partsObj.add("parts", parts);
        contents.add(partsObj);
        messageBody.add("contents", contents);

        Gson gson = new Gson();
        String jsonInputString = gson.toJson(messageBody);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (InputStreamReader reader = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)) {
                JsonObject jsonResponse = JsonParser.parseReader(reader).getAsJsonObject();
                if (jsonResponse.has("candidates")) {
                    JsonArray candidates = jsonResponse.getAsJsonArray("candidates");
                    if (candidates.size() > 0) {
                        JsonObject candidate = candidates.get(0).getAsJsonObject();
                        JsonObject content = candidate.getAsJsonObject("content");
                        if (content.has("parts")) {
                            JsonArray resParts = content.getAsJsonArray("parts");
                            if (resParts.size() > 0) {
                                return resParts.get(0).getAsJsonObject().get("text").getAsString();
                            }
                        }
                    }
                }
            }
        } else {
            // Read error stream for debugging
            String errorMsg = "HTTP code: " + responseCode;
            try (InputStreamReader errorReader = new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8)) {
                StringBuilder sb = new StringBuilder();
                int ch;
                while ((ch = errorReader.read()) != -1) {
                    sb.append((char) ch);
                }
                errorMsg = sb.toString();
                System.err.println("Gemini API Error: " + errorMsg);
            } catch (Exception ignored) {}
            throw new Exception("API Error (" + responseCode + "): " + errorMsg);
        }
        
        return "Failed to generate content.";
    }
}
