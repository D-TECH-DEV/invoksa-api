package com.you_soft.invoksa.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class InvoiceResponse {

    private Long id;
    private ClientResponse client;
    private Double total;
    private String number;
    private String status;

    // @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private String createdAt;
    private String updatedAt;

    private List<InvoiceItemResponse> items;

    public void setStatus(int statusCode) {

        if (statusCode == 400) {
            this.status = "unpaid";
        }
        else if (statusCode == 200) {
            this.status = "paid";
        }
        else if (statusCode == 500) {
            this.status = "pending";
        }
        else {
            this.status = "unknown";
        }

    }
}
