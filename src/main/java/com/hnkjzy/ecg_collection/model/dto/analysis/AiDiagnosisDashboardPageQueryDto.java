package com.hnkjzy.ecg_collection.model.dto.analysis;

import com.hnkjzy.ecg_collection.model.dto.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AI diagnosis dashboard page query.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AiDiagnosisDashboardPageQueryDto extends BaseDto {

    private String keyword;
    private Long deptId;
    private String startTime;
    private String endTime;
    private Long pageNum;
    private Long pageSize;
}
