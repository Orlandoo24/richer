package com.astrosea.richer.constant;


public class HttpCode {

	public static final int SUCCESS = 200;

	/**
	 * 业务错误
	 */
	public static final int BIZ_ERROR = 1000;

	/** 未登录 */
	public static final int UN_LOGIN = 1001;

	/** 登录过期 */
	public static final int LOGIN_EXPIRED = 1002;

	/**
	 * RPC服务错误
	 */
	public static final int RPC_ERROR = 3000;

	/**
	 * 请求参数不合法
	 */
	public static final int BAD_REQUEST = 4000;

	public static final int INNER_ERROR = 5000;

}
