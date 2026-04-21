package com.hnkjzy.ecg_collection.model.dto.analysis;

import com.hnkjzy.ecg_collection.model.dto.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AI diagnosis dashboard audit submit request.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AiDiagnosisAuditSubmitV2Dto extends BaseDto {

    private String diagnosisId;
    private Integer adoptAiFlag;
    private Integer markNormalFlag;
    private String finalAuditResult;
    private String doctorConclusion;
    private String doctorSuggestion;
    private String auditOpinion;
}
