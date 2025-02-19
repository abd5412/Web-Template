package com.abd.server.exception;

import com.abd.server.pojo.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseResult handleCustomPasswordException(CustomException ex) {
        log.error(ex.getMessage());
        return new ResponseResult(500,ex.getMessage(),null);
    }

    @ExceptionHandler(TokenException.class)
    public ResponseResult handleTokenException(TokenException ex) {
        log.error(ex.getMessage());
        return new ResponseResult(310,ex.getMessage(),null);
    }
}
