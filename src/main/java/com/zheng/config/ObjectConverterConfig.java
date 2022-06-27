package com.zheng.config;

import com.alibaba.fastjson.JSON;
import com.zheng.encry.RSAUtil;
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
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author: ZhengTianLiang
 * @date: 2022/06/13  21:39
 * @desc: 当返回的结果是 Object 的时候，(全部的返回实体都加上 这些处理)
 */

@Component  // 这个也是必须的，加入到spring的容器中去初始化
public class ObjectConverterConfig extends AbstractHttpMessageConverter<Object> {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private static final String publicKeyStr;
    private static final String privateKeyStr;

    static {
        //生成RSA公钥和私钥，并Base64编码
        KeyPair keyPair = null;
        try {
            keyPair = RSAUtil.getKeyPair();
        } catch (Exception e) {
            e.printStackTrace();
        }
        publicKeyStr = RSAUtil.getPublicKey(keyPair);
        privateKeyStr = RSAUtil.getPrivateKey(keyPair);
        System.out.println("静态代码块_RSA公钥Base64编码:" + publicKeyStr);
        System.out.println("静态代码块_RSA私钥Base64编码:" + privateKeyStr);
    }


    // 这一步很重要，没有的话，就不会被注册进来，也就不会有作用
    public ObjectConverterConfig(){
        super(new MediaType("application","type-t",Charset.forName("UTF-8")));
    }

    // 这一步也是不可少的，是决定处理哪些方法
    @Override
    protected boolean supports(Class<?> aClass) {
        // 只处理返回值是User对象的方法
//        return User.class.isAssignableFrom(aClass);
        // 只要是响应，统统的进行强化的处理
        return true;
    }

    @Override
    protected Object readInternal(Class<?> aClass, HttpInputMessage httpInputMessage) throws IOException, HttpMessageNotReadableException {
        String json = StreamUtils.copyToString(httpInputMessage.getBody(), Charset.forName("UTF-8"));
        if (StringUtils.isEmpty(json)){
            return null;
        }
        logger.info("readInternal...:"+json);
        String[] split = json.split(",");
        String name = split[0];
        String body = split[0];
        User user = new User(name,body);
        return user;
    }

    // 具体的返回的处理
    @Override
    protected void writeInternal(Object o, HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException {
        String msg = "自定义类型application/type=t，名称："+ JSON.toJSONString(o);
        logger.info("writeInternal...   "+msg);
        // 对返回的结果做一个强化的处理
        msg = msg.concat("牛皮牛皮真的牛皮哇33333");
//        httpOutputMessage.getBody().write(msg.getBytes("UTF-8"));

        // 开始进行一个加密的操作
        try {
            // 将Base64编码后的公钥转换为PublicKey对象
            PublicKey publicKey = RSAUtil.string2PublicKey(publicKeyStr);
            // 用公钥进行加密操作
            byte[] bytes = RSAUtil.publicEncrypt(msg.getBytes(), publicKey);
            // 加密后的内容进行Base64编码
            String s = RSAUtil.byte2Base64(bytes);
            logger.info("加密前的字符串："+msg + "\n加密后的数组的字符串:"+new String(bytes)+"\n加密后的base64编码"+s);
            //加密后的内容Base64解码
            byte[] base642Byte = RSAUtil.base642Byte(s);
            //用私钥解密
            PrivateKey privateKey = RSAUtil.string2PrivateKey(privateKeyStr);
            byte[] privateDecrypt = RSAUtil.privateDecrypt(base642Byte, privateKey);
            logger.error("解密后的内容是："+new String(privateDecrypt));
            httpOutputMessage.getBody().write(s.getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
