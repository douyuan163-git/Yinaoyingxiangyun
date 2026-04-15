package cn.tieling.yibao.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 系统用户实体
 */
@Data
@Entity
@Table(name = "sys_user")
public class SysUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 用户名/工号 */
    @Column(nullable = false, unique = true, length = 64)
    private String username;

    /** 密码（BCrypt加密） */
    @Column(nullable = false, length = 128)
    private String password;

    /** 真实姓名 */
    @Column(name = "real_name", length = 64)
    private String realName;

    /** 手机号 */
    @Column(length = 20)
    private String phone;

    /**
     * 角色类型
     * admin      - 系统管理员（后台CMS）
     * supervisor - 医保监管部门
     * hospital   - 医疗机构
     * patient    - 参保人员
     * developer  - 系统开发者
     */
    @Column(nullable = false, length = 32)
    private String role;

    /** 所属机构ID（医疗机构用户关联） */
    @Column(name = "org_id")
    private Long orgId;

    /** 身份证号（参保人员） */
    @Column(name = "id_card", length = 18)
    private String idCard;

    /** 医保卡号（参保人员） */
    @Column(name = "insurance_no", length = 32)
    private String insuranceNo;

    /** 备注 */
    @Column(length = 256)
    private String remark;

    /** 状态：1-正常 0-禁用 */
    @Column(nullable = false)
    private Integer status = 1;

    /** 最后登录时间 */
    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;

    /** 创建时间 */
    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    /** 更新时间 */
    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}
