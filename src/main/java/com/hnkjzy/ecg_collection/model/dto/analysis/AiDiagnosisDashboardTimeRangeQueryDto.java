package com.hnkjzy.ecg_collection.model.dto.analysis;

import com.hnkjzy.ecg_collection.model.dto.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AI diagnosis dashboard time range query.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AiDiagnosisDashboardTimeRangeQueryDto extends BaseDto {

    private String startTime;
    private String endTime;
}
