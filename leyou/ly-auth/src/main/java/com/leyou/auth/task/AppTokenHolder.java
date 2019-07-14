package com.leyou.auth.task;

import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.mapper.ApplicationInfoMapper;
import com.leyou.common.auth.entity.AppInfo;
import com.leyou.common.auth.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@EnableConfigurationProperties(JwtProperties.class)
public class AppTokenHolder {
    @Autowired
    private  JwtProperties props;
    @Autowired
    private ApplicationInfoMapper infoMapper;

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
                 List<Long> idList = infoMapper.queryTargetIdList(props.getApp().getId());
                 AppInfo appInfo = new AppInfo();
                 appInfo.setId(props.getApp().getId());
                 appInfo.setServiceName(props.getApp().getSecret());
                 appInfo.setTargetList(idList);
                 this.token = JwtUtils.generateTokenExpireInMinutes(appInfo, props.getPrivateKey(), props.getApp().getExpire());

                 log.info("【auth服务】申请token成功！");
                 break;
             } catch (Exception e) {
                 log.info("【auth服务】定时获取token失败");
             }
           Thread.sleep(TOKEN_RETRY_INTERVAL);
         }


    }
    public String getToken(){
        return token;
    }
}
