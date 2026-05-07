package com.you_soft.invoksa.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiInvoiceResponse {
    private Double total;
    private List<AiItemResponse> items;
    private Integer status;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AiItemResponse {
        private String description;
        private Integer quantity;
        private Double price;
        private Double total;
    }
}