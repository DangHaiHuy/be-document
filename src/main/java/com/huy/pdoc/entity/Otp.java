package com.huy.pdoc.entity;

import java.util.Date;

import com.huy.pdoc.dto.response.UserResponse;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(name = "otp_code")
    private String otpCode;
    @Column(name = "expiry_time")
    private Date expiryTime;
    @JoinColumn(name = "user", unique = true)
    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    public UserResponse getUser() {
        return new UserResponse(user);
    }
}
