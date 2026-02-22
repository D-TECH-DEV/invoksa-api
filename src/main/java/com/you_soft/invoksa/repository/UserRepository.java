package com.you_soft.invoksa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.you_soft.invoksa.entity.User;


public interface UserRepository extends JpaRepository <User, Long>{
    User findByUsername(String username);
    User findByEmail(String email);
}