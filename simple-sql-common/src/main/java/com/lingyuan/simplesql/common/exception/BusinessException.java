package com.lingyuan.simplesql.common.exception;

/**
 * 业务异常类，用于表示业务逻辑中的错误或异常情况。
 */
public class BusinessException extends RuntimeException {

    private final String message;
    private final int code;

    /**
     * 构造函数
     *
     * @param message 错误信息
     * @param code    错误代码
     */
    public BusinessException(String message, int code) {
        this.message = message;
        this.code = code;
    }

    /**
     * 获取错误信息
     *
     * @return 错误信息
     */
    public String getMessage() {
        return message;
    }

    /**
     * 获取错误代码
     *
     * @return 错误代码
     */
    public int getCode() {
        return code;
    }

    public BusinessException(String message) {
        this(message, 500); // 默认错误代码为500
    }
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
        this.code = 500; // 默认错误代码为500
    }

    public BusinessException(Throwable cause) {
        super(cause);
        this.message = cause.getMessage();
        this.code = 500; // 默认错误代码为500
    }
}
