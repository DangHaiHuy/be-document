package com.huy.pdoc.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    String firstName;
    String lastName;
    @Past(message = "Birthday must be in the past")
    LocalDate dob;
    String location;
    @Pattern(regexp = "^\\+?[0-9]*$", message = "Invalid phone number format")
    String phone;
    @Email(message = "Invalid email format")
    String email;
}
