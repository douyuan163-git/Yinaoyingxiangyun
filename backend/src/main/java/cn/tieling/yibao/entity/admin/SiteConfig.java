package cn.tieling.yibao.entity.admin;

import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 网站配置表（Key-Value 结构，支持所有前台可配置项）
 */
@Data
@Entity
@Table(name = "site_config")
public class SiteConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 配置分组：basic/home/portal/sso */
    @Column(nullable = false, length = 32)
    private String groupKey;

    /** 配置键 */
    @Column(nullable = false, unique = true, length = 128)
    private String configKey;

    /** 配置值（支持JSON字符串） */
    @Column(columnDefinition = "TEXT")
    private String configValue;

    /** 配置说明 */
    @Column(length = 256)
    private String description;

    /** 更新时间 */
    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    /** 更新人 */
    @Column(name = "update_by", length = 64)
    private String updateBy;
}
