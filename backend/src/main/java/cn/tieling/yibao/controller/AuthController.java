package cn.tieling.yibao.controller;

import cn.tieling.yibao.dto.request.LoginRequest;
import cn.tieling.yibao.dto.request.RefreshTokenRequest;
import cn.tieling.yibao.dto.response.LoginResponse;
import cn.tieling.yibao.dto.response.Result;
import cn.tieling.yibao.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * SSO 统一认证控制器
 * 白名单接口，不进行 Token 鉴权
 */
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 统一登录接口
     * POST /api/auth/login
     * 白名单：不需要 Token
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Validated @RequestBody LoginRequest request) {
        log.info("用户登录请求: username={}, role={}", request.getUsername(), request.getRole());
        LoginResponse response = authService.login(request);
        return Result.success("登录成功", response);
    }

    /**
     * 登出接口
     * POST /api/auth/logout
     */
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        String token = extractToken(request);
        authService.logout(token);
        return Result.success();
    }

    /**
     * 刷新 Token
     * POST /api/auth/refresh
     * 白名单：不需要 Token
     */
    @PostMapping("/refresh")
    public Result<LoginResponse> refresh(@Validated @RequestBody RefreshTokenRequest request) {
        LoginResponse response = authService.refreshToken(request.getRefreshToken());
        return Result.success("Token 刷新成功", response);
    }

    /**
     * 获取 SSO 跳转 Token（用于子系统单点登录）
     * GET /api/auth/sso-token?system=supervisor
     */
    @GetMapping("/sso-token")
    public Result<String> getSsoToken(@RequestParam String system, HttpServletRequest request) {
        String token = extractToken(request);
        String ssoToken = authService.generateSsoToken(token, system);
        return Result.success("SSO Token 生成成功", ssoToken);
    }

    /**
     * 验证 Token 有效性（供子系统调用）
     * GET /api/auth/validate
     */
    @GetMapping("/validate")
    public Result<Object> validateToken(HttpServletRequest request) {
        String token = extractToken(request);
        Object userInfo = authService.validateAndGetUserInfo(token);
        return Result.success("Token 有效", userInfo);
    }

    /**
     * 获取当前用户信息
     * GET /api/auth/me
     */
    @GetMapping("/me")
    public Result<Object> getCurrentUser(HttpServletRequest request) {
        String token = extractToken(request);
        Object userInfo = authService.validateAndGetUserInfo(token);
        return Result.success(userInfo);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
