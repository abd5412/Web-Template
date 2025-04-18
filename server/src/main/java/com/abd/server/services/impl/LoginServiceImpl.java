package com.abd.server.services.impl;

import com.abd.server.exception.CustomException;
import com.abd.server.mapper.UserMapper;
import com.abd.server.pojo.Authority;
import com.abd.server.pojo.LoginUser;
import com.abd.server.pojo.R;
import com.abd.server.pojo.User;
import com.abd.server.pojo.sysEnum.HttpStatus;
import com.abd.server.pojo.sysEnum.RedisEnum;
import com.abd.server.pojo.vo.UserVo;
import com.abd.server.services.UserService;
import com.abd.server.utils.JwtUtil;
import com.abd.server.utils.LongIdGenerator;
import com.abd.server.utils.RedisCache;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
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
    @Resource
    private LongIdGenerator longIdGenerator;

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
            redisCache.saveObject(RedisEnum.LOGIN + userid, loginUser);
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
        redisCache.remove(RedisEnum.LOGIN + userid);
        return R.success("注销成功");
    }

    @Override
    public R getUserInfo() {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        String userid = loginUser.getUser().getUsername();
        LoginUser userInfo = redisCache.getObject(RedisEnum.LOGIN + userid);
        userInfo.getUser().setPassword("***");
        return R.success("获取成功", userInfo);
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
        Page<User> userPage = userMapper.selectUsersByPage(page, vo);
        for (User user : userPage.getRecords()){
            user.setOnline(redisCache.hasKey(RedisEnum.LOGIN + user.getUsername()));
        }
        return R.success(userPage);
    }

    @Transactional
    @Override
    public R editUser(UserVo vo) {
        if (vo.getUsername() == null) {
            return R.failed(HttpStatus.BAD_REQUEST, "用户名不能为空");
        }
        LambdaUpdateWrapper<User> update = new LambdaUpdateWrapper<>();
        update.eq(User::getUsername, vo.getUsername());
        User user = userMapper.selectOne(update);
        if (user == null){
            return R.failed(HttpStatus.BAD_REQUEST, "用户不存在");
        }
        if (StringUtils.isBlank(vo.getEmail())) {
            return R.failed(HttpStatus.BAD_REQUEST, "邮箱不能为空");
        }
        update.set(User::getEmail, vo.getEmail());
        if (StringUtils.isBlank(vo.getTel())) {
            return R.failed(HttpStatus.BAD_REQUEST, "手机号不能为空");
        } else {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getTel, vo.getTel()).ne(User::getUsername, vo.getUsername());
            if (userMapper.selectOne(queryWrapper) != null) {
                return R.failed(HttpStatus.BAD_REQUEST, "手机号已存在");
            }
        }
        update.set(User::getTel, vo.getTel());
        if (vo.getAuthorities().isEmpty()) {
            return R.failed(HttpStatus.BAD_REQUEST, "权限不能为空");
        }
        userMapper.deleteUserAuthority(user.getId());
        for (String authorityName : vo.getAuthorities()) {
            Authority authority = userMapper.selectAuthoritiesByUserName(authorityName);
            userMapper.insertUserAuthority(user.getId(), authority.getId());
        }
//        if (StringUtils.isNotBlank(vo.getNewPwd())){
//            UserVo userVo = new UserVo();
//            userVo.setId(user.getId());
//            userVo.setNewPwd(vo.getNewPwd());
//            resetPassword(userVo);
//        }
        userMapper.update(null, update);
        return R.success("修改成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R addUser(UserVo vo) {
        User insert = new User();
        if (StringUtils.isBlank(vo.getUsername())) {
            return R.failed(HttpStatus.BAD_REQUEST, "用户名不能为空");
        }else if (userMapper.selectOne(Wrappers.lambdaQuery(User.class).eq(User::getUsername, vo.getUsername())) != null) {
            return R.failed(HttpStatus.BAD_REQUEST, "用户名已存在");
        }
        insert.setUsername(vo.getUsername());
        if (StringUtils.isBlank(vo.getEmail())) {
            return R.failed(HttpStatus.BAD_REQUEST, "邮箱不能为空");
        }
        insert.setEmail(vo.getEmail());
        if (StringUtils.isBlank(vo.getTel())) {
            return R.failed(HttpStatus.BAD_REQUEST, "手机号不能为空");
        } else {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getTel, vo.getTel());
            if (userMapper.selectOne(queryWrapper) != null) {
                return R.failed(HttpStatus.BAD_REQUEST, "手机号已存在");
            }
        }
        insert.setTel(vo.getTel());
        if (StringUtils.isBlank(vo.getNewPwd())){
            return R.failed(HttpStatus.BAD_REQUEST, "密码不能为空");
        }
        insert.setPassword(new BCryptPasswordEncoder().encode(vo.getNewPwd()));
        long id = longIdGenerator.generateId();
        insert.setId(id);
        insert.setCdTime(LocalDateTime.now());
        userMapper.insert(insert);
        if (vo.getAuthorities().isEmpty()) {
            return R.failed(HttpStatus.BAD_REQUEST, "权限不能为空");
        }
        userMapper.deleteUserAuthority(id);
        for (String authorityName : vo.getAuthorities()) {
            Authority authority = userMapper.selectAuthoritiesByUserName(authorityName);
            userMapper.insertUserAuthority(id, authority.getId());
        }
        return R.success("新增成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R delUser(UserVo vo) {
        String username = vo.getUsername();
        User user = userMapper.selectOne(Wrappers.lambdaUpdate(User.class).eq(User::getUsername, username));
        userMapper.deleteUserAuthority(user.getId());
        userMapper.deleteById(user.getId());
        return R.success();
    }

    @Override
    public R offlineUser(UserVo vo) {
        if (redisCache.hasKey(RedisEnum.LOGIN + vo.getUsername())) {
            log.info("用户下线：{}", vo.getUsername());
            redisCache.remove(RedisEnum.LOGIN + vo.getUsername());
            return R.success("下线成功");
        }
        return R.failed();
    }

    public static void main(String[] args) {
//        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
//        System.out.println(bCryptPasswordEncoder.encode("123456"));
//        System.out.println(bCryptPasswordEncoder.upgradeEncoding("$2a$10$ORiqoqBARNMB/kS0Rj5Hg.LDW16lZvBTx0Tmrju1FPrSDo8sJb3d."));
    }
}
