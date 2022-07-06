package com.zheng.config;

import com.zheng.pojo.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author: ZhengTianLiang
 * @date: 2022/06/13  21:39
 * @desc: 只对User做响应的加强的处理   (响应结果是其他的，不会被强化)
 */

@Component
public class UserConverterConfig extends AbstractHttpMessageConverter<User> {

    Logger logger =  LoggerFactory.getLogger(getClass());

    // 这一步很重要，没有这一步的话，就不会被注册进来，也就不会有效
    public UserConverterConfig(){
        super(new MediaType("application","type-t",Charset.forName("UTF-8")));
    }

    @Override
    protected boolean supports(Class<?> aClass) {
        // 只处理返回值是user对象的方法
        return User.class.isAssignableFrom(aClass);
    }

    // 这个是读取的时候做的一些处理，此次不动
    @Override
    protected User readInternal(Class<? extends User> aClass, HttpInputMessage httpInputMessage) throws IOException, HttpMessageNotReadableException {
        String json = StreamUtils.copyToString(httpInputMessage.getBody(), Charset.forName("UTF-8"));
        if (StringUtils.isEmpty(json)){
            return null;
        }
        logger.info("readInternal start ... :"+json);
        String[] split = json.split("-");
        String name = split[0];
        String body = split[0];
        User user = new User(name,body);
        return user;
    }

    // 对返回的数据做出强化的处理
    @Override
    protected void writeInternal(User user, HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException {
        String msg = "自定义一个类型application/json 名称："+user.getName()+"内容："+user.getBody();
        logger.info("writeInternal ... start : "+msg);
        msg = msg.concat("此次做出的强化的处理23423423424。");
        httpOutputMessage.getBody().write(msg.getBytes("UTF-8"));
    }
}
