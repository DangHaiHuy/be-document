package com.huy.pdoc.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppException extends RuntimeException{

    public AppException(ErrorCode errorCode, String errMessage) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.errMessage=errMessage;
    }

    private ErrorCode errorCode;
    private String errMessage;
}
