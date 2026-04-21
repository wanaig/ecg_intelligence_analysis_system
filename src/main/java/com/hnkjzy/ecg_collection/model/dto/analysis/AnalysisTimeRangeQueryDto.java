package com.hnkjzy.ecg_collection.model.dto.analysis;

import com.hnkjzy.ecg_collection.model.dto.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据分析大盘时间范围查询参数。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AnalysisTimeRangeQueryDto extends BaseDto {

    private String startTime;
    private String endTime;
}
