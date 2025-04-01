package com.abd.server.controller;

import com.abd.server.pojo.R;
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
    public R login(@RequestBody User user) {
        return userService.login(user);
    }

    @GetMapping("/test")
    public R test() {
        return R.success("鉴权成功");
    }

    @GetMapping("/getUserInfo")
    public R getUserInfo() {
        return userService.getUserInfo();
    }

    @GetMapping("/logout")
    public R logout() {
        return userService.logout();
    }

    @PostMapping("/resetPassword")
    public R resetPassword(@RequestBody UserVo vo) {
        return userService.resetPassword(vo);
    }

    @GetMapping("/userPage")
    public R getUserList(Page page, UserVo param) {
        return userService.getUserList(page,param);
    }

    @PostMapping("/editUser")
    public R editUser(@RequestBody UserVo param) {
        return userService.editUser(param);
    }

    @PostMapping("/addUser")
    public R addUser(@RequestBody UserVo param) {
        return userService.addUser(param);
    }

    @PostMapping("/delUser")
    public R delUser(@RequestBody UserVo param) {
        return userService.delUser(param);
    }

    @PostMapping("/offlineUser")
    public R offlineUser(@RequestBody UserVo param) {
        return userService.offlineUser(param);
    }


}
