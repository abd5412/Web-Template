package com.abd.server.filter;

import com.abd.server.exception.CustomException;
import com.abd.server.exception.TokenException;
import com.abd.server.pojo.LoginUser;
import com.abd.server.utils.JwtUtil;
import com.abd.server.utils.RedisCache;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Resource
    private RedisCache redisCache;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //获取token
        String Authorization = request.getHeader("Authorization");
        // 这里先设置放行最后再使用return是因为，如果该请求无含token，那么就对其进行放行，让请求进入下一个拦截器。
        // 后续Security框架还有很多拦截器可以对其进行验证，而使用return是因为后续在进行参数返回的时候，不需要再执行以下代码。
        if (!StringUtils.hasText(Authorization) || !StringUtils.hasText(Authorization.replace("Bearer ", ""))) {
            //放行
            filterChain.doFilter(request, response);
            return;
        }
        String token = Authorization.replace("Bearer ", "");
        //解析token
        String userid;
        try {
            Claims claims = JwtUtil.verifyToken(token);
            userid = claims.getSubject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new TokenException("token非法");
        }
        //从redis中获取用户信息
        String redisKey = "login:" + userid;
        LoginUser loginUser = redisCache.getObject(redisKey);
        if(Objects.isNull(loginUser)){
            throw new TokenException("用户未登录");
        }
        //存入SecurityContextHolder
        //TODO 获取权限信息封装到Authentication中
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginUser,null,loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        //放行
        filterChain.doFilter(request, response);
    }
}
