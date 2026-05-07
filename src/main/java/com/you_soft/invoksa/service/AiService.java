package com.you_soft.invoksa.service;

import com.you_soft.invoksa.utils.AiUtil;
import org.springframework.stereotype.Service;

@Service
public class AiService {

    private final AiUtil aiUtil;

    public AiService(AiUtil aiUtil) {
        this.aiUtil = aiUtil;
    }

    public String getInvoiceFromAi(String description, String lang) {
        // We reuse AiUtil logic but defaults devise to empty if not provided at this level
        return aiUtil.getInvoiceFromAi(description, lang, "");
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