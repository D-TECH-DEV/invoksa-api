package com.you_soft.invoksa.mapper;

import com.you_soft.invoksa.dto.request.UserRequest;
import com.you_soft.invoksa.dto.response.UserResponse;
import com.you_soft.invoksa.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        if (user == null) return null;
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .emailVerified(user.isEmailVerified())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public User toEntity(UserRequest userRequest) {
        if (userRequest == null) return null;
        return User.builder()
                .id(userRequest.getId())
                .username(userRequest.getUsername())
                .email(userRequest.getEmail())
                .build();
    }
}
