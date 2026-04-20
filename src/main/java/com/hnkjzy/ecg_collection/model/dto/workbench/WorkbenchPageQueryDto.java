package com.hnkjzy.ecg_collection.model.dto.workbench;

import com.hnkjzy.ecg_collection.model.dto.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Workbench page query.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WorkbenchPageQueryDto extends BaseDto {

    private String timeType;
    private String startTime;
    private String endTime;
    private Long pageNum;
    private Long pageSize;
}
