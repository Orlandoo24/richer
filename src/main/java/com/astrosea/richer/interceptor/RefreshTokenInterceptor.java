package com.astrosea.richer.interceptor;


import cn.hutool.core.util.StrUtil;
import com.astrosea.richer.constant.HttpCode;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public class RefreshTokenInterceptor extends HandlerInterceptorAdapter {



    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        //先从请求头(K-V)里拿 token
        String token = request.getHeader("token");



        //返回 true 放行
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception
    {

    }

}