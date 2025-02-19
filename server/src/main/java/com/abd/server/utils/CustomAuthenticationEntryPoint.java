package com.abd.server.utils;

import com.abd.server.pojo.R;
import com.abd.server.pojo.sysEnum.HttpStatus;
import com.alibaba.fastjson.JSON;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// 身份验证异常
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String requestURI = request.getRequestURI();
        String msg = String.format("请求访问 %s,认证失败，登录失败 ", requestURI);
        response.setStatus(200);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
//        R responseResult = new R();
//        responseResult.setCode(310);
//        responseResult.setMsg(msg);
        response.getWriter().write(JSON.toJSONString(R.failed(HttpStatus.UNAUTHORIZED)));
    }
}
