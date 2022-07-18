package com.zheng.encry;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: ZhengTianLiang
 * @date: 2022/07/18  15:40
 * @desc:
 */
public class MD5Util {
    private static final Logger logger = LoggerFactory.getLogger(MD5Util.class);

    public MD5Util() {
    }

    public static final String MD5(String s) {
        if (s != null && !"".equals(s)) {
            char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
            byte[] btInput = s.getBytes();

            try {
                MessageDigest mdInst = MessageDigest.getInstance("MD5");
                mdInst.update(btInput);
                byte[] md = mdInst.digest();
                int j = md.length;
                char[] str = new char[j * 2];
                int k = 0;

                for(int i = 0; i < j; ++i) {
                    byte byte0 = md[i];
                    str[k++] = hexDigits[byte0 >>> 4 & 15];
                    str[k++] = hexDigits[byte0 & 15];
                }

                return new String(str);
            } catch (NoSuchAlgorithmException var10) {
                logger.error("no md5 algo error", var10);
                return null;
            }
        } else {
            return "";
        }
    }

    public static final String lowercaseMD5(String s) {
        return MD5(s).toLowerCase();
    }
}

