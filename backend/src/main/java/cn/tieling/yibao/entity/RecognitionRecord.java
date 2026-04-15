package cn.tieling.yibao.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 检查互认记录实体
 */
@Data
@Entity
@Table(name = "recognition_record")
public class RecognitionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 患者姓名 */
    @Column(name = "patient_name", length = 64)
    private String patientName;

    /** 患者身份证号 */
    @Column(name = "id_card", length = 18)
    private String idCard;

    /** 原检查机构ID */
    @Column(name = "source_institution_id")
    private Long sourceInstitutionId;

    /** 原检查机构名称 */
    @Column(name = "source_institution_name", length = 128)
    private String sourceInstitutionName;

    /** 互认机构ID */
    @Column(name = "target_institution_id")
    private Long targetInstitutionId;

    /** 互认机构名称 */
    @Column(name = "target_institution_name", length = 128)
    private String targetInstitutionName;

    /** 检查项目（CT/MRI/DR/US等） */
    @Column(name = "exam_type", length = 32)
    private String examType;

    /** 检查日期 */
    @Column(name = "exam_date")
    private LocalDate examDate;

    /** 互认日期 */
    @Column(name = "recognition_date")
    private LocalDate recognitionDate;

    /** 状态：recognized-已互认 pending-待互认 */
    @Column(length = 32)
    private String status = "recognized";

    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;
}
