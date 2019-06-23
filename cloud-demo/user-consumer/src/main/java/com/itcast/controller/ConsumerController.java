package com.itcast.controller;


import com.itcast.client.Userclient;
import com.itcast.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("consumer")
public class ConsumerController {
 @Autowired
 private Userclient userClient;

    @GetMapping("{id}")

    public User queryById(@PathVariable("id")Long id){

      return  userClient.queryUserById(id);
    }

}
