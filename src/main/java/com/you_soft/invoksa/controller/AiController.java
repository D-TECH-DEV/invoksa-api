package com.you_soft.invoksa.controller;

import com.you_soft.invoksa.service.AiService;
//import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ia")
//@RequiredArgsConstructor
public class AiController {

    private  final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @GetMapping("/invoice")
    public String getInvoiceFromAi(@RequestParam String description, @RequestParam String lang) {
        return aiService.getInvoiceFromAi(description, lang);
    }
}
