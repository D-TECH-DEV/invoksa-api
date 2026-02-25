package com.you_soft.invoksa.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ClientRequest {

    private String name;
    private String email;
    private String phone;
    private String address;
    private Long userId;
}