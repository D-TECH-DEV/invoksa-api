package com.you_soft.invoksa.mapper;

import com.you_soft.invoksa.dto.request.InvoiceRequest;
import com.you_soft.invoksa.dto.response.InvoiceItemResponse;
import com.you_soft.invoksa.dto.response.InvoiceResponse;
import com.you_soft.invoksa.entity.Client;
import com.you_soft.invoksa.entity.Invoice;
import com.you_soft.invoksa.entity.InvoiceItem;
import com.you_soft.invoksa.repository.ClientRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@Component
public class InvoiceMapper {

        private final ClientMapper clientMapper;
        private final InvoiceItemMapper invoiceItemMaper;

        public InvoiceResponse toResponse(Invoice invoice) {
                List<InvoiceItemResponse> itemResponses = java.util.Collections.emptyList();
                if (invoice.getItems() != null) {
                        itemResponses = invoice.getItems()
                                        .stream()
                                        .map(invoiceItemMaper::toResponse)
                                        .toList();
                }

                InvoiceResponse invoiceResponse = InvoiceResponse.builder()
                                .id(invoice.getId())
                                .client(invoice.getClient() != null ? clientMapper.toResponse(invoice.getClient()) : null)
                                .total(invoice.getTotal())
                                .number(invoice.getNumber())
                                .token(invoice.getToken())
                                .items(itemResponses)
                                .createdAt(invoice.getCreatedAt() != null ? invoice.getCreatedAt().toString() : null)
                                .updatedAt(invoice.getUpdatedAt() != null ? invoice.getUpdatedAt().toString() : null)
                                .build();
                invoiceResponse.setStatus(invoice.getStatus());
                return invoiceResponse;
        }

        public Invoice toEntity(InvoiceRequest invoiceRequest, Client client) {
                List<InvoiceItem> items = invoiceRequest.getItems()
                                .stream()
                                .map(invoiceItemMaper::toEntity)
                                .toList();

                Invoice invoice = Invoice.builder()
                                .id(invoiceRequest.getId())
                                .client(client)
                                .total(invoiceRequest.getTotal())
                                .status(invoiceRequest.getStatus())
                                .items(items)
                                .build();

                items.forEach(item -> item.setInvoice(invoice));

                return invoice;
        }

}