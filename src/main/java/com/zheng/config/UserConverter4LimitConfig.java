package com.zheng.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zheng.encry.MD5Util;
import com.zheng.exception.LoginException;
import com.zheng.pojo.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author: ZhengTianLiang
 * @date: 2022/08/15  21:39
 * @desc: 对请求限流。同一个url，每5s内，只能访问一次
 */

@Component
public class UserConverter4LimitConfig extends AbstractHttpMessageConverter<User> {

    Logger logger =  LoggerFactory.getLogger(getClass());
    // 免5秒url校验的请求url的集合
    final static ArrayList<String> urls = new ArrayList();
    protected ObjectMapper objectMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    static {
        urls.add("/test1"); // test1这个url不会有5s的校验
    }

    // 这一步很重要，没有这一步的话，就不会被注册进来，也就不会有效
    public UserConverter4LimitConfig(){
        super(new MediaType("application","type-t",Charset.forName("UTF-8")));
    }

    @PostConstruct
    public void init(){
        objectMapper = new ObjectMapper();
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
        if (!StringUtils.isEmpty(json)){
            try {
                return decryptRequest(json,aClass);
            } catch (Exception e) {
                e.printStackTrace();
                throw new LoginException(-1,"每5s只能点击一次哦每5s只能点击一次哦");
            }
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

    public User decryptRequest(String requestBody,Class<? extends Object> clazz) throws Exception{
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        String url = request.getRequestURI();
        if (!urls.contains(url)){
            String key = "sys_repost_"+ MD5Util.MD5(url+"_"+requestBody);
            Object o = redisTemplate.opsForValue().get(key);
            if (!Objects.isNull(o)){
                throw new RuntimeException("每5s只能点击一次哦");
            }
            redisTemplate.opsForValue().set(key,"随便写一个key",5, TimeUnit.SECONDS);
        }
        Object o = objectMapper.readValue(requestBody, clazz);
        User u = (User)o;
        return u;
    }

    @Override
    protected boolean canRead(MediaType mediaType) {
//        return super.canRead(mediaType);
        return true;
    }
}
