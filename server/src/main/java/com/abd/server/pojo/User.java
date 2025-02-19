package com.abd.server.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("dub_user")
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Serializable {

    private Long id;

    private String username;

    private String password;

    private String tel;

    private String email;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime cdTime;

    private List<Authority> authorities;

    public User(LoginUser loginUser) {
        this.username = loginUser.getUsername();
        this.password = loginUser.getPassword();
    }
}
