package cn.tieling.yibao.interceptor;

import cn.tieling.yibao.dto.response.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 后台管理角色拦截器
 * 仅允许 admin 角色访问 /admin/** 路径
 * 此拦截器在 JwtInterceptor 之后执行（已确保 Token 有效）
 */
@Slf4j
@Component
public class AdminRoleInterceptor implements HandlerInterceptor {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // OPTIONS 预检请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String role = (String) request.getAttribute("role");
        if (!"admin".equals(role)) {
            log.warn("非管理员角色 [{}] 尝试访问后台接口: {}", role, request.getRequestURI());
            writeForbidden(response, "权限不足，仅管理员可访问后台接口");
            return false;
        }
        return true;
    }

    private void writeForbidden(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        Result<Void> result = Result.error(403, message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
