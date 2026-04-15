package cn.tieling.yibao.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 登录请求 DTO
 */
@Data
public class LoginRequest {

    /**
     * 用户名/工号/身份证号/医保卡号
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 角色类型：supervisor/hospital/patient/developer
     */
    @NotBlank(message = "角色类型不能为空")
    private String role;
}
