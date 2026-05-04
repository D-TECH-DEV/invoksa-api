package com.you_soft.invoksa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiService {

    private final ChatModel chatModel;

    public String getInvoiceFromAi(String description, String lang) {

        String prompt = """
You are an invoice generator.

Generate a structured invoice in JSON format only.

Language: %s
User description: %s

Rules:
- Analyze the user description and generate invoice items logically.
- Create between 1 and 5 invoice items.
- Each item must contain:
  - description
  - quantity (integer >=1)
  - price (number)
  - total = quantity * price

- Compute the global invoice total.

- Default values:
  - client: null if not specified
  - user: null if not specified
  - status: "PENDING"

Output MUST be JSON only.
""".formatted(lang, description);

        String response = chatModel.call(prompt);
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