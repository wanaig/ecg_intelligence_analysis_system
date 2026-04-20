package com.hnkjzy.ecg_collection.model.dto.analysis;

import com.hnkjzy.ecg_collection.model.dto.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Diagnosis report management page query.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DiagnosisReportPageQueryDto extends BaseDto {

    private String reportNo;
    private String patientName;
    private String status;
    private Long pageNum;
    private Long pageSize;
}
