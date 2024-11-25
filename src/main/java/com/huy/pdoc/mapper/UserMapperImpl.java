package com.huy.pdoc.mapper;

import org.springframework.stereotype.Service;

import com.huy.pdoc.dto.request.UserCreationRequest;
import com.huy.pdoc.dto.request.UserUpdateRequest;
import com.huy.pdoc.dto.response.UserResponse;
import com.huy.pdoc.entity.User;

@Service
public class UserMapperImpl implements UserMapper {
    @Override
    public User toUser(UserCreationRequest request) {
        User user=User.builder()
            .username(request.getUsername())
            .password(request.getPassword())
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .build();
        return user;
    }
    @Override
    public UserResponse toUserResponse(User user) {
        return new UserResponse(user);
    }
    @Override
    public void updateUser(User user, UserUpdateRequest request) {
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setDob(request.getDob());
        user.setLocation(request.getLocation());
        user.setPhone(request.getPhone());
    }
}
