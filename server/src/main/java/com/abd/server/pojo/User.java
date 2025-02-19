package com.abd.server.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@NoArgsConstructor
@TableName("dub_user")
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Serializable {

    @TableId
    private Long id;

    private String username;

    private String password;

    private String tel;

    private String email;

    private LocalDateTime cdTime;

    private List<Authority> authorities;

    private LocalDateTime getCdTime() {
        return cdTime;
    }

    private void setCdTime(LocalDateTime cdTime) {
        this.cdTime = cdTime;
    }

    public List<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<Authority> authorities) {
        this.authorities = authorities;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(LoginUser loginUser) {
        this.username = loginUser.getUsername();
        this.password = loginUser.getPassword();
    }
}
