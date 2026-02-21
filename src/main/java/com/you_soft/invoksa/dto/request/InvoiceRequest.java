
package com.you_soft.invoksa.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@Data
public class InvoiceRequest {
    private Long id;
    private ClientRequest client;
    private UserRequest user;
    private Double total;
    private String status;


    private List<InvoiceItemRequest> items;

    // getters & setters
}