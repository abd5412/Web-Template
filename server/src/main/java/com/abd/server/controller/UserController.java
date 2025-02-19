package com.abd.server.controller;

import com.abd.server.pojo.ResponseResult;
import com.abd.server.pojo.User;
import com.abd.server.pojo.vo.UserVo;
import com.abd.server.services.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    UserService userService;

    @PostMapping("/login")
    public ResponseResult login(@RequestBody User user) {
        return userService.login(user);
    }

    @GetMapping("/test")
    public ResponseResult test() {
        return new ResponseResult(200,"鉴权成功",null);
    }

    @GetMapping("/getUserInfo")
    public ResponseResult getUserInfo() {
        return userService.getUserInfo();
    }

    @GetMapping("/logout")
    public ResponseResult logout() {
        return userService.logout();
    }

    @PostMapping("/resetPassword")
    public ResponseResult resetPassword(@RequestBody UserVo vo) {
        return userService.resetPassword(vo);
    }

    @GetMapping("/userPage")
    public ResponseResult getUserList(Page page,UserVo param) {
        return userService.getUserList(page,param);
    }
}
