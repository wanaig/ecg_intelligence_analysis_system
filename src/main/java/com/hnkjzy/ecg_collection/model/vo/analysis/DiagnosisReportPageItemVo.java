package com.hnkjzy.ecg_collection.model.vo.analysis;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * Diagnosis report management page item.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DiagnosisReportPageItemVo extends BaseVo {

    private Long reportId;
    private String reportNo;
    private String patientInfo;
    private String hospitalNo;
    private LocalDateTime collectionTime;
    private String aiConclusion;
    private String doctorConclusion;
    private String auditDoctorName;
    private LocalDateTime auditTime;
    private String status;
}
