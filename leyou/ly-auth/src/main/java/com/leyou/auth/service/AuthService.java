package com.leyou.auth.service;

import com.leyou.auth.config.JwtProperties;
import com.leyou.common.auth.entity.AppInfo;
import com.leyou.auth.entity.ApplicationInfo;
import com.leyou.auth.mapper.ApplicationInfoMapper;
import com.leyou.common.auth.entity.Payload;
import com.leyou.common.auth.entity.UserInfo;
import com.leyou.common.auth.utils.JwtUtils;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.CookieUtils;
import com.leyou.dto.UserDTO;
import com.leyou.user.client.UserClient;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class AuthService {
    @Autowired
    private ApplicationInfoMapper appMapper;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private UserClient userClient;
    @Autowired
    private JwtProperties props;
    private static final String USER_ROLE = "role_user";
    public void login(String username, String password, HttpServletResponse response) {
        try {
            UserDTO userDTO = userClient.queryUserByUsernameAndPassword(username, password);
            if (userDTO==null){
                throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
            }
            UserInfo userInfo = new UserInfo(userDTO.getId(), userDTO.getUsername(), USER_ROLE);
            String  token = JwtUtils.generateTokenExpireInMinutes(userInfo, props.getPrivateKey(), props.getUser().getExpire());
            CookieUtils.newBuilder()
                    .response(response)
                    .httpOnly(true)
                    .domain(props.getUser().getCookieDomain())
                    .name(props.getUser().getCookieName()).value(token)
                    .maxAge(props.getUser().getCookieExpire())
                    .build();
        } catch (LyException e) {
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }
    }

    public UserInfo verifyUser(HttpServletRequest request, HttpServletResponse response) {
        try {
            String token = CookieUtils.getCookieValue(request, props.getUser().getCookieName());
            //解析token
            Payload<UserInfo> payload = JwtUtils.getInfoFromToken(token, props.getPublicKey(),UserInfo.class);
            UserInfo info = payload.getInfo();
            String id = payload.getId();
            Boolean aBoolean = redisTemplate.hasKey(id);
            if (aBoolean!=null&&aBoolean){
                throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
            }
            Date expiration = payload.getExpiration();
            DateTime refreshTime = new DateTime(expiration.getTime()).minusMinutes(props.getUser().getMinRefreshInterval());
            if (refreshTime.isBefore(System.currentTimeMillis())){
                String token1 = JwtUtils.generateTokenExpireInMinutes(info, props.getPrivateKey(), props.getUser().getExpire());
                CookieUtils.newBuilder()
                           .response(response)
                           .httpOnly(true)
                           .domain(props.getUser().getCookieDomain())
                           .name(props.getUser().getCookieName())
                           .value(token1)
                           .maxAge(props.getUser().getCookieExpire())
                           .build();
            }
            return  info;
        } catch (Exception e) {
           throw new LyException(ExceptionEnum.NOLOGIN_USERNAME_PASSWORD);
        }

    }
    @Autowired
    private StringRedisTemplate redisTemplate;
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String token = CookieUtils.getCookieValue(request, props.getUser().getCookieName());
        Payload<UserInfo> payload = JwtUtils.getInfoFromToken(token, props.getPublicKey(), UserInfo.class);
        String id = payload.getId();
        Long time=payload.getExpiration().getTime()-System.currentTimeMillis();
        if (time>5000){
          redisTemplate.opsForValue().set(id,"",time, TimeUnit.MINUTES);
        }
         CookieUtils.deleteCookie(props.getUser().getCookieName(),props.getUser().getCookieDomain(),response);
    }

    public String authenticate(Long id, String secret) {
        // 根据id查询应用信息
        ApplicationInfo app = appMapper.selectByPrimaryKey(id);
        // 判断是否为空
        if (app == null) {
            // id不存在，抛出异常
            throw new LyException(ExceptionEnum.INVALID_SERVER_ID_SECRET);
        }
        // 校验密码
        if (!passwordEncoder.matches(secret, app.getSecret())) {
            // 密码错误
            throw new LyException(ExceptionEnum.INVALID_SERVER_ID_SECRET);
        }
        // 查询app的权限信息
        List<Long> idList = appMapper.queryTargetIdList(id);
        // 封装AppInfo
        AppInfo appInfo = new AppInfo();
        appInfo.setId(id);
        appInfo.setServiceName(app.getServiceName());
        appInfo.setTargetList(idList);
        // 生成JWT并返回
        return JwtUtils.generateTokenExpireInMinutes(
                appInfo, props.getPrivateKey(), props.getApp().getExpire());
    }
}
