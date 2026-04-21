package com.hnkjzy.ecg_collection.model.dto.analysis;

import com.hnkjzy.ecg_collection.model.dto.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据分析-预警纳入请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AnalysisWarningIncludeDto extends BaseDto {

    private Long warningId;
}
