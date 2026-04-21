package com.hnkjzy.ecg_collection.model.vo.analysis;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Analysis-side warning full detail.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AnalysisWarningDetailVo extends BaseVo {

    private Long alertId;
    private LocalDateTime warningTime;
    private Integer alertLevel;
    private String alertLevelText;
    private Integer alertStatus;
    private String alertStatusText;
    private String warningType;
    private String warningDesc;
    private String lisHint;
    private String handleUserName;
    private LocalDateTime handleTime;
    private String handleRemark;

    private Long patientId;
    private String patientName;
    private Integer age;
    private String genderText;
    private String inpatientNo;
    private String wardName;
    private String bedNo;
    private String phone;
    private String primaryDiagnosis;

    private Long recordId;
    private String ecgNo;
    private Long deviceId;
    private String deviceName;
    private Integer leadCount;
    private Integer samplingRate;
    private Integer collectionDuration;
    private LocalDateTime collectionStartTime;
    private LocalDateTime collectionEndTime;
    private String ecgDataFileUrl;
    private String uploadSourceFileUrl;

    private Long aiDiagnosisId;
    private String diagnosisNo;
    private String aiModelVersion;
    private String aiConclusion;
    private Integer heartRate;
    private Integer prInterval;
    private Integer qrsDuration;
    private Integer qtInterval;
    private Integer qtcInterval;
    private String abnormalType;
    private Integer abnormalCount;
    private Integer abnormalLevel;
    private String abnormalLevelText;
    private BigDecimal confidence;
    private Integer analysisStatus;
    private String analysisStatusText;
    private LocalDateTime diagnosisTime;
}
