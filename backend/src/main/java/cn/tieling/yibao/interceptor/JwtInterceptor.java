package cn.tieling.yibao.interceptor;

import cn.tieling.yibao.dto.response.Result;
import cn.tieling.yibao.util.JwtUtil;
import cn.tieling.yibao.util.RedisUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * JWT Token 鉴权拦截器
 * 白名单路径不进行 Token 校验
 */
@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${sso.whitelist}")
    private List<String> whitelist;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestPath = request.getRequestURI();
        // 去掉 context-path 前缀
        String contextPath = request.getContextPath();
        if (requestPath.startsWith(contextPath)) {
            requestPath = requestPath.substring(contextPath.length());
        }

        // OPTIONS 预检请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 白名单路径放行
        for (String pattern : whitelist) {
            if (pathMatcher.match(pattern, requestPath)) {
                log.debug("白名单路径放行: {}", requestPath);
                return true;
            }
        }

        // 获取 Token
        String token = extractToken(request);
        if (token == null || token.isEmpty()) {
            writeUnauthorized(response, "请求头中缺少 Authorization Token");
            return false;
        }

        // 验证 Token 格式和签名
        if (!jwtUtil.validateToken(token)) {
            writeUnauthorized(response, "Token 无效或已过期，请重新登录");
            return false;
        }

        // 检查 Token 是否在黑名单（已登出）
        if (redisUtil.isBlacklisted(token)) {
            writeUnauthorized(response, "Token 已失效，请重新登录");
            return false;
        }

        // 将用户信息存入 request，供后续 Controller 使用
        request.setAttribute("userId", jwtUtil.getUserIdFromToken(token));
        request.setAttribute("username", jwtUtil.getUsernameFromToken(token));
        request.setAttribute("role", jwtUtil.getRoleFromToken(token));
        request.setAttribute("token", token);

        return true;
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7).trim();
        }
        return null;
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        Result<Void> result = Result.error(401, message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
