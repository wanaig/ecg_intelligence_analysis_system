package com.hnkjzy.ecg_collection.model.vo.analysis;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * Diagnosis report detail response.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DiagnosisReportDetailVo extends BaseVo {

    private Long reportId;
    private String reportNo;
    private Long recordId;
    private Long aiDiagnosisId;

    private Long patientId;
    private String patientName;
    private String gender;
    private Integer age;
    private String hospitalNo;

    private LocalDateTime collectionTime;

    private String aiConclusion;
    private String doctorConclusion;
    private String doctorSuggestion;

    private Long reportCreateDoctorId;
    private String reportCreateDoctorName;
    private LocalDateTime reportCreateTime;

    private Long auditDoctorId;
    private String auditDoctorName;
    private LocalDateTime auditTime;
    private String auditOpinion;

    private Integer reportStatus;
    private String status;
}
