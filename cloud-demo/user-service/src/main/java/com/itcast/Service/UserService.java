package com.itcast.Service;

import com.itcast.mapper.UserMapper;
import com.itcast.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    public User queryUserById(Long id){
        User user = userMapper.selectByPrimaryKey(id);
        return  user;
    }
}
