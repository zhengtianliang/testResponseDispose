package com.zheng.exception;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: ZhengTianLiang
 * @date: 2022/08/16  10:49
 * @desc:
 */

@Data
@NoArgsConstructor
public class GlobleException {

    private Integer code;
    private String message;

    public GlobleException(Integer code, String message){
        this.code = code;
        this.message = message;
    }
}
