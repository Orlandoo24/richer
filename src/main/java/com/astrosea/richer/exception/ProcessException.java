package com.astrosea.richer.exception;

import com.astrosea.richer.constant.RpcCode;

/**
 * 流程处理异常 Exception
 */
public final class ProcessException extends RuntimeException {

    private static final long serialVersionUID = 3771622198365372253L;

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
    public ProcessException() {
    }

    public ProcessException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

	public ProcessException(String message) {
        super(message);
        this.code = RpcCode.BIZ_ERROR;
		this.message = message;
	}

    public Integer getCode() {
        return code;
    }

    public ProcessException setCode(Integer code) {
        this.code = code;
        return this;
    }

	public ProcessException setMessage(String message) {
		this.message = message;
		return this;
	}

	public String getMessage() {
        return message;
    }

}
