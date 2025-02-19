package com.abd.server.services;

import com.abd.server.pojo.R;
import com.abd.server.pojo.User;
import com.abd.server.pojo.vo.UserVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface UserService {

    R login(User user);

    R logout();

    R getUserInfo();

    R resetPassword(UserVo user);

    R getUserList(Page<User> page, UserVo user);

    R editUser(UserVo vo);

}
