package com.hnkjzy.ecg_collection.model.vo.analysis;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * AI diagnosis audit submit result.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AiDiagnosisAuditSubmitResultVo extends BaseVo {

    private String diagnosisId;
    private String status;
    private String auditedBy;
    private LocalDateTime auditedTime;
    private Long reportDraftId;
    private String reportNo;
}
