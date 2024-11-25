package com.huy.pdoc.mapper;

import com.huy.pdoc.dto.request.UserCreationRequest;
import com.huy.pdoc.dto.request.UserUpdateRequest;
import com.huy.pdoc.dto.response.UserResponse;
import com.huy.pdoc.entity.User;

public interface UserMapper {
    User toUser(UserCreationRequest request);

    UserResponse toUserResponse(User user);

    void updateUser(User user, UserUpdateRequest request);
}
