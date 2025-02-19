package com.abd.server.exception;

import com.abd.server.pojo.R;
import com.abd.server.pojo.sysEnum.HttpStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public R handleCustomPasswordException(CustomException ex) {
        log.error(ex.getMessage());
        return R.failed(ex.getMessage(),null);
    }

    @ExceptionHandler(TokenException.class)
    public R handleTokenException(TokenException ex) {
        log.error(ex.getLocalizedMessage());
        return R.failed(HttpStatus.UNAUTHORIZED,ex.getMessage());
    }
}
