package com.zheng.service.impl;

import com.zheng.dao.UserMapper;
import com.zheng.pojo.User;
import com.zheng.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: ZhengTianLiang
 * @date: 2022/08/16  17:04
 * @desc:
 */

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> userList() {
        return userMapper.userList();
    }
}
