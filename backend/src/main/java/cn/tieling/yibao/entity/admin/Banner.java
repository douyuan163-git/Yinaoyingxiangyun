package cn.tieling.yibao.entity.admin;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 首页Banner/轮播图配置
 */
@Data
@Entity
@Table(name = "banner")
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 标题 */
    @Column(nullable = false, length = 128)
    private String title;

    /** 副标题 */
    @Column(length = 256)
    private String subtitle;

    /** 图片URL */
    @Column(name = "image_url", length = 512)
    private String imageUrl;

    /** 跳转链接 */
    @Column(name = "link_url", length = 512)
    private String linkUrl;

    /** 排序（越小越靠前） */
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

    @Column(name = "create_by", length = 64)
    private String createBy;
}
