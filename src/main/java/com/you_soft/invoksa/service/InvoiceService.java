package com.you_soft.invoksa.service;

import com.you_soft.invoksa.config.JwtUtils;
import com.you_soft.invoksa.dto.request.ClientRequest;
import com.you_soft.invoksa.dto.request.InvoiceRequest;
import com.you_soft.invoksa.dto.response.InvoiceResponse;
import com.you_soft.invoksa.entity.Client;
import com.you_soft.invoksa.entity.Invoice;
import com.you_soft.invoksa.entity.InvoiceItem;
import com.you_soft.invoksa.mapper.ClientMapper;
import com.you_soft.invoksa.mapper.InvoiceItemMapper;
import com.you_soft.invoksa.mapper.InvoiceMapper;
import com.you_soft.invoksa.mapper.UserMapper;
import com.you_soft.invoksa.repository.ClientRepository;
import com.you_soft.invoksa.repository.InvoiceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class InvoiceService {


    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;
    private final ClientMapper clientMapper;
    private final UserMapper userMapper;
    private final InvoiceItemMapper invoiceItemMapper;
    private final JwtUtils jwtUtils;
    private final ClientRepository clientRepository;


    public InvoiceResponse create(InvoiceRequest invoiceRequest) {

        Client client = clientRepository.findById(invoiceRequest.getClientId())
                .orElseThrow(() -> new RuntimeException("Client not found aa"));

        // Crée la facture
        Invoice invoice = Invoice.builder()
                .client(client)
                //.user(user)
                .total(invoiceRequest.getTotal())
                .status(invoiceRequest.getStatus())
                .build();
        // Crée les items
        List<InvoiceItem> items = invoiceRequest.getItems()
                .stream()
                .map(invoiceItemMapper::toEntity)
                .toList();
        items.forEach(item -> item.setInvoice(invoice));
        invoice.setItems(items);
        double total = invoiceRequest.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        invoice.setTotal(total);
         ;
        return invoiceMapper.toResponse(invoiceRepository.save(invoice));
    }

    public List<InvoiceResponse> getAll() {
        return  invoiceRepository.findAll().stream()
                .map(invoiceMapper::toResponse)
                .collect(Collectors.toList());
    }

    public InvoiceResponse getById (Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        return invoiceMapper.toResponse(invoice);
    }

    public InvoiceResponse update(Long id, InvoiceRequest invoiceRequest) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        Client client = clientRepository.findById(invoiceRequest.getClientId())
                .orElseThrow(() -> new RuntimeException("Client not found"));

        invoice.setTotal(invoiceRequest.getTotal());
        invoice.setStatus(invoiceRequest.getStatus());
        invoice.setClient(client);
        //invoice.setUser(userMapper.toEntity(invoiceRequest.getUser()));

        // Mettre à jour les items
        invoice.getItems().clear(); // Supprime les anciens items
        List<InvoiceItem> items = invoiceRequest.getItems()
                .stream()
                .map(invoiceItemMapper::toEntity)
                .toList();
        items.forEach(item -> item.setInvoice(invoice));
        invoice.getItems().addAll(items);

        double total = invoiceRequest.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        invoice.setTotal(total);

        Invoice invoiceUpdated = invoiceRepository.save(invoice);

        return  invoiceMapper.toResponse(invoiceUpdated);
    }

    public void delete(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Invoice not found"));
        invoice.setDeleted(1);
        Invoice invoiceDeleted = invoiceRepository.save(invoice);
    }
}