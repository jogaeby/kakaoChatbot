package com.chatbot.base.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    protected String handleMethodArgumentNotValidException(Exception ex) {
        log.error("******* exceptionHandler catch = {} {} *******",ex.getMessage(),ex.getStackTrace());

        return "login";
    }

}
