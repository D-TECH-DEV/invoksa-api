
package com.you_soft.invoksa.dto.request;

import com.you_soft.invoksa.entity.Client;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class InvoiceRequest {
    private Long id;
    private Long clientId;
    //private UserRequest user;
    private Double total;
    private String status;


    private List<InvoiceItemRequest> items;

    // getters & setters
}