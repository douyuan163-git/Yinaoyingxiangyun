package cn.tieling.yibao.entity.admin;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 统一服务门户入口配置（四大角色入口及子功能链接）
 */
@Data
@Entity
@Table(name = "portal_entry")
public class PortalEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 所属角色分组
     * supervisor / hospital / patient / developer
     */
    @Column(nullable = false, length = 32)
    private String roleGroup;

    /** 入口名称 */
    @Column(nullable = false, length = 64)
    private String name;

    /** 入口描述 */
    @Column(length = 256)
    private String description;

    /** 图标（lucide图标名或URL） */
    @Column(length = 128)
    private String icon;

    /** 跳转URL */
    @Column(name = "link_url", length = 512)
    private String linkUrl;

    /** 是否为主入口（每组一个主入口） */
    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary = false;

    /** 排序 */
    @Column(nullable = false)
    private Integer sortOrder = 0;

    /** 状态：1-启用 0-禁用 */
    @Column(nullable = false)
    private Integer status = 1;

    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}
