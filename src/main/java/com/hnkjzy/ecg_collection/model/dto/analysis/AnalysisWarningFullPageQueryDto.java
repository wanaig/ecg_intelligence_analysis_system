package com.hnkjzy.ecg_collection.model.dto.analysis;

import com.hnkjzy.ecg_collection.model.dto.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Full warning page init query.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AnalysisWarningFullPageQueryDto extends BaseDto {

    private String keyword;
    private String ward;
    private String alertLevel;
    private String alertStatus;
    private String startTime;
    private String endTime;
    private Long pageNum;
    private Long pageSize;
}
