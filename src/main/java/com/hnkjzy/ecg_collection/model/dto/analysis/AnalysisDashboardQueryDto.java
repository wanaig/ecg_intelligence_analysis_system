package com.hnkjzy.ecg_collection.model.dto.analysis;

import com.hnkjzy.ecg_collection.model.dto.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据分析核心指标查询参数。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AnalysisDashboardQueryDto extends BaseDto {

    private String timeType;
    private Long wardId;
    private String startDate;
    private String endDate;
}
