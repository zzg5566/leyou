package com.leyou.gateway.task;

import com.leyou.gateway.clients.AuthClient;
import com.leyou.gateway.config.JwtProperties;
import jdk.nashorn.internal.objects.annotations.Where;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@EnableConfigurationProperties(JwtProperties.class)
public class PrivilegeTokenHolder {
    @Autowired
    private  JwtProperties props;
    @Autowired
    private AuthClient authClient;
    private static final long TOKEN_REFRESH_INTERVAL = 86400000L;

    /**
     * token获取失败后重试的间隔
     */
    private static final long TOKEN_RETRY_INTERVAL = 10000L;
    private  String token;
    @Scheduled(fixedDelay =TOKEN_REFRESH_INTERVAL )
    public void loadToken() throws InterruptedException {
         while(true){

             try {
                 this.token = authClient.authorize(props.getApp().getId(), props.getApp().getSecret());
                 log.info("【网关】定时获取token成功");
                 break;
             } catch (Exception e) {
                 log.info("【网关】定时获取token失败");
             }
           Thread.sleep(TOKEN_RETRY_INTERVAL);
         }


    }
    public String getToken(){
        return token;
    }
}
