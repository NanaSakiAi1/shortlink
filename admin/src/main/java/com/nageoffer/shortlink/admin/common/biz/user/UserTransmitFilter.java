package com.nageoffer.shortlink.admin.common.biz.user;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;

/**
 * 用户信息传输过滤器
 */
@RequiredArgsConstructor
@Slf4j
public class UserTransmitFilter implements Filter {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpReq = (HttpServletRequest) req;

        // 既兼容 Authorization 也兼容 token（优先 Authorization）
        String token = httpReq.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            token = httpReq.getHeader("token");
        }

        // username 仍然从头里取；如果你想只靠 token 也能找回用户名，见下「可选增强」
        String username = httpReq.getHeader("username");
        log.info("headers username={}, token={}", username, token);

        try {
            // 任何一个为空都不查 Redis，避免 HGET 传 null 直接抛错
            if (username != null && !username.isEmpty()
                    && token != null && !token.isEmpty()) {

                String key = "login_" + username;
                Object userInfoJsonStr = stringRedisTemplate.opsForHash().get(key, token);
                if (userInfoJsonStr != null) {
                    // 这里你可以反序列化完整用户，或只放用户名都行
                    // UserInfoDTO dto = JSON.parseObject(userInfoJsonStr.toString(), UserInfoDTO.class);
                    // UserContext.setUser(dto);
                    UserContext.setUser(new UserInfoDTO(null, username, null));
                } else {
                    log.info("no session found in redis for key={}, token={}", key, token);
                }
            }

            chain.doFilter(req, res);
        } finally {
            UserContext.removeUser();
        }
    }
}

