package com.abd.server.services.impl;

import com.abd.server.exception.CustomException;
import com.abd.server.mapper.UserMapper;
import com.abd.server.pojo.LoginUser;
import com.abd.server.pojo.R;
import com.abd.server.pojo.User;
import com.abd.server.pojo.sysEnum.HttpStatus;
import com.abd.server.pojo.vo.UserVo;
import com.abd.server.services.UserService;
import com.abd.server.utils.JwtUtil;
import com.abd.server.utils.RedisCache;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class LoginServiceImpl implements UserService {

    @Resource
    private AuthenticationManager authenticationManager;


    @Resource
    private RedisCache redisCache;
    @Resource
    private UserMapper userMapper;

    @Override
    public R login(User user) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
        try {
            Authentication authenticate = authenticationManager.authenticate(authenticationToken);
            // 如果认证通过了
            log.info(user.toString());
            LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
            String userid = loginUser.getUser().getUsername();
            String jwt = JwtUtil.generateToken(userid);
            Map<String, String> map = new HashMap<>();
            map.put("token", jwt);
            // 把完整的用户信息存入redis，userid作为key
            redisCache.saveObject("login:" + userid, loginUser);
            return R.success("登录成功", map);
        } catch (AuthenticationException e) {
            // 如果认证没通过，给出对应的提示
            throw new RuntimeException("登录失败", e);
        }
    }

    // 这里并不需要删除SecurityContextHolder中的信息，只需要删除redis中所存储的即可，因为在进行认证的时候，需要先在SecurityContextHolder中拿到信息后，再从redis中获取对应信息。
    @Override
    public R logout() {
        //获取SecurityContextHolder中的用户id
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        String userid = loginUser.getUser().getUsername();
        //删除redis中的值
        redisCache.remove("login:" + userid);
        return R.success("注销成功");
    }

    @Override
    public R getUserInfo() {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        String userid = loginUser.getUser().getUsername();
        LoginUser userInfo = redisCache.getObject("login:" + userid);
        userInfo.getUser().setPassword("***");
        return R.success( "获取成功", userInfo);
    }

    @Override
    public R resetPassword(UserVo user) {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        String userid = loginUser.getUser().getUsername();
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        if (!bCryptPasswordEncoder.matches(user.getOldPwd(), loginUser.getPassword())) {
            throw new CustomException(("原密码错误"));
        }
        userMapper.update(null, Wrappers.lambdaUpdate(User.class)
                .eq(User::getUsername, userid)
                .set(User::getPassword, bCryptPasswordEncoder.encode(user.getNewPwd())));
        return R.success("修改成功");
    }

    @Override
    public R getUserList(Page<User> page, UserVo vo) {
//        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<>();
//        if (Objects.nonNull(user)) {
//            if (StringUtils.isNotBlank(user.getUsername())) {
//                query.like(User::getUsername, user.getUsername());
//            }
//
//        }
        Page<User> userPage = userMapper.selectUsersByPage(page, vo);
        return R.success(userPage);
    }

    @Override
    public R editUser(UserVo vo) {
        return null;
    }

    public static void main(String[] args) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        System.out.println(bCryptPasswordEncoder.encode("123456"));
        System.out.println(bCryptPasswordEncoder.upgradeEncoding("$2a$10$ORiqoqBARNMB/kS0Rj5Hg.LDW16lZvBTx0Tmrju1FPrSDo8sJb3d."));
    }
}
