package com.example.demo.service;

import com.example.demo.mapper.UserMapper;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public void creatOrUpdate(User user) {
        User dbuser = userMapper.findByAccountId(user.getAccountId());
        if(dbuser == null){
            //insert
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);
        }else {
            //update
            dbuser.setAvatarUrl(user.getAvatarUrl());
            dbuser.setToken(user.getToken());
            dbuser.setGmtModified(System.currentTimeMillis());
            dbuser.setName(user.getName());
            userMapper.update(dbuser);
        }
    }
}
