package com.zheng;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.TimeUnit;

/**
 * @author: ZhengTianLiang
 * @date: 2022/08/12  13:57
 * @desc:
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = com.zheng.TestreponseencryApplication.class) // 这个是项目的启动类
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void test1(){
        System.out.println(123);
        redisTemplate.opsForValue().set("6","6666666",1, TimeUnit.SECONDS);
        try {
            Thread.sleep(1100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Object o = redisTemplate.opsForValue().get("6");
        System.out.println(o);
    }
}
