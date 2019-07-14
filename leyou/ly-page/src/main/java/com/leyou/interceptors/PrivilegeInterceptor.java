package com.leyou.interceptors;

import com.leyou.common.auth.entity.AppInfo;
import com.leyou.common.auth.entity.Payload;
import com.leyou.common.auth.utils.JwtUtils;
import com.leyou.gateway.config.JwtProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Slf4j
public class PrivilegeInterceptor implements HandlerInterceptor {
    private JwtProperties props;

    public PrivilegeInterceptor(JwtProperties props) {
        this.props = props;
    }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            String token = request.getHeader(props.getApp().getHeaderName());
            Payload<AppInfo> payload  = JwtUtils.getInfoFromToken(token, props.getPublicKey(), AppInfo.class);
            AppInfo info = payload.getInfo();
            List<Long> targetList = info.getTargetList();
            Long id = props.getApp().getId();
            if (targetList == null || !targetList.contains(id)) {
                log.error("请求者没有访问本服务的权限！");
            }
            log.info("服务{}正在请求资源:{}", info.getServiceName(), request.getRequestURI());
            return true;

        } catch (Exception e) {
            log.error("服务访问被拒绝,token认证失败!", e);
            return false;
        }
    }
 }


