package com.huy.pdoc.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationRequest {
    @Size(min = 3, message = "Username must have at least 3 characters")
    String username;
    @Size(min = 8, message = "Password must have at least 8 characters")
    String password;
}
