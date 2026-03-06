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
You are an invoice generator.

Generate a structured invoice in JSON format only.

Language: %s
currency: %s
User description: %s

Rules:
- Analyze the user description and generate invoice items logically.
- Create between 1 and 10 invoice items.
- Each item must contain:
  - description
  - quantity (integer >=1)
  - price (number)
  - total = quantity * price

- Compute the global invoice total.

- Default values:
 
  - status: 500

Output format MUST be strictly JSON like this:

  
  "items": [
    {
      "description": "",
      "quantity": 1,
      "price": 0,
      "total": quantity*prirce
    }
  ]
  


Do not add explanations, markdown, or text outside JSON.
""".formatted(lang, devise,  description);

        return Objects.requireNonNull(
                chatClient.prompt()
                        .user(prompt)
                        .call()
                        .content()
        );
    }
}
