package com.astrosea.richer.interceptor;


import com.astrosea.richer.constant.HttpCode;
import com.astrosea.richer.exception.BizException;
import com.astrosea.richer.exception.PubPayException;
import com.astrosea.richer.exception.RpcException;
import com.astrosea.richer.exception.TokenException;
import com.astrosea.richer.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

@Slf4j
@RestControllerAdvice(basePackages = {"com.astrosea.controller"})
public class JiaRuExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(JiaRuExceptionHandler.class);


    /**
     * 登陆异常处理
     *
     * 没有token、token 过期、token 不合法
     *
     * @param e 未知异常
     * @return Response
     */
    @ExceptionHandler(TokenException.class)
    public Response<?> tokenException(TokenException e) {
        return Response.error(e.getCode(), e.getMessage());
    }


    /**
     * 业务逻辑异常处理
     *
     * @param e 业务逻辑异常
     * @return Response
     */
    @ExceptionHandler(BizException.class)
    public Response<?> handleBizException(BizException e) {
        return Response.error(e.getCode(), e.getMessage());
    }

    /**
     * 公售处理异常处理
     *
     * @param e 流程处理异常
     * @return Response
     */
    @ExceptionHandler(PubPayException.class)
    public Response<?> handleProcessException(PubPayException e) {
        return Response.error(e.getCode(), e.getMessage());
    }

    /**
     * RPC服务异常处理
     *
     * @param e RPC服务异常
     * @return Response
     */
    @ExceptionHandler(RpcException.class)
    public Response<?> handleRpcException(RpcException e) {
        return Response.error(e.getCode(), e.getMessage());
    }


    /**
     * 处理SQL异常
     *
     * @param e SQL异常
     * @return Response
     */
    @ExceptionHandler(SQLException.class)
    public Response<?> handleSQLException(SQLException e) {
        log.error("SQL异常：", e);
        return Response.error(HttpCode.MYSQL_ERROR_5001, "数据库操作异常：" + e.getMessage());
    }


    /**
     * 未知异常处理
     *
     * @param e 未知异常
     * @return Response
     */
    @ExceptionHandler(Exception.class)
    public Response<?> handleUnknownException(Exception e) {
        log.info("系统异常日志：{}", e);
        return Response.error(HttpCode.INNER_ERROR_5000 , "系统异常"+e);
    }



}