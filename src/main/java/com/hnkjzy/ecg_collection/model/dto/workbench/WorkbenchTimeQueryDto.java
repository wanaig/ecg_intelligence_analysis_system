package com.hnkjzy.ecg_collection.model.dto.workbench;

import com.hnkjzy.ecg_collection.model.dto.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Workbench time range query.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WorkbenchTimeQueryDto extends BaseDto {

    private String timeType;
    private String startTime;
    private String endTime;
}
