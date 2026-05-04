package com.you_soft.invoksa.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class LoginRequest {
    @NotBlank(message = "L'e-mail est requis")
    @Email(message = "Format d'e-mail invalide")
    private String email;

    @NotBlank(message = "Le mot de passe est requis")
    private String password;
}
