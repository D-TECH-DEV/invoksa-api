
package com.you_soft.invoksa.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceRequest {

    private Long id;


    private ClientRef client;
 
    private Double total;
    //private Double total;


    private int status;

    private List<InvoiceItemRequest> items;

    public Long getClientId() {
        return client != null ? client.getId() : null;
    }


//    public int getStatus( String status) {
//        if ("unpaid".equals(status))
//            return 400;
//        if ("paid".equals(status))
//            return 200;
//        if ("pending".equals(status))
//            return 500;
//        return 501;
//    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClientRef {
        private Long id;
    }
}