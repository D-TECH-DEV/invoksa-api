package com.you_soft.invoksa.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class LoginRequest {
    private Long id;
    private String username;
    private String email;
    private String password;

}