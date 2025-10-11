package com.nageoffer.shortlink.admin.config;

import com.nageoffer.shortlink.admin.common.biz.user.UserContext;
import com.nageoffer.shortlink.admin.common.biz.user.UserInfoDTO;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class FeignHeaderPropagateConfig {

    @Bean
    public RequestInterceptor authHeaderPropagator() {
        return (RequestTemplate template) -> {
            UserInfoDTO u = UserContext.getUser();
            String token = (u == null) ? null : u.getToken();
            String username = (u == null) ? null : u.getUsername();

            if (StringUtils.isNotBlank(token)) {
                template.header("token", token);
                template.header("Token", token);
                template.header("Authorization", "Bearer " + token);
            }
            if (StringUtils.isNotBlank(username)) {
                template.header("username", username);
                template.header("Username", username);
            }
            log.info("[feign->short-link] 注入完成 -> token={}, username={}", token, username);
        };
    }
}