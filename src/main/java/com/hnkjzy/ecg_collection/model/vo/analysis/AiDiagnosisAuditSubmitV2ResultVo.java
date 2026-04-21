package com.hnkjzy.ecg_collection.model.vo.analysis;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * AI diagnosis dashboard audit submit result.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AiDiagnosisAuditSubmitV2ResultVo extends BaseVo {

    private String diagnosisId;
    private String finalAuditResult;
    private Integer reportStatus;
    private String status;
    private String auditedBy;
    private LocalDateTime auditedTime;
    private Long reportId;
    private String reportNo;
    private Integer warningSyncCount;
    private Integer markNormalFlag;
    private Integer adoptAiFlag;
}
