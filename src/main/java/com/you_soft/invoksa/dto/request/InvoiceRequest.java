
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

    /**
     * Accepte { "client": { "id": 1 } } depuis le JSON
     */
    private ClientRef client;
 
    private Double total;
    //private Double total;

    /**
     * "unpaid" | "paid" | "pending"
     */
    private int status;

    private List<InvoiceItemRequest> items;

    /**
     * Retourne l'id du client depuis l'objet imbriqué { "client": { "id": 1 } }
     */
    public Long getClientId() {
        return client != null ? client.getId() : null;
    }

    /**
     * Convertit le status String → int pour la persistence
     */
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