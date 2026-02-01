package com.gabr.ecommerce.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException{
    private final ErrorCode code;
    public BusinessException(ErrorCode code)
    {
        super(code.name());
        this.code = code ;
    }
}
