package uan.edu.co.crazy_bakery.application.services;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class OpenAITranslationService {

    private final ChatClient chatClient;

    // Spring AI will provide a configured ChatClient.Builder bean
    public OpenAITranslationService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public String translateJsonToEnglish(String jsonContent) {
        // The prompt is crucial. It tells OpenAI exactly what to do.
        String prompt = "Translate the following JSON content from Spanish to English. Only return the translated JSON, do not include any other text, explanations, or formatting like markdown code blocks. Maintain the original JSON structure, only translating string values:\n" + jsonContent;

        try {
            String translatedContent = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

            // Clean up possible markdown code blocks if OpenAI adds them (although the prompt tries to prevent it)
            if (translatedContent != null) {
                if (translatedContent.startsWith("```json")) {
                    translatedContent = translatedContent.substring("```json".length(), translatedContent.length() - "```".length()).trim();
                } else if (translatedContent.startsWith("```")) {
                    translatedContent = translatedContent.substring("```".length(), translatedContent.length() - "```".length()).trim();
                }
            }
            return translatedContent;

        } catch (Exception e) {
            System.err.println("Error general al traducir con OpenAI: " + e.getMessage());
            // In case of an error, return null so the interceptor uses the original JSON
            return null;
        }
    }
}
