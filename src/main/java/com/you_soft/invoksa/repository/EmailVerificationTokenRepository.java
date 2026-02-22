package com.you_soft.invoksa.repository;

import com.you_soft.invoksa.entity.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    EmailVerificationToken findByToken(String token);
    EmailVerificationToken findByTokenAndType(String token, String type);
}
