package com.huy.pdoc.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ChangePasswordRequest {
    private String oldPassword;
    @Size(min = 8, message = "Password must have at least 8 characters")
    private String newPassword;
}
