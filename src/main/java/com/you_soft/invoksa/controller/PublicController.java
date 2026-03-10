package com.you_soft.invoksa.controller;

import com.you_soft.invoksa.entity.Invoice;
import com.you_soft.invoksa.service.InvoiceService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/i")
@AllArgsConstructor
public class PublicController {

    final InvoiceService invoiceService;

    @GetMapping(value = "/{token}", produces = MediaType.TEXT_HTML_VALUE)
    public String showInvoice(@PathVariable String token, Model model) {
        Invoice invoice = invoiceService.getByToken(token);
        model.addAttribute("invoice", invoice);

        return "invoice_public";
    }
}