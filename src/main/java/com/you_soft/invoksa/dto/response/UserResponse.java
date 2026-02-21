package com.you_soft.invoksa.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class UserResponse {
    private Long id;
    private String username;
    private String email;

}