package com.you_soft.invoksa.utils;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AiUtil {
    private final ChatClient chatClient;

    public AiUtil(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String getInvoiceFromAi(String description, String lang, String devise) {

        var prompt = """
                You are an expert invoice processing assistant for 'Invoksa'. 
                Your task is to extract line items from a user description and return a valid JSON object.
                
                Language: %s
                Currency-Devise: %s
                User description: %s
                
                IMPORTANT: Return ONLY the raw JSON object. 
                DO NOT include markdown code blocks (```json ... ```).
                DO NOT include any preamble or explanation.
                
                RULES:
                1. Extract or infer invoice items (description, quantity, price).
                2. Generate between 1 and 10 items.
                3. If quantity is not mentioned, use 1.
                4. If price is not mentioned, use 0.
                5. Calculate 'total' for each item: quantity * price.
                6. Calculate the global 'total' for the invoice.
                7. Use the following JSON structure.
                
                JSON STRUCTURE:
                {
                  "total": 0.0,
                  "status": 500,
                  "items": [
                    {
                      "description": "Item description",
                      "quantity": 1,
                      "price": 100.0,
                      "total": 100.0
                    }
                  ]
                }
                """.formatted(lang, devise, description);

        try {
            // Using the recommended fluent API that avoids common AssistantMessage.getContent() issues
            String response = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

            if (response == null || response.isBlank()) {
                throw new RuntimeException("L'IA a renvoyé une réponse vide.");
            }

            return extractJson(response);
        } catch (Exception e) {
            log.error("Error calling AI: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de l'appel à l'IA: " + e.getMessage(), e);
        }
    }

    private String extractJson(String response) {
        if (response == null) return "{}";
        
        int firstBrace = response.indexOf('{');
        int lastBrace = response.lastIndexOf('}');
        
        if (firstBrace >= 0 && lastBrace >= 0 && lastBrace > firstBrace) {
            return response.substring(firstBrace, lastBrace + 1);
        }
        
        return response.trim();
    }
}

