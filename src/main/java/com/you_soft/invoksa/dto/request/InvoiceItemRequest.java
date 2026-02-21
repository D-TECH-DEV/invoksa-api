package com.you_soft.invoksa.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class InvoiceItemRequest {
    private Long id;
    private String description;
    private Integer quantity;
    private Double price;
    private Double total;
}