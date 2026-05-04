package com.you_soft.invoksa.utils;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AiUtil {
    private final ChatClient chatClient;

    public AiUtil(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String getInvoiceFromAi(String description, String lang, String devise) {

        var prompt = """
You are an expert invoice processing assistant. 
Your task is to extract line items from a user description and return a valid JSON object.

Language: %s
Currency: %s
User description: %s

RULES:
1. Extract or infer invoice items (description, quantity, price).
2. Generate between 1 and 10 items.
3. If quantity is not mentioned, use 1.
4. If price is not mentioned, use 0.
5. Calculate 'total' for each item: quantity * price.
6. Calculate the global 'total' for the invoice.
7. Return ONLY the JSON object. No preamble, no markdown code blocks, no explanation.

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

        String response = Objects.requireNonNull(
                chatClient.prompt()
                        .user(prompt)
                        .call()
                        .content()
        );

        return extractJson(response);
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
