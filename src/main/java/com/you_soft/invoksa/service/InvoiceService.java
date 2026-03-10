package com.you_soft.invoksa.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.you_soft.invoksa.config.JwtUtils;
import com.you_soft.invoksa.dto.request.InvoiceRequest;
import com.you_soft.invoksa.dto.response.InvoiceResponse;
import com.you_soft.invoksa.entity.Client;
import com.you_soft.invoksa.entity.Invoice;
import com.you_soft.invoksa.entity.InvoiceItem;
import com.you_soft.invoksa.entity.User;
import com.you_soft.invoksa.mapper.InvoiceItemMapper;
import com.you_soft.invoksa.mapper.InvoiceMapper;
import com.you_soft.invoksa.repository.ClientRepository;
import com.you_soft.invoksa.repository.InvoiceRepository;
import com.you_soft.invoksa.utils.AiUtil;
import com.you_soft.invoksa.utils.MatriculeUtils;
import com.you_soft.invoksa.utils.PdfUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class InvoiceService {

        private final InvoiceRepository invoiceRepository;
        private final ClientRepository clientRepository;

        private final PdfUtil pdfUtil;
        private final AiUtil aiUtil;
        private final JwtUtils jwtUtils;
        private final MatriculeUtils matriculeUtils;

        private final InvoiceMapper invoiceMapper;

        private final InvoiceItemMapper invoiceItemMapper;

        @Transactional
        public InvoiceResponse create(InvoiceRequest invoiceRequest) {

                Client client = clientRepository.findById(invoiceRequest.getClientId())
                                .orElseThrow(
                                                () -> new RuntimeException("Client introuvable avec l'id: "
                                                                + invoiceRequest.getClientId()));

                // Crée la facture
                Invoice invoice = Invoice.builder()
                                .client(client)
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

                invoice.setNumber(matriculeUtils.generate(client));
                return invoiceMapper.toResponse(invoiceRepository.save(invoice));
        }

        public List<InvoiceResponse> getAll() {
                return invoiceRepository.findAll().stream()
                                .map(invoiceMapper::toResponse)
                                .collect(Collectors.toList());
        }

        public List<InvoiceResponse> getMyInvoices() {
                User user = jwtUtils.getConnectedUser();
                if (user == null) {
                        throw new RuntimeException("User not found");
                }
                List<Client> clients = clientRepository.findAllByUserId(user.getId());
                return invoiceRepository.findAllByClientIn(clients, Sort.by(Sort.Direction.DESC, "createdAt"))
                                .stream()
                                .map(invoiceMapper::toResponse)
                                .toList();
        }

        public List<InvoiceResponse> getClientInvoices(Long clientId) {
                return invoiceRepository.findAllByClientId(clientId, Sort.by(Sort.Direction.DESC, "createdAt"))
                                .stream()
                                .map(invoiceMapper::toResponse)
                                .toList();

        }

        public InvoiceResponse getById(Long id) {
                Invoice invoice = invoiceRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Invoice not found"));
                return invoiceMapper.toResponse(invoice);
        }

        public Invoice getByToken(String token) {
                Invoice invoice = invoiceRepository.findByToken(token);
                if (invoice == null) {
                        throw new RuntimeException("Invoice not found");
                }
                return invoice;
        }

        public InvoiceResponse update(Long id, InvoiceRequest invoiceRequest) {
                Invoice invoice = invoiceRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Invoice not found"));
                Client client = clientRepository.findById(invoiceRequest.getClientId())
                                .orElseThrow(() -> new RuntimeException("Client not found"));

                invoice.setTotal(invoiceRequest.getTotal());
                invoice.setStatus(invoiceRequest.getStatus());
                invoice.setClient(client);
                // invoice.setUser(userMapper.toEntity(invoiceRequest.getUser()));

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

                return invoiceMapper.toResponse(invoiceUpdated);
        }

        public void delete(Long id) {
                Invoice invoice = invoiceRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Invoice not found"));
                invoice.setDeleted(1);
                Invoice invoiceDeleted = invoiceRepository.save(invoice);
        }

        public byte[] getInvoicePdf(Long id) {
                Invoice invoice = invoiceRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Invoice not found"));
                // System.out.println(invoice);

                Map<String, Object> invoiceMap = new HashMap<>();
                invoiceMap.put("id", invoice.getId());
                invoiceMap.put("number", invoice.getNumber());
                invoiceMap.put("clientName", invoice.getClient() != null ? invoice.getClient().getName() : "____");
                invoiceMap.put("date",
                                invoice.getCreatedAt() != null ? invoice.getCreatedAt().toLocalDate().toString() : "");
                invoiceMap.put("total", invoice.getTotal() != null ? invoice.getTotal() : 0.0);

                List<Map<String, Object>> itemsList = invoice.getItems().stream().map(item -> {
                        Map<String, Object> itemMap = new HashMap<>();
                        itemMap.put("name", item.getDescription() != null ? item.getDescription() : "");
                        itemMap.put("price", item.getPrice() != null ? item.getPrice() : 0.0);
                        itemMap.put("quantity", item.getQuantity() != null ? item.getQuantity() : 0);
                        return itemMap;
                }).collect(Collectors.toList());

                invoiceMap.put("items", itemsList);

                Map<String, Object> data = new HashMap<>();
                data.put("invoice", invoiceMap);

                return pdfUtil.generateInvoicePdf(data);
        }

        public InvoiceResponse invoiceAi(String description, String lang, String devise) {
                try {
                        String jsonInvoice = aiUtil.getInvoiceFromAi(description, lang, devise);

                        // Convertir JSON -> Invoice
                        ObjectMapper objectMapper = new ObjectMapper();
                        Invoice invoice = objectMapper.readValue(jsonInvoice, Invoice.class);
                        return invoiceMapper.toResponse(invoice);
                } catch (Exception e) {
                        // e.printStackTrace();
                        throw new RuntimeException("Erreur génération facture IA : " + e.getMessage(), e);
                }
        }
}