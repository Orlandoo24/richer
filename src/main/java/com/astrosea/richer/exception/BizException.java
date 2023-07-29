package com.astrosea.richer.exception;


import com.astrosea.richer.constant.RpcCode;

/**
 * 业务逻辑异常 Exception
 */
public final class BizException extends RuntimeException {

    private static final long serialVersionUID = 4462646530730074145L;

    /**
     * 业务错误码
     *
     */
    private Integer code;
    /**
     * 错误提示
     */
    private String message;

    /**
     * 空构造方法，避免反序列化问题
     */
    public BizException() {
    }

    public BizException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

	public BizException(String message) {
        super(message);
		this.code = RpcCode.BIZ_ERROR;
		this.message = message;
	}

    public Integer getCode() {
        return code;
    }

    public BizException setCode(Integer code) {
        this.code = code;
        return this;
    }

	public BizException setMessage(String message) {
		this.message = message;
		return this;
	}

	public String getMessage() {
        return message;
    }

}
