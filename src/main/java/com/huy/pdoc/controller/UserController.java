package com.huy.pdoc.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.huy.pdoc.dto.request.ChangePasswordRequest;
import com.huy.pdoc.dto.request.PasswordCreationRequest;
import com.huy.pdoc.dto.request.PictureUpdateRequest;
import com.huy.pdoc.dto.request.ResetPasswordRequest;
import com.huy.pdoc.dto.request.UserCreationRequest;
import com.huy.pdoc.dto.request.UserUpdateRequest;
import com.huy.pdoc.dto.response.ActivateResponse;
import com.huy.pdoc.dto.response.ApiResponse;
import com.huy.pdoc.dto.response.ChangePasswordResponse;
import com.huy.pdoc.dto.response.HiddenEmailResponse;
import com.huy.pdoc.dto.response.ResetPasswordResponse;
import com.huy.pdoc.dto.response.UserResponse;
import com.huy.pdoc.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @PostMapping
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request))
                .build();
    }

    @PostMapping("/create-password")
    ApiResponse<Void> createPassWord(@RequestBody @Valid PasswordCreationRequest passwordCreationRequest) {
        userService.createPassWord(passwordCreationRequest);
        return ApiResponse.<Void>builder().message("Successfully creating password").build();
    }

    @GetMapping
    ApiResponse<List<UserResponse>> getUsers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        authentication.getAuthorities()
                .forEach(grantedAuthority -> System.err.println(grantedAuthority.getAuthority()));
        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getUsers())
                .build();
    }

    @GetMapping("/{userId}")
    ApiResponse<UserResponse> getUser(@PathVariable("userId") String userId) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUser(userId))
                .build();
    }

    @GetMapping("/myInfo")
    ApiResponse<UserResponse> getUser() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    @PutMapping("/{userId}")
    ApiResponse<UserResponse> updateUser(@PathVariable String userId, @RequestBody @Valid UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser(userId, request))
                .build();
    }
    
    @PutMapping("/update-picture/{userId}")
    ApiResponse<UserResponse> updatePicture(@PathVariable String userId,
            @RequestBody PictureUpdateRequest pictureUpdateRequest) {
        return ApiResponse.<UserResponse>builder().result(userService.updatePicture(userId, pictureUpdateRequest))
                .build();
    }

    @PostMapping("/activate")
    ApiResponse<ActivateResponse> activateAccount(@RequestParam(required = true) String code,
            @RequestParam(required = true) String email) {
        return ApiResponse.<ActivateResponse>builder().result(userService.activateAccount(code, email)).build();
    }

    @GetMapping("/get-hidden-email/{username}")
    ApiResponse<HiddenEmailResponse> getHiddenEmail(@PathVariable String username) {
        return ApiResponse.<HiddenEmailResponse>builder().result(userService.getHiddenEmail(username)).build();
    }

    @PutMapping("/change-password")
    ApiResponse<ChangePasswordResponse> changePassword(
            @RequestBody @Valid ChangePasswordRequest changePasswordRequest) {
        return ApiResponse.<ChangePasswordResponse>builder().result(userService.changePassword(changePasswordRequest))
                .build();
    }

    @PutMapping("/reset-password")
    ApiResponse<ResetPasswordResponse> resetPassword(
            @RequestBody @Valid ResetPasswordRequest request) {
        return ApiResponse.<ResetPasswordResponse>builder().result(userService.resetPassword(request))
                .build();
    }
}
