package com.zheng;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Field;
import java.math.BigDecimal;

@SpringBootTest
class TestreponseencryApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    public void test1(){
        Object o = new Object();
        Class<?> aClass = o.getClass();
        Field[] fields = aClass.getFields();
//        for (Field field : fields){
//            if (field.getType() instanceof BigDecimal){
//
//            }
//        }
    }


}
