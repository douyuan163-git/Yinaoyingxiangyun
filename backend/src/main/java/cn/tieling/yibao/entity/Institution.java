package cn.tieling.yibao.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 医疗机构实体
 */
@Data
@Entity
@Table(name = "institution")
public class Institution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 机构名称 */
    @Column(nullable = false, length = 128)
    private String name;

    /** 机构等级：三级甲等/三级乙等/二级甲等等 */
    @Column(length = 32)
    private String level;

    /** 机构类型：综合医院/中医医院等 */
    @Column(length = 64)
    private String type;

    /** 所在区县 */
    @Column(length = 64)
    private String district;

    /** 详细地址 */
    @Column(length = 256)
    private String address;

    /** 联系电话 */
    @Column(length = 32)
    private String phone;

    /** 接入日期 */
    @Column(name = "join_date")
    private LocalDate joinDate;

    /**
     * 接入状态：running-运行中 testing-测试中 pending-待接入
     */
    @Column(nullable = false, length = 32)
    private String status = "running";

    /** 互认检查项目（逗号分隔，如CT,MRI,DR） */
    @Column(name = "recognition_items", length = 256)
    private String recognitionItems;

    /** 是否支持互认：1-是 0-否 */
    @Column(name = "support_recognition")
    private Integer supportRecognition = 1;

    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}
