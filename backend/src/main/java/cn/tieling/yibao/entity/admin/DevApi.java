package cn.tieling.yibao.entity.admin;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 开发者中心 API 文档条目
 */
@Data
@Entity
@Table(name = "dev_api")
public class DevApi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** API名称 */
    @Column(nullable = false, length = 128)
    private String name;

    /** API分类（auth/imaging/recognition/institution） */
    @Column(nullable = false, length = 32)
    private String category;

    /** HTTP方法 */
    @Column(nullable = false, length = 16)
    private String method;

    /** 接口路径 */
    @Column(nullable = false, length = 256)
    private String path;

    /** 接口描述 */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** 请求参数示例（JSON） */
    @Column(name = "request_example", columnDefinition = "TEXT")
    private String requestExample;

    /** 响应示例（JSON） */
    @Column(name = "response_example", columnDefinition = "TEXT")
    private String responseExample;

    /** 是否需要认证 */
    @Column(name = "need_auth", nullable = false)
    private Boolean needAuth = true;

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
