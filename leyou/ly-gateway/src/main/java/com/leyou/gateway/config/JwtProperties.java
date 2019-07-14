package com.leyou.gateway.config;


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
    private PublicKey publicKey;
    private  UserTokenProperties user= new UserTokenProperties();
   @Data
   public class UserTokenProperties{
      private  String cookieName;

   }
    private PrivilegeTokenProperties app = new PrivilegeTokenProperties();
   @Data
   public class PrivilegeTokenProperties{
       private  Long id;
       private  String secret;
       private  String headerName;
   }
    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            this.publicKey=RsaUtils.getPublicKey(pubKeyPath);
        } catch (Exception e) {
            log.error("初始化公钥和私钥失败！", e);
            throw new RuntimeException(e);
        }
    }
}