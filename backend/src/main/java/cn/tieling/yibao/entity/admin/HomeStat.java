package cn.tieling.yibao.entity.admin;

import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 首页统计数字配置（接入机构数、影像数量、互认次数等）
 */
@Data
@Entity
@Table(name = "home_stat")
public class HomeStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 指标标签 */
    @Column(nullable = false, length = 64)
    private String label;

    /** 数值 */
    @Column(nullable = false)
    private Long value;

    /** 单位后缀（家、万张、次等） */
    @Column(length = 16)
    private String suffix;

    /** 颜色标识（CSS类名或颜色值） */
    @Column(length = 64)
    private String color;

    /** 排序 */
    @Column(nullable = false)
    private Integer sortOrder = 0;

    /** 状态：1-启用 0-禁用 */
    @Column(nullable = false)
    private Integer status = 1;

    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}
