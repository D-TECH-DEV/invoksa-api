package com.you_soft.invoksa.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
public class AiService {
    private final ChatClient chatClient;

    public AiService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String getInvoiceFromAi(String description, String lang) {

        var prompt = """
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

Output format MUST be strictly JSON like this:

{
  "client": { "id": null },
  "user": { "id": null },
  "total": 0,
  "status": "PENDING",
  "items": [
    {
      "description": "",
      "quantity": 1,
      "price": 0,
      "total": 0
    }
  ]
}

Do not add explanations, markdown, or text outside JSON.
""".formatted(lang, description);

        var completion = Objects.requireNonNull(
                chatClient.prompt()
                        .user(prompt)
                        .call()
                        .content()
        );

        return completion;
    }
}
