package com.chatbot.base.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ModelAndView handleMethodArgumentNotValidException(Exception e) {
        log.error("******* exceptionHandler catch = {} *******", e.getMessage(), e);
        ModelAndView mav = new ModelAndView("loginError");
        mav.addObject("message", "예상치 못한 오류가 발생하였습니다.");
        mav.setStatus(HttpStatus.BAD_REQUEST);
        return mav;
    }

}
