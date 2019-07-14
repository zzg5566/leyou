package com.leyou.user.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.utils.NumberUtils;
import com.leyou.common.utils.RegexUtils;
import com.leyou.dto.UserDTO;
import com.leyou.user.config.PasswordConfig;
import com.leyou.user.entity.User;
import com.leyou.user.mapper.UserMapper;
import com.mysql.jdbc.TimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.TimeoutUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static com.leyou.common.constants.MQConstants.Exchange.SMS_EXCHANGE_NAME;
import static com.leyou.common.constants.MQConstants.Queue.SMS_VERIFY_CODE_QUEUE;
import static com.leyou.common.constants.MQConstants.RoutingKey.VERIFY_CODE_KEY;

@Service
public class UserService {
    private static final String KEY_PREFIX ="手机号";
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private StringRedisTemplate redisTemplate;
    public Boolean checkUserData(String data, Integer type) {
        User user = new User();
        switch (type){
            case  1 :
                user.setUsername(data);
                break;
            case  2 :
                user.setPhone(data);
                break;
            default:
                throw  new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }
        int count = userMapper.selectCount(user);
        return  count==0;
    }

    public void sendCode(String phone) {
        if (!RegexUtils.isPhone(phone)){
            throw new LyException(ExceptionEnum.INVALID_PHONE_NUMBER);
        }
        String code = NumberUtils.generateCode(6);
         redisTemplate.opsForValue().set(KEY_PREFIX+phone,code,5,TimeUnit.MINUTES );
        HashMap<String,String> map = new HashMap<>();
        map.put("phone",phone);
        map.put("code",code);
        amqpTemplate.convertAndSend(SMS_EXCHANGE_NAME,VERIFY_CODE_KEY,map);
    }
     @Autowired
     private BCryptPasswordEncoder passwordEncoder;
    public void register(User user, String code) {
        String cashCode = redisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());
        if (!StringUtils.equals(code,cashCode)){
            throw new LyException(ExceptionEnum.INVALID_VERIFY_CODE);
        }
        String encode = passwordEncoder.encode(user.getPassword());
        user.setPassword(encode);
        int i = userMapper.insertSelective(user);
        if (i!=1){
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
    }

    public UserDTO queryUserByUsernameAndPassword(String username, String password) {
        User user = new User();
        user.setUsername(username);
        User user1 = userMapper.selectOne(user);
        if (user1==null){
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }
        if (!passwordEncoder.matches(password,user1.getPassword())){
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }
        return BeanHelper.copyProperties(user1,UserDTO.class);
    }
}
