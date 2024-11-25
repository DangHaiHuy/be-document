package com.huy.pdoc.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huy.pdoc.dto.request.CheckOtpRequest;
import com.huy.pdoc.dto.response.CheckOtpResponse;
import com.huy.pdoc.entity.Otp;
import com.huy.pdoc.entity.User;
import com.huy.pdoc.exception.AppException;
import com.huy.pdoc.exception.ErrorCode;
import com.huy.pdoc.repository.OtpRepository;
import com.huy.pdoc.repository.UserRepository;

@Service
public class OtpService {
    private OtpRepository otpRepository;
    private EmailService emailService;
    private UserRepository userRepository;

    @Autowired
    public OtpService(OtpRepository otpRepository, EmailService emailService, UserRepository userRepository) {
        this.otpRepository = otpRepository;
        this.emailService = emailService;
        this.userRepository = userRepository;
    }

    public void sendOtpEmail(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED, "Cannot find this username"));
        if (user.isActivated() == false) {
            throw new AppException(ErrorCode.NOT_ACTIVATED,
                    "You need to activate your account to access this resource. Please check your mail");
        }
        if (user.getEmail() != null) {
            String subject = "Your OTP Code: [OTP]";
            String text = "Hi " + username + "! Please use this code for authentication:"
                    + "<html><body></br><h1>" + generateOtp(user)
                    + "</h1></body></html>";
            emailService.sendMessage("haihuy9a@gmail.com", user.getEmail(), subject, text);
        } else
            throw new RuntimeException("Cannot send email");
    }

    private String generateOtp(User user) {
        otpRepository.deleteByUser(user);
        String otpCode = String.format("%06d", new Random().nextInt(999999));
        Otp otp = new Otp();
        otp.setOtpCode(otpCode);
        otp.setUser(user);
        otp.setExpiryTime(new Date(Instant.now().plus(5, ChronoUnit.MINUTES).toEpochMilli()));
        otpRepository.save(otp);
        return otpCode;
    }

    public CheckOtpResponse checkOtpCode(String username, CheckOtpRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED, "Cannot find this username"));
        Otp otp = otpRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Cannot find your account's otp"));
        if (user.isActivated() == false) {
            throw new AppException(ErrorCode.NOT_ACTIVATED,
                    "You need to activate your account to access this resource. Please check your mail");
        }
        if (request.getOtpCode().equals(otp.getOtpCode())) {
            if (otp.getExpiryTime().before(new Date())) {
                throw new AppException(ErrorCode.UNAUTHORIZED, "Your otp code has expired");
            }
            return CheckOtpResponse.builder().result("Verified successfully").build();
        }
        throw new AppException(ErrorCode.INVALID_KEY, "Your otp code you entered is in valid");
    }

    public void deleteByUser(User user) {
        otpRepository.deleteByUser(user);
    }

}
