package com.leyou.auth.config;


import com.leyou.common.auth.utils.RsaUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.PrivateKey;
import java.security.PublicKey;

//初始化公共密钥和私有密钥
@Data
@Slf4j
@ConfigurationProperties(prefix = "ly.jwt")
public  class JwtProperties implements InitializingBean {

    private String pubKeyPath;
    private String priKeyPath;
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private  UserTokenProperties user= new UserTokenProperties();
    private  AppTokenProperties app=new AppTokenProperties();
    @Data
    public class AppTokenProperties{
        private  int expire;
        private  Long id;
        private  String secret;
        private  String headerName;
    }
   @Data
   public class UserTokenProperties{
      private  int expire;
      private  String cookieName;
      private  String cookieDomain;
      private  int  cookieExpire;
      private  int  minRefreshInterval;
   }
    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            this.publicKey=RsaUtils.getPublicKey(pubKeyPath);
            this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
        } catch (Exception e) {
            log.error("初始化公钥和私钥失败！", e);
            throw new RuntimeException(e);
        }
    }
}