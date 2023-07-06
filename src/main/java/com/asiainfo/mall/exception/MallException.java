package com.asiainfo.mall.exception;

/**
 * 统一异常类
 */
public class MallException extends RuntimeException {
    private final Integer code;
    private final String message;

    public MallException(Integer code, String message) {
        this.code=code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public MallException(MallExceptionEnum mallExceptionEnum) {
        this(mallExceptionEnum.getCode(), mallExceptionEnum.getMsg());
    }
}
