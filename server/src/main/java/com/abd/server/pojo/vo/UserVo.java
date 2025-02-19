package com.abd.server.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVo {

    private Long id;

    private String username;

    private String oldPwd;

    private String newPwd;

    private String email;

    private String tel;

    private List<String> authorities;

}
