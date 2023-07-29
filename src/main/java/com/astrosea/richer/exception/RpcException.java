package com.astrosea.richer.exception;


import com.astrosea.richer.constant.RpcCode;

/**
 * 业务逻辑异常 Exception
 */
public final class RpcException extends RuntimeException {

    private static final long serialVersionUID = 5641293727370683250L;

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
     * 错误明细，内部调试错误
     *
     */
    private String detailMessage;

    /**
     * 空构造方法，避免反序列化问题
     */
    public RpcException() {
    }

    public RpcException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

	public RpcException(String message) {
		this.code = RpcCode.BIZ_ERROR;
		this.message = message;
	}

    public Integer getCode() {
        return code;
    }

    public String getDetailMessage() {
        return detailMessage;
    }

    public RpcException setDetailMessage(String detailMessage) {
        this.detailMessage = detailMessage;
        return this;
    }

    public RpcException setCode(Integer code) {
        this.code = code;
        return this;
    }

	public RpcException setMessage(String message) {
		this.message = message;
		return this;
	}

    @Override
	public String getMessage() {
        return message;
    }

}
