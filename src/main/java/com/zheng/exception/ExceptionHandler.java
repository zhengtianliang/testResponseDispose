package com.zheng.exception;

import com.zheng.exception.LoginException;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author: ZhengTianLiang
 * @date: 2022/08/16  10:46
 * @desc:
 */

@RestControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(value = LoginException.class)
    public GlobleException handlerException(LoginException e){
        System.out.println("进去了LoginException方法");
        GlobleException globleException = new GlobleException(-1,"每5s只能点击一次o");
        return globleException;
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = Exception.class)
    public GlobleException handlerException(Exception e){
        System.out.println("进去了Exception方法");
        GlobleException globleException = new GlobleException(-1,"系统异常");
        return globleException;
    }
}
