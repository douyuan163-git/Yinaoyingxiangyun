package cn.tieling.yibao.service;

import cn.tieling.yibao.dto.request.LoginRequest;
import cn.tieling.yibao.dto.response.LoginResponse;

public interface AuthService {

    /**
     * 用户登录，返回 JWT Token
     */
    LoginResponse login(LoginRequest request);

    /**
     * 登出，将 Token 加入黑名单
     */
    void logout(String token);

    /**
     * 刷新 Token
     */
    LoginResponse refreshToken(String refreshToken);

    /**
     * 生成子系统 SSO 跳转 Token
     */
    String generateSsoToken(String token, String targetSystem);

    /**
     * 验证 Token 并返回用户信息
     */
    Object validateAndGetUserInfo(String token);
}
