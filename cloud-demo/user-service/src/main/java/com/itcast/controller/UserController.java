package com.itcast.controller;

import com.itcast.Service.UserService;
import com.itcast.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class UserController {
    @Autowired
    private UserService userService;
    @GetMapping("{id}")
    public User queryUserById(@PathVariable("id")Long id){
        User user = userService.queryUserById(id);
        return  user;
    }
}
