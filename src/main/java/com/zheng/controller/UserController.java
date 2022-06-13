package com.zheng.controller;

import com.zheng.pojo.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: ZhengTianLiang
 * @date: 2022/06/13  21:30
 * @desc:
 */

@RestController
public class UserController {

    @GetMapping("test1")
    public String test1(){
        System.out.println(123);
        return "mijao";
    }

    @GetMapping("test2")
    public User test2(@RequestBody User user){
        System.out.println(user);
        return user;
    }
}
