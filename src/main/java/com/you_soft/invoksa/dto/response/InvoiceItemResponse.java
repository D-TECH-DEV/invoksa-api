package com.you_soft.invoksa.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class InvoiceItemResponse {
    private Long id;
    private String description;
    private Integer quantity;
    private Double price;
    private Double total;
}