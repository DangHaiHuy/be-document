package com.huy.pdoc.controller;

import com.huy.pdoc.dto.request.AuthenticationRequest;
import com.huy.pdoc.dto.request.IntrospectRequest;
import com.huy.pdoc.dto.request.LogoutRequest;
import com.huy.pdoc.dto.request.RefreshTokenRequest;
import com.huy.pdoc.dto.response.ApiResponse;
import com.huy.pdoc.dto.response.AuthenticationResponse;
import com.huy.pdoc.dto.response.IntrospectResponse;
import com.huy.pdoc.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.text.ParseException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/token")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody @Valid AuthenticationRequest request) {
        AuthenticationResponse result = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest introspectRequest)
            throws JOSEException, ParseException {
        IntrospectResponse result = authenticationService.introspect(introspectRequest);
        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .code(result.isValid() == false ? 1006 : 1000)
                .build();
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody LogoutRequest logoutRequest) throws JOSEException, ParseException {
        authenticationService.logout(logoutRequest);
        return ApiResponse.<Void>builder().message("Success logout").build();
    }

    @PostMapping("/refresh")
    ApiResponse<AuthenticationResponse> refreshToken(@RequestBody RefreshTokenRequest request)
            throws JOSEException, ParseException {
        return ApiResponse.<AuthenticationResponse>builder().result(authenticationService.refreshToken(request))
                .build();
    }

    @PostMapping("/outbound/authentication")
    ApiResponse<AuthenticationResponse> outboundAuthentication(@RequestParam("code") String code) {
        AuthenticationResponse result = authenticationService.outboundAuthentication(code);
        return ApiResponse.<AuthenticationResponse>builder().result(result).build();
    }
}
