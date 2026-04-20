package com.hnkjzy.ecg_collection.model.dto.analysis;

import com.hnkjzy.ecg_collection.model.dto.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AI diagnosis audit submit request.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AiDiagnosisAuditSubmitDto extends BaseDto {

    private String diagnosisId;
    private String doctorConclusion;
}
