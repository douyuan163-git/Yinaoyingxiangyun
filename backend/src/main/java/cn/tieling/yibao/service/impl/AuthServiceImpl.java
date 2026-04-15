package cn.tieling.yibao.service.impl;

import cn.tieling.yibao.dto.request.LoginRequest;
import cn.tieling.yibao.dto.response.LoginResponse;
import cn.tieling.yibao.entity.SysUser;
import cn.tieling.yibao.repository.SysUserRepository;
import cn.tieling.yibao.service.AuthService;
import cn.tieling.yibao.util.JwtUtil;
import cn.tieling.yibao.util.RedisUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private SysUserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisUtil redisUtil;

    @Value("${jwt.expiration}")
    private Long expiration;

    private static final Map<String, String> ROLE_NAMES = new HashMap<String, String>() {{
        put("supervisor", "医保监管部门");
        put("hospital", "医疗机构");
        put("patient", "参保人员");
        put("developer", "系统开发者");
    }};

    @Override
    public LoginResponse login(LoginRequest request) {
        // 查找用户（支持用户名、手机号、身份证号、医保卡号）
        Optional<SysUser> userOpt = userRepository.findByUsername(request.getUsername());
        if (!userOpt.isPresent()) {
            userOpt = userRepository.findByPhone(request.getUsername());
        }
        if (!userOpt.isPresent()) {
            userOpt = userRepository.findByIdCard(request.getUsername());
        }
        if (!userOpt.isPresent()) {
            userOpt = userRepository.findByInsuranceNo(request.getUsername());
        }

        SysUser user = userOpt.orElseThrow(() ->
                new RuntimeException("用户不存在，请检查用户名或联系管理员"));

        // 验证密码（实际项目应使用 BCrypt，此处简化）
        if (!verifyPassword(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 验证用户状态
        if (user.getStatus() != 1) {
            throw new RuntimeException("账号已被禁用，请联系管理员");
        }

        // 验证角色匹配
        if (!user.getRole().equals(request.getRole())) {
            throw new RuntimeException("角色类型不匹配，请选择正确的登录角色");
        }

        // 生成 Token
        String accessToken = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getUsername(), user.getRole());

        // 将 Token 存入 Redis（SSO 会话管理）
        Map<String, Object> sessionInfo = new HashMap<>();
        sessionInfo.put("userId", user.getId());
        sessionInfo.put("username", user.getUsername());
        sessionInfo.put("realName", user.getRealName());
        sessionInfo.put("role", user.getRole());
        redisUtil.setToken(accessToken, sessionInfo, expiration / 1000);
        redisUtil.setUserSession(user.getId(), sessionInfo, expiration / 1000);

        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.save(user);

        log.info("用户登录成功: userId={}, username={}, role={}", user.getId(), user.getUsername(), user.getRole());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(expiration / 1000)
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .role(user.getRole())
                .roleName(ROLE_NAMES.getOrDefault(user.getRole(), user.getRole()))
                .build();
    }

    @Override
    public void logout(String token) {
        if (token != null && jwtUtil.validateToken(token)) {
            // 获取 Token 剩余有效期
            Claims claims = jwtUtil.parseToken(token);
            long remainSeconds = (claims.getExpiration().getTime() - System.currentTimeMillis()) / 1000;
            if (remainSeconds > 0) {
                // 加入黑名单
                redisUtil.addToBlacklist(token, remainSeconds);
            }
            // 删除 Redis 中的会话
            redisUtil.deleteToken(token);
            Long userId = jwtUtil.getUserIdFromToken(token);
            redisUtil.delete("sso:user:" + userId);
            log.info("用户登出成功: userId={}", userId);
        }
    }

    @Override
    public LoginResponse refreshToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new RuntimeException("refreshToken 无效或已过期");
        }

        Claims claims = jwtUtil.parseToken(refreshToken);
        String type = claims.get("type", String.class);
        if (!"refresh".equals(type)) {
            throw new RuntimeException("Token 类型错误");
        }

        Long userId = Long.valueOf(claims.get("userId").toString());
        String username = claims.get("username", String.class);
        String role = claims.get("role", String.class);

        SysUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        String newAccessToken = jwtUtil.generateToken(userId, username, role);
        String newRefreshToken = jwtUtil.generateRefreshToken(userId, username, role);

        Map<String, Object> sessionInfo = new HashMap<>();
        sessionInfo.put("userId", userId);
        sessionInfo.put("username", username);
        sessionInfo.put("role", role);
        redisUtil.setToken(newAccessToken, sessionInfo, expiration / 1000);

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(expiration / 1000)
                .userId(userId)
                .username(username)
                .realName(user.getRealName())
                .role(role)
                .roleName(ROLE_NAMES.getOrDefault(role, role))
                .build();
    }

    @Override
    public String generateSsoToken(String token, String targetSystem) {
        if (!jwtUtil.validateToken(token)) {
            throw new RuntimeException("Token 无效");
        }
        Long userId = jwtUtil.getUserIdFromToken(token);
        String username = jwtUtil.getUsernameFromToken(token);
        String role = jwtUtil.getRoleFromToken(token);
        return jwtUtil.generateSsoToken(userId, username, role, targetSystem);
    }

    @Override
    public Object validateAndGetUserInfo(String token) {
        if (token == null || !jwtUtil.validateToken(token)) {
            throw new RuntimeException("Token 无效或已过期");
        }
        if (redisUtil.isBlacklisted(token)) {
            throw new RuntimeException("Token 已失效，请重新登录");
        }
        return redisUtil.getToken(token);
    }

    private boolean verifyPassword(String rawPassword, String encodedPassword) {
        // 简化版密码验证，实际项目请使用 BCryptPasswordEncoder
        // return BCrypt.checkpw(rawPassword, encodedPassword);
        return rawPassword.equals(encodedPassword);
    }
}
