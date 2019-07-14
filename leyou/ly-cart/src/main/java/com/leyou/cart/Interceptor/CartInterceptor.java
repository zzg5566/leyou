package com.leyou.cart.Interceptor;

import com.leyou.cart.config.JwtProperties;
import com.leyou.common.auth.entity.Payload;
import com.leyou.common.auth.entity.UserInfo;
import com.leyou.common.auth.utils.JwtUtils;
import com.leyou.common.utils.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@EnableConfigurationProperties(JwtProperties.class)
public class CartInterceptor implements HandlerInterceptor {
    @Autowired
    private  JwtProperties props;
    private   static ThreadLocal<UserInfo> tl=new ThreadLocal<>();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = CookieUtils.getCookieValue(request, props.getUser().getCookieName());
        Payload<UserInfo> payload = JwtUtils.getInfoFromToken(token, props.getPublicKey(), UserInfo.class);
        UserInfo info = payload.getInfo();
        tl.set(info);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
            tl.remove();
    }
    public static UserInfo  getUser(){
        return tl.get();
    }
}
