package com.you_soft.invoksa.utils;

import com.you_soft.invoksa.entity.User;
import com.you_soft.invoksa.service.EmailService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
public class EmailUtil {
    private final EmailService emailService;

    public String sendRegisterMail(User user, String tokenEmailCheck) {
       try {
           emailService.sendEmail(
                   user.getEmail(),
                   "Vérification de votre compte",

                   "Cliquez sur ce lien : http://localhost:8080/api/auth/verify?token=" + tokenEmailCheck +
                           "vous avez 15 minutes pour confirmer le mail !"
           );

           return "Email de vérification envoyé";
       } catch (RuntimeException e) {
           throw new RuntimeException(e);
       }
    }
}
