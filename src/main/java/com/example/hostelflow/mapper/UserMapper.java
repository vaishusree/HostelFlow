package com.example.hostelflow.mapper;

import com.example.hostelflow.dto.UserResponse;
import com.example.hostelflow.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getRegistrationNumber(),
                user.getName(),
                user.getEmail(),
                user.isActivated(),
                user.getUserRole(),
                user.getCreatedAt()
        );
    }
}