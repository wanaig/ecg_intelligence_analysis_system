package com.hnkjzy.ecg_collection.model.dto.analysis;

import com.hnkjzy.ecg_collection.model.dto.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AI diagnosis center page query.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AiDiagnosisPageQueryDto extends BaseDto {

    private String ecgNo;
    private String patientName;
    private String status;
    private Long pageNum;
    private Long pageSize;
}
