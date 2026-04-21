package com.hnkjzy.ecg_collection.model.vo.analysis;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AI diagnosis dashboard page item.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AiDiagnosisDashboardPageItemVo extends BaseVo {

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
    private String aiConclusion;
    private String abnormalType;
    private Integer abnormalCount;
    private BigDecimal confidence;
    private Integer reportStatus;
    private String reportStatusText;
    private Integer warningLevel;
    private String warningLevelText;
    private LocalDateTime diagnosisTime;
}
