package com.you_soft.invoksa.dto.request;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InvoiceItemRequest {
    private Long id;
    private String description;
    private Integer quantity;
    private Double price;
    private Double total;
}