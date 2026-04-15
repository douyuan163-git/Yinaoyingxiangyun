package cn.tieling.yibao.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 新闻动态实体
 */
@Data
@Entity
@Table(name = "news")
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 标题 */
    @Column(nullable = false, length = 256)
    private String title;

    /**
     * 分类：notice-平台公告 dynamic-医保动态 policy-政策解读 industry-行业资讯
     */
    @Column(nullable = false, length = 32)
    private String category;

    /** 摘要 */
    @Column(length = 512)
    private String excerpt;

    /** 正文 */
    @Column(columnDefinition = "TEXT")
    private String content;

    /** 封面图URL */
    @Column(name = "cover_url", length = 512)
    private String coverUrl;

    /** 作者/来源 */
    @Column(length = 64)
    private String author;

    /** 发布日期 */
    @Column(name = "publish_date")
    private LocalDate publishDate;

    /** 是否置顶：1-是 0-否 */
    @Column(name = "is_top")
    private Integer isTop = 0;

    /** 浏览次数 */
    @Column(name = "view_count")
    private Integer viewCount = 0;

    /** 排序权重 */
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
