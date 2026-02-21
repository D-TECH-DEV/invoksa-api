package com.you_soft.invoksa.service;

import com.you_soft.invoksa.entity.EmailVerificationToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class AuthService {

    public ResponseEntity<?> resetPassword(String email, String newPwd) {


        return new ResponseEntity<>("Mail de confimation envoyé au mail",HttpStatus.OK);
    }

    public String generateToken(){
        return UUID.randomUUID().toString();
    }

//    public void saveToken(String tokens) {
//
//        EmailVerificationToken token = new EmailVerificationToken();
//        token.setToken(tokenEmailCheck);
//        token.setUser(savedUser);
//        token.setExpirationDate(
//                new Date(System.currentTimeMillis() + 1000 * 60 * 15)
//        );
//
//    }
}
