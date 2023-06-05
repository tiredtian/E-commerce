package com.asiainfo.mall.service.impl;

import com.asiainfo.mall.model.dao.UserMapper;
import com.asiainfo.mall.model.pojo.User;
import com.asiainfo.mall.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public User getUser() {
        return userMapper.selectByPrimaryKey(1);
    }
}
