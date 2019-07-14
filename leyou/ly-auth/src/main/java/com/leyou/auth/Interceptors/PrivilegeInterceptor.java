package com.leyou.auth.Interceptors;

import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.task.AppTokenHolder;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
public class PrivilegeInterceptor implements RequestInterceptor {
    @Autowired
    private AppTokenHolder tokenHolder;
    @Autowired
    private  JwtProperties props;
    @Override
    public void apply(RequestTemplate requestTemplate) {
        String token = tokenHolder.getToken();
        requestTemplate.header(props.getApp().getHeaderName(),token);
    }
}
