package com.huy.pdoc.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CheckOtpRequest {
    private String username;
    private String otpCode;
}
