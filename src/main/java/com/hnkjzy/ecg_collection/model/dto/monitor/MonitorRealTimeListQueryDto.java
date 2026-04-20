package com.hnkjzy.ecg_collection.model.dto.monitor;

import com.hnkjzy.ecg_collection.model.dto.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 实时监护患者列表请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorRealTimeListQueryDto extends BaseDto {

    private Long wardId;
    private Integer level;
}
