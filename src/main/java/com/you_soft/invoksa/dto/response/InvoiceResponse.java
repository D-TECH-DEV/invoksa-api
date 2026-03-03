
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
    //private UserResponse user;
    private Double sum;
    private String number;
    private String status;
    private List<InvoiceItemResponse> items;

    public void setStatus(int status) {
        if (status == 400) {
            this.status = "unpaid";
        } else if (status == 200) {
            this.status = "paid";
        } else if (status == (500)) {
            this.status = "pending";
        }
    }

    // getters & setters
}