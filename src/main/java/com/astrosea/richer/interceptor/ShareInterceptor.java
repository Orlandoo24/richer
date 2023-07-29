package com.astrosea.richer.interceptor;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ShareInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        //先从请求头(K-V)里拿 token
        String shareHash = request.getHeader("invite_code");

        //具体分享校验的逻辑


        //返回 true 放行
        return true;
    }
}
