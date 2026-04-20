package com.hnkjzy.ecg_collection.model.vo.analysis;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * AI diagnosis detail response.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AiDiagnosisDetailVo extends BaseVo {

    private String diagnosisId;
    private Long aiDiagnosisId;
    private Long ecgId;
    private String ecgNo;

    private Long patientId;
    private String patientName;
    private String gender;
    private Integer age;
    private String hospitalNo;
    private String deptName;

    private String aiVersion;
    private LocalDateTime diagnosisTime;
    private LocalDateTime analysisStartTime;
    private LocalDateTime analysisEndTime;

    private Integer heartRate;
    private Integer prInterval;
    private Integer qrsDuration;
    private Integer qtInterval;
    private Integer qtcInterval;

    private String aiConclusion;
    private String abnormalType;
    private Integer abnormalCount;
    private BigDecimal confidence;

    private Integer leadCount;
    private Integer samplingRate;
    private LocalDateTime collectionStartTime;
    private LocalDateTime collectionEndTime;
    private String rawDataFileUrl;

    private Long reportId;
    private String reportNo;
    private Integer reportStatus;
    private String status;
    private String doctorConclusion;
    private String doctorSuggestion;
    private Long auditDoctorId;
    private String auditDoctorName;
    private LocalDateTime auditTime;
    private String auditOpinion;

    private List<AiDiagnosisAbnormalPointVo> abnormalPointList;
}
