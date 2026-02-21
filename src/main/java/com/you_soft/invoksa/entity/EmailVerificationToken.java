package com.you_soft.invoksa.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "emailverificationtoken")
@Data
@RequiredArgsConstructor
public class EmailVerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    private String token;
    private Date expirationDate;
    private boolean used;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

}
