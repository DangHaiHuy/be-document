package com.huy.pdoc.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.huy.pdoc.entity.Role;
import com.huy.pdoc.entity.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String id;
    String username;
    String firstName;
    String lastName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    LocalDate dob;
    String picture;
    private String namePictureFirebase;
    Set<Role> authorities;
    private String location;
    private String phone;
    private String email;
    Boolean noPassword;
    Boolean activated;

    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.noPassword = user.getPassword() != null ? false : true;
        this.dob = user.getDob();
        this.picture = user.getPicture();
        this.authorities = user.getAuthorities();
        this.phone = user.getPhone();
        this.email = user.getEmail();
        this.location = user.getLocation();
        this.activated=user.isActivated();
    }
}
