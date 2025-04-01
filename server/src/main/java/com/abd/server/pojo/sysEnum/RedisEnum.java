package com.abd.server.pojo.sysEnum;

public enum RedisEnum {
    LOGIN("login:");

    private String key;

    RedisEnum(String key) {
        this.key = key;
    }
    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return key;
    }
}
