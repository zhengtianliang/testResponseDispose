package com.zheng.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: ZhengTianLiang
 * @date: 2022/06/13  21:30
 * @desc:
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private String name;
    private String body;
}
