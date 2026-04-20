package com.hnkjzy.ecg_collection.model.vo.analysis;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * Internal VO for diagnosis audit base data.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AiDiagnosisAuditBaseVo extends BaseVo {

    private Long aiDiagnosisId;
    private String diagnosisId;
    private Long ecgId;
    private String ecgNo;

    private Long patientId;
    private String patientName;
    private String gender;
    private Integer age;
    private String hospitalNo;
    private String aiConclusion;
    private LocalDateTime collectionTime;

    private Long reportId;
    private String reportNo;
    private Integer reportStatus;
}
