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
import com.you_soft.invoksa.exception.ResourceNotFoundException;
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
                                .orElseThrow(() -> new ResourceNotFoundException("Client introuvable avec l'id: " + invoiceRequest.getClientId()));

                Invoice invoice = Invoice.builder()
                                .client(client)
                                .status(invoiceRequest.getStatus())
                                .build();

                List<InvoiceItem> items = invoiceRequest.getItems()
                                .stream()
                                .map(invoiceItemMapper::toEntity)
                                .toList();
                
                items.forEach(item -> item.setInvoice(invoice));
                invoice.setItems(items);
                
                double total = items.stream()
                                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                                .sum();
                invoice.setTotal(total);
                invoice.setNumber(matriculeUtils.generate(client));

                return invoiceMapper.toResponse(invoiceRepository.save(invoice));
        }

        public List<InvoiceResponse> getAll() {
                return invoiceRepository.findAll().stream()
                                .map(invoiceMapper::toResponse)
                                .toList();
        }

        public List<InvoiceResponse> getMyInvoices() {
                User user = jwtUtils.getConnectedUser();
                if (user == null) {
                        throw new RuntimeException("Utilisateur non connecté");
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
                return invoiceRepository.findById(id)
                                .map(invoiceMapper::toResponse)
                                .orElseThrow(() -> new ResourceNotFoundException("Facture non trouvée"));
        }

        @Transactional
        public InvoiceResponse update(Long id, InvoiceRequest invoiceRequest) {
                Invoice invoice = invoiceRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Facture non trouvée"));
                
                Client client = clientRepository.findById(invoiceRequest.getClientId())
                                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé"));

                invoice.setClient(client);
                invoice.setStatus(invoiceRequest.getStatus());

                invoice.getItems().clear();
                if (invoiceRequest.getItems() != null) {
                        List<InvoiceItem> items = invoiceRequest.getItems()
                                        .stream()
                                        .map(invoiceItemMapper::toEntity)
                                        .toList();
                        items.forEach(item -> item.setInvoice(invoice));
                        invoice.getItems().addAll(items);
                        
                        double total = items.stream()
                                        .mapToDouble(item -> item.getPrice() * item.getQuantity())
                                        .sum();
                        invoice.setTotal(total);
                }

                return invoiceMapper.toResponse(invoiceRepository.save(invoice));
        }

        @Transactional
        public void delete(Long id) {
                if (!invoiceRepository.existsById(id)) {
                        throw new ResourceNotFoundException("Facture non trouvée");
                }
                invoiceRepository.deleteById(id);
        }

        public byte[] getInvoicePdf(Long id, String color, String companyName, String companyPhone, String companyEmail, String companyAddress, String legalMentions) {
                Invoice invoice = invoiceRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Facture non trouvée"));

                // Status verification for pdf output
                String statusName = "PENDING";
                if (invoice.getStatus() == 200 || "PAID".equalsIgnoreCase(String.valueOf(invoice.getStatus()))) {
                    statusName = "PAID";
                } else if ("DRAFT".equalsIgnoreCase(String.valueOf(invoice.getStatus()))) {
                    statusName = "DRAFT";
                }

                Map<String, Object> invoiceMap = new HashMap<>();
                invoiceMap.put("id", invoice.getId());
                invoiceMap.put("number", invoice.getNumber());
                invoiceMap.put("clientName", invoice.getClient() != null ? invoice.getClient().getName() : "____");
                invoiceMap.put("date", invoice.getCreatedAt() != null ? invoice.getCreatedAt().toLocalDate().toString() : "");
                invoiceMap.put("total", invoice.getTotal() != null ? invoice.getTotal() : 0.0);
                invoiceMap.put("status", statusName);

                List<Map<String, Object>> itemsList = invoice.getItems().stream().map(item -> {
                        Map<String, Object> itemMap = new HashMap<>();
                        itemMap.put("name", item.getDescription() != null ? item.getDescription() : "");
                        itemMap.put("price", item.getPrice() != null ? item.getPrice() : 0.0);
                        itemMap.put("quantity", item.getQuantity() != null ? item.getQuantity() : 0);
                        return itemMap;
                }).toList();

                invoiceMap.put("items", itemsList);

                Map<String, Object> configMap = new HashMap<>();
                configMap.put("color", color != null && !color.isEmpty() ? color : "#3498db");
                configMap.put("companyName", companyName != null ? companyName : "Notre Entreprise");
                configMap.put("companyPhone", companyPhone != null ? companyPhone : "");
                configMap.put("companyEmail", companyEmail != null ? companyEmail : "");
                configMap.put("companyAddress", companyAddress != null ? companyAddress : "");
                configMap.put("legalMentions", legalMentions != null ? legalMentions : "");

                Map<String, Object> data = new HashMap<>();
                data.put("invoice", invoiceMap);
                data.put("config", configMap);

                return pdfUtil.generateInvoicePdf(data);
        }

        public Invoice getByToken(String token) {
        Invoice invoice = invoiceRepository.findByToken(token);
        if (invoice == null) {
            throw new ResourceNotFoundException("Facture non trouvée avec ce token");
        }
        return invoice;
    }

    public InvoiceResponse invoiceAi(String description, String lang, String devise) {
                try {
                        String jsonInvoice = aiUtil.getInvoiceFromAi(description, lang, devise);
                        ObjectMapper objectMapper = new ObjectMapper();
                        Invoice invoice = objectMapper.readValue(jsonInvoice, Invoice.class);
                        return invoiceMapper.toResponse(invoice);
                } catch (Exception e) {
                        throw new RuntimeException("Erreur génération facture IA : " + e.getMessage());
                }
        }
}