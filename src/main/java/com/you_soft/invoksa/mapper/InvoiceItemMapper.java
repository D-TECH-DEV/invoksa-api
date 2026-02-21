package com.you_soft.invoksa.mapper;

import com.you_soft.invoksa.dto.request.InvoiceItemRequest;
import com.you_soft.invoksa.dto.response.InvoiceItemResponse;
import com.you_soft.invoksa.entity.InvoiceItem;
import org.springframework.stereotype.Component;

@Component
public class InvoiceItemMapper {

    public InvoiceItemResponse toResponse(InvoiceItem item) {
        return InvoiceItemResponse.builder()
                .id(item.getId())
                .description(item.getDescription())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .build();
    }

    public InvoiceItem toEntity(InvoiceItemRequest invoiceItemRequest) {
        return InvoiceItem.builder()
                .id(invoiceItemRequest.getId())
                .description(invoiceItemRequest.getDescription())
                .quantity(invoiceItemRequest.getQuantity())
                .price(invoiceItemRequest.getPrice())
                .build();
    }
}
