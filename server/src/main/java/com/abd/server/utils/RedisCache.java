package com.abd.server.utils;

import com.abd.server.pojo.LoginUser;
import com.abd.server.pojo.User;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

@Component
public class RedisCache {

    @Resource
    private RedisTemplate<String,LoginUser > redisTemplate;

    public void saveObject(String key, LoginUser value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public LoginUser getObject(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void remove(String key) {
        redisTemplate.delete(key);
    }

    // 查看key是否存在
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
