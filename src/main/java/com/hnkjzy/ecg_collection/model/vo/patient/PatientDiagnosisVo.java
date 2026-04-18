package com.hnkjzy.ecg_collection.model.vo.patient;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 患者诊断信息。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PatientDiagnosisVo extends BaseVo {

    private Long reportId;
    private String reportNo;
    private Long recordId;
    private Long aiDiagnosisId;
    private LocalDateTime reportCreateTime;
    private Integer reportStatus;
    private String reportStatusText;
    private String aiConclusion;
    private String doctorDiagnosis;
    private String doctorSuggestion;
    private String reportCreateDoctorName;
    private String auditDoctorName;
    private LocalDateTime auditTime;
    private String auditOpinion;
    private Integer abnormalLevel;
    private String abnormalLevelText;
    private BigDecimal aiConfidence;
}
