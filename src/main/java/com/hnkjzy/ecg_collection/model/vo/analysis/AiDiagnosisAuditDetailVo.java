package com.hnkjzy.ecg_collection.model.vo.analysis;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * AI diagnosis dashboard audit detail.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AiDiagnosisAuditDetailVo extends BaseVo {

    private String diagnosisId;
    private Long aiDiagnosisId;
    private Long ecgId;
    private String ecgNo;

    private String patientName;
    private String gender;
    private Integer age;
    private String inpatientNo;
    private Long deptId;
    private String deptName;
    private String bedNo;

    private String aiVersion;
    private LocalDateTime diagnosisTime;
    private String aiConclusion;
    private String abnormalType;
    private Integer abnormalCount;
    private Integer abnormalLevel;
    private BigDecimal confidence;

    private Integer heartRate;
    private Integer prInterval;
    private Integer qrsDuration;
    private Integer qtInterval;
    private Integer qtcInterval;

    private Long reportId;
    private String reportNo;
    private Integer reportStatus;
    private String reportStatusText;
    private String doctorConclusion;
    private String doctorSuggestion;
    private String auditDoctorName;
    private LocalDateTime auditTime;
    private String auditOpinion;

    private List<AiDiagnosisAuditWarningVo> warningList;
}
