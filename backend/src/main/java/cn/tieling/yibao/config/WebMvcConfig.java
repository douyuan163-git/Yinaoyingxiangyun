package cn.tieling.yibao.config;

import cn.tieling.yibao.interceptor.AdminRoleInterceptor;
import cn.tieling.yibao.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 * - 跨域 CORS 配置
 * - JWT 拦截器注册（顺序：JWT验证 → Admin角色校验）
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Autowired
    private AdminRoleInterceptor adminRoleInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 1. JWT Token 验证拦截器（全局，白名单除外）
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/auth/login",
                        "/auth/logout",
                        "/auth/refresh",
                        "/public/**",
                        "/actuator/**",
                        "/error"
                )
                .order(1);

        // 2. Admin 角色校验拦截器（仅 /admin/** 路径）
        registry.addInterceptor(adminRoleInterceptor)
                .addPathPatterns("/admin/**")
                .order(2);
    }
}
