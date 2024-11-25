package com.huy.pdoc.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.huy.pdoc.dto.request.CheckOtpRequest;
import com.huy.pdoc.dto.response.ApiResponse;
import com.huy.pdoc.dto.response.CheckOtpResponse;
import com.huy.pdoc.service.OtpService;

@RestController
@RequestMapping("/api/v1/otp")
public class OtpController {
    private OtpService otpService;

    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    @GetMapping("/send-otp/{username}")
    public ApiResponse<Void> sendOtpEmail(@PathVariable String username) {
        otpService.sendOtpEmail(username);
        return ApiResponse.<Void>builder().build();
    }

    @PostMapping("/check-otp/{username}")
    public ApiResponse<CheckOtpResponse> checkOtpCode(@PathVariable String username,
            @RequestBody CheckOtpRequest request) {
        return ApiResponse.<CheckOtpResponse>builder().result(otpService.checkOtpCode(username, request)).build();
    }
}
