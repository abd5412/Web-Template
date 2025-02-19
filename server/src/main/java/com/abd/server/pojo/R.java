package com.abd.server.pojo;

import com.abd.server.pojo.sysEnum.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class R {

    private int code;

    private String msg;

    private Object data;

    public static R success() {
        HttpStatus ok = HttpStatus.OK;
        return new R(ok.getCode(), ok.getMessage(), null);
    }

    public static R success(Object data) {
        HttpStatus ok = HttpStatus.OK;
        return new R(ok.getCode(), ok.getMessage(), data);
    }

    public static R success(String msg, Object data) {
        HttpStatus ok = HttpStatus.OK;
        return new R(ok.getCode(), msg, data);
    }

    public static R success(String msg) {
        HttpStatus ok = HttpStatus.OK;
        return new R(ok.getCode(), msg, null);
    }

    public static R failed() {
        HttpStatus internalServerError = HttpStatus.INTERNAL_SERVER_ERROR;
        return new R(internalServerError.getCode(), internalServerError.getMessage(), null);
    }

    public static R failed(Object data) {
        HttpStatus internalServerError = HttpStatus.INTERNAL_SERVER_ERROR;
        return new R(internalServerError.getCode(), internalServerError.getMessage(), data);
    }

    public static R failed(String msg) {
        HttpStatus internalServerError = HttpStatus.INTERNAL_SERVER_ERROR;
        return new R(internalServerError.getCode(), msg, null);
    }

    public static R failed(String msg, Object data) {
        HttpStatus internalServerError = HttpStatus.INTERNAL_SERVER_ERROR;
        return new R(internalServerError.getCode(), msg, data);
    }

    public static R failed(HttpStatus status) {
        return new R(status.getCode(), status.getMessage(), null);
    }

    public static R failed(HttpStatus status,String msg) {
        return new R(status.getCode(), msg, null);
    }

}
