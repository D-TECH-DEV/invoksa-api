package com.you_soft.invoksa.controller;

import com.you_soft.invoksa.dto.request.InvoiceRequest;
import com.you_soft.invoksa.dto.response.InvoiceResponse;
import com.you_soft.invoksa.service.InvoiceService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;
    @GetMapping
    public ResponseEntity<List<InvoiceResponse>> getAll() {
        return ResponseEntity.ok(invoiceService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponse> getById(@PathVariable Long id){
        return ResponseEntity.ok(invoiceService.getById(id));
    }

    @GetMapping("/me")
    public ResponseEntity<List<InvoiceResponse>> getMyInvoice() {
        return ResponseEntity.ok(invoiceService.getMyInvoices());
    }

    @PostMapping
    public ResponseEntity<InvoiceResponse> create(@RequestBody InvoiceRequest invoiceRequest) {
        return ResponseEntity.ok(invoiceService.create(invoiceRequest));
    }

    @GetMapping("/client/{id}")
    public  ResponseEntity<List<InvoiceResponse>> getClientInvoices(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getClientInvoices(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InvoiceResponse> update(@PathVariable Long id, @RequestBody InvoiceRequest invoiceRequest) {
        return ResponseEntity.ok(invoiceService.update(id, invoiceRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        invoiceService.delete(id);
        return ResponseEntity.noContent().build();
    }

    //juste pour le test de postman je vais  garder la méthode d'en haut
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> getInvoicePdf(@PathVariable Long id,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String tel,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String legalMentions) {
        byte[] pdf = invoiceService.getInvoicePdf(id, color, name, tel, email, address, legalMentions); // retourne juste le byte[]
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=facture_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/ai")
    public ResponseEntity<InvoiceResponse> getInvoiceFromAi(
            @RequestParam String description, @RequestParam String lang, @RequestParam String devise
    )  {
        return ResponseEntity.ok(invoiceService.invoiceAi(description, lang, devise));
    }
}