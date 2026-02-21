package com.you_soft.invoksa.dto.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
public class UserRequest {
    private Long id;
    private String username;
    private String email;

}