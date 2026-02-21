
package com.you_soft.invoksa.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Builder
@Data
public class InvoiceResponse {
    private Long id;
    private ClientResponse client;
    private UserResponse user;
    private Double total;
    private String status;
    private List<InvoiceItemResponse> items;

    // getters & setters
}