package cn.tieling.yibao.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 政策法规实体
 */
@Data
@Entity
@Table(name = "policy")
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 政策标题 */
    @Column(nullable = false, length = 256)
    private String title;

    /** 发布机构 */
    @Column(nullable = false, length = 128)
    private String issuer;

    /** 文号 */
    @Column(name = "doc_no", length = 128)
    private String docNo;

    /**
     * 级别：national-国家级 provincial-省级 municipal-市级
     */
    @Column(nullable = false, length = 32)
    private String level;

    /** 摘要 */
    @Column(length = 1024)
    private String summary;

    /** 正文内容 */
    @Column(columnDefinition = "TEXT")
    private String content;

    /** 附件URL */
    @Column(name = "attachment_url", length = 512)
    private String attachmentUrl;

    /** 标签（逗号分隔） */
    @Column(length = 256)
    private String tags;

    /** 发布日期 */
    @Column(name = "publish_date")
    private LocalDate publishDate;

    /** 排序权重（越大越靠前） */
    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    /** 状态：1-发布 0-草稿 */
    @Column(nullable = false)
    private Integer status = 1;

    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}
