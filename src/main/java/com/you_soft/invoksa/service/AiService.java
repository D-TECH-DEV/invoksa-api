package com.you_soft.invoksa.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.stereotype.Service;

@Service
public class AiService {

    private final Client client;

    public AiService() {
        this.client = new Client();
    }

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

        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-1.5-flash",
                        prompt,
                        null
                );

        return response.text();
    }
}