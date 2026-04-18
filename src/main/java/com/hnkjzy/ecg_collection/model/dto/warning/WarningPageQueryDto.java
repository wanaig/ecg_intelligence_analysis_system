package com.hnkjzy.ecg_collection.model.dto.warning;

import com.hnkjzy.ecg_collection.model.dto.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 预警列表分页查询请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WarningPageQueryDto extends BaseDto {

    private String keyword;
    private String ward;
    private String alertLevel;
    private String alertStatus;
    private String startTime;
    private String endTime;
    private Long pageNum;
    private Long pageSize;
}
