package com.zheng.controller;

import com.zheng.pojo.Role;
import com.zheng.pojo.TestUser;
import com.zheng.pojo.User;
import com.zheng.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.client.RestTemplate;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

/**
 * @author: ZhengTianLiang
 * @date: 2022/06/13  21:30
 * @desc:
 */

@RestController
public class UserController {

    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserService userService;

    @GetMapping("test1")
    public String test1(){
        System.out.println(123);
        redisTemplate.opsForValue().set("1","222");
        System.out.println(redisTemplate.opsForValue().get("1"));
        System.out.println(redisTemplate.opsForValue().get("0"));
        return "mijao";
    }

    @GetMapping("test2")
    public User test2(@RequestBody User user){
        System.out.println(user);
        return user;
    }

    /**
     * @author: ZhengTianLiang
     * @date: 2022/06/12 10:12 AM
     * @desc: 返回结果不是User，不会被强化(只有UserConverterConfig没有ObjectCoverterConfig的时候是这样)
     */
    @GetMapping(value = "/test3")
    public Role test3(@RequestBody Role role){
        logger.info("121113"+role);
        return role;
    }

    /**
     * @author: ZhengTianLiang
     * @date: 2022/06/12 10:12 AM
     * @desc: 返回结果不是User，不会被强化_测试Object的强化
     */
    @GetMapping(value = "/test4")
    public TestUser test4(@RequestBody TestUser user){
        logger.info("333"+user);
        return user;
    }

    /**
     * @author: ZhengTianLiang
     * @date: 2022/06/27 14:04
     * @desc: 测试异常的返回
     */
    @GetMapping(value = "/test5")
    public TestUser test5(@RequestBody TestUser user){
        logger.info("test5"+user);
        if (1 == 1){
            throw new RuntimeException("出错了出错了");
        }
        return user;
    }

    /**
     * @author: ZhengTianLiang
     * @date: 2022/08/16 17:02
     * @desc: 测试service中记录sql的日志
     */
    @GetMapping(value = "/test6")
    public List<User> userList(){
        return userService.userList();
    }

}
