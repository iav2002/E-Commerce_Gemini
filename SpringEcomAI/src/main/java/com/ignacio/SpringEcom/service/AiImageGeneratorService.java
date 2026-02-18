package com.ignacio.SpringEcom.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AiImageGeneratorService {

    @Value("${spring.ai.google.genai.api-key}")
    private String apiKey;

    public byte[] generateImage(String imagePrompt) {
        try (Client client = Client.builder().apiKey(apiKey).build()) {

            GenerateContentConfig config = GenerateContentConfig.builder()
                    .responseModalities("TEXT", "IMAGE")
                    .build();

            GenerateContentResponse response = client.models.generateContent(
                    "gemini-2.5-flash-image",
                    imagePrompt,
                    config);

            // Extract image bytes from response
            for (Part part : response.parts()) {
                if (part.inlineData().isPresent()) {
                    var blob = part.inlineData().get();
                    if (blob.data().isPresent()) {
                        return blob.data().get();
                    }
                }
            }

            throw new RuntimeException("No image generated");
        } catch (Exception e) {
            throw new RuntimeException("Image generation failed: " + e.getMessage(), e);
        }
    }
}