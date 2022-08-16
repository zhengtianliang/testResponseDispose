package com.zheng;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(value = "com.zheng.dao")
public class TestreponseencryApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestreponseencryApplication.class, args);
    }

}
