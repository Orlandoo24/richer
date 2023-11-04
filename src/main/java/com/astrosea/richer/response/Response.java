package com.astrosea.richer.response;

import com.astrosea.richer.constant.HttpCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.MDC;

import java.io.Serializable;


@Getter
@Setter
@NoArgsConstructor
@ToString
public class Response<T> implements Serializable {

	private static final long serialVersionUID = 5771204995227939321L;

	private String requestId;

	private Integer code;

	private String message;

	private T data;

	public Response(String requestId, Integer code, String message, T data) {
		this.requestId = requestId;
		this.code = code;
		this.message = message;
		this.data = data;
	}

	public static <T> Response<T> commonRes(Integer code, String message, T data) {
		Response<T> result = new Response<>();
		result.requestId = MDC.get("REQUEST_ID");
		result.code = code;
		result.data = data;
		result.message = message;
		return result;
	}

	public static <T> Response<T> success(T data) {
		Response<T> result = new Response<>();
		result.requestId = MDC.get("REQUEST_ID");
		result.code = HttpCode.SUCCESS;
		result.data = data;
		result.message = "success";
		return result;
	}

	public static <T> Response<T> successMsg(String message) {
		Response<T> result = new Response<>();
		result.requestId = MDC.get("REQUEST_ID");
		result.code = HttpCode.SUCCESS;
		result.message = message;
		return result;
	}

	public static <T> Response<T> successMsg(T data, String message) {
		Response<T> result = new Response<>();
		result.requestId = MDC.get("REQUEST_ID");
		result.code = HttpCode.SUCCESS;
		result.data = data;
		result.message = message;
		return result;
	}

	public static <T> Response<T> success() {
		Response<T> result = new Response<>();
		result.requestId = MDC.get("REQUEST_ID");
		result.code = HttpCode.SUCCESS;
		result.data = null;
		result.message = "success";
		return result;
	}

	public static <T> Response<T> error(Integer code, String message) {
		Response<T> result = new Response<>();
		result.requestId = MDC.get("REQUEST_ID");
		result.code = code;
		result.message = message;
		return result;
	}

	public boolean isSuccess() {
		return this.code == HttpCode.SUCCESS;
	}

}
