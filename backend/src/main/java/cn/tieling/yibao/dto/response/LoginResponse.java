package cn.tieling.yibao.dto.response;

import lombok.Builder;
import lombok.Data;

/**
 * 登录响应 DTO
 */
@Data
@Builder
public class LoginResponse {

    /** 访问 Token */
    private String accessToken;

    /** 刷新 Token */
    private String refreshToken;

    /** Token 类型 */
    private String tokenType = "Bearer";

    /** 过期时间（秒） */
    private Long expiresIn;

    /** 用户ID */
    private Long userId;

    /** 用户名 */
    private String username;

    /** 真实姓名 */
    private String realName;

    /** 角色 */
    private String role;

    /** 角色名称 */
    private String roleName;
}
