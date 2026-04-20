package com.hnkjzy.ecg_collection.model.dto.monitor;

import com.hnkjzy.ecg_collection.model.dto.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 质控记录分页查询请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorQualityControlPageQueryDto extends BaseDto {

    private Long deviceId;
    private String status;
    private String testType;
    private String startTime;
    private String endTime;
    private Long pageNum;
    private Long pageSize;
}
