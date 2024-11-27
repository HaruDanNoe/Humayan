package com.example.humayan;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class GeminiAPIHelper {
    private static final String API_KEY = "AIzaSyCd5Z0eOaO4K0HLPohfTg3_mLVQuVuUhw4";  // Make sure to replace with your actual API key
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";

    public static String getGeminiResponse(String prompt) throws IOException, JSONException {
        OkHttpClient client = new OkHttpClient();

        // Create the JSON payload
        JSONObject payload = new JSONObject();
        payload.put("contents", new JSONArray()
                .put(new JSONObject()
                        .put("role", "user")
                        .put("parts", new JSONArray()
                                .put(new JSONObject().put("text", prompt))))
        );
        JSONObject generationConfig = new JSONObject();
        generationConfig.put("temperature", 1);
        generationConfig.put("topK", 40);
        generationConfig.put("topP", 0.95);
        generationConfig.put("maxOutputTokens", 8192);
        generationConfig.put("responseMimeType", "text/plain");

        payload.put("generationConfig", generationConfig);

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), payload.toString());

        Request request = new Request.Builder()
                .url(API_URL + "?key=" + API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "No response body";
                throw new IOException("Unexpected code " + response.code() + ": " + errorBody);
            }

            String responseBody = response.body().string();
            JSONObject jsonObject = new JSONObject(responseBody);

            // Get the first candidate and extract the text
            JSONArray candidates = jsonObject.getJSONArray("candidates");
            JSONObject candidate = candidates.getJSONObject(0);
            JSONObject content = candidate.getJSONObject("content");

            // Extract the text from the 'parts' array
            JSONArray parts = content.getJSONArray("parts");
            String generatedText = parts.getJSONObject(0).getString("text");

            return generatedText;
        }
    }
}