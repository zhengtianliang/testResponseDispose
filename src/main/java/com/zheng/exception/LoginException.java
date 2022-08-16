package com.zheng.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: ZhengTianLiang
 * @date: 2022/08/16  10:49
 * @desc:
 */

@Data
@NoArgsConstructor
public class LoginException extends RuntimeException{

    private Integer code;

    private String message;
    public LoginException(Integer code,String message){
        this.code = code;
        this.message = message;
    }
    // 当你不希望你的异常打印出来堆栈信息时，放开此方法即可
//    @Override
//    public Throwable fillInStackTrace() {
//        return this;
//    }
}
