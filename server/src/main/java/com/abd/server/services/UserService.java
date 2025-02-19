package com.abd.server.services;

import com.abd.server.pojo.ResponseResult;
import com.abd.server.pojo.User;
import com.abd.server.pojo.vo.UserVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface UserService {

    ResponseResult login(User user);

    ResponseResult logout();

    ResponseResult getUserInfo();

    ResponseResult resetPassword(UserVo user);

    Page getUserList(Page<User> page, UserVo user);

}
