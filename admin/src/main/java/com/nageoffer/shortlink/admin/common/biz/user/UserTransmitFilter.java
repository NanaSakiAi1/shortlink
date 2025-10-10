package com.nageoffer.shortlink.admin.common.biz.user;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import com.nageoffer.shortlink.admin.common.convention.exception.ClientException;
import com.nageoffer.shortlink.admin.common.convention.result.Results;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;

import static com.nageoffer.shortlink.admin.common.constant.RedisCacheConstant.USER_LOGIN_KEY;
import static com.nageoffer.shortlink.admin.common.convention.errorcode.BaseErrorCode.IDEMPOTENT_TOKEN_NULL_ERROR;

/**
 * 用户信息传输过滤器
 */
@RequiredArgsConstructor
@Slf4j
public class UserTransmitFilter implements Filter {

    private final StringRedisTemplate stringRedisTemplate;

    private static final List<String> IGNORE_URI = Lists.newArrayList(
            "/api/short-link/admin/v1/user/login",
            "/api/short-link/admin/v1/user/has-username",
            "/api/short-link/admin/v1/user"
    );

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpReq = (HttpServletRequest) req;
        String requestURI = httpReq.getRequestURI();
        if (!IGNORE_URI.contains(requestURI)) {
            String method = httpReq.getMethod();

            // ========= 放行注册接口：POST /api/short-link/admin/v1/user =========
            if (Objects.equals(requestURI, "/api/short-link/admin/v1/user") && Objects.equals(method, "POST")) {
                chain.doFilter(req, res);
                return;
            } else {
                // ========= 其余请求，执行你原来的鉴权逻辑 =========
                // 既兼容 Authorization 也兼容 token（优先 Authorization）
                String token = httpReq.getHeader("Authorization");
                if (token == null || token.isEmpty()) {
                    token = httpReq.getHeader("token");
                }
                // username 仍然从头里取；如果你想只靠 token 也能找回用户名，见下「可选增强」
                String username = httpReq.getHeader("username");
                log.info("headers username={}, token={}", username, token);
                if (!StrUtil.isAllNotBlank(token, username)) {
                    try {
                        returnJson((HttpServletResponse) res, JSON.toJSONString(Results.failure(new ClientException(IDEMPOTENT_TOKEN_NULL_ERROR))));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    return;
                }

                String key = USER_LOGIN_KEY + username;
                Object userInfoJsonStr;
                try {
                    userInfoJsonStr = stringRedisTemplate.opsForHash().get(key, token);
                    if (userInfoJsonStr == null) {
                        throw new ClientException(IDEMPOTENT_TOKEN_NULL_ERROR);
                    }

                } catch (Exception e) {
                    try {
                        returnJson((HttpServletResponse) res, JSON.toJSONString(Results.failure(new ClientException(IDEMPOTENT_TOKEN_NULL_ERROR))));
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                    return;
                }
                // 将 Redis 里的 JSON 解析成用户信息对象
                UserInfoDTO userInfoDTO = JSON.parseObject(userInfoJsonStr.toString(), UserInfoDTO.class);

                // ✅ Redis 里没存 token，要手动补回来
                userInfoDTO.setToken(token);

                // 存入 ThreadLocal
                UserContext.setUser(userInfoDTO);
            }
        }
        try {
            chain.doFilter(req, res);
        } finally {
            UserContext.removeUser();
        }
    }
    private void returnJson(HttpServletResponse response, String json) throws Exception {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        PrintWriter writer = response.getWriter();
        writer.write(json);
        writer.flush();
        writer.close();
    }
}


