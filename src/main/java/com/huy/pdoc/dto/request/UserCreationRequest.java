package com.huy.pdoc.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    @Size(min = 3, message = "Username must have at least 3 characters")
    String username;
    @Size(min = 8, message = "Password must have at least 8 characters")
    String password;
    @NotBlank(message = "Cannot be empty")
    String firstName;
    @NotBlank(message = "Cannot be empty")
    String lastName;
    @Email(message = "Invalid email format")
    @NotBlank(message = "Cannot be empty")
    String email;
}
