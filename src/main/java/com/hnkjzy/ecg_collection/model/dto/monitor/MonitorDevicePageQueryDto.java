package com.hnkjzy.ecg_collection.model.dto.monitor;

import com.hnkjzy.ecg_collection.model.dto.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 设备分页查询请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorDevicePageQueryDto extends BaseDto {

    private String deviceName;
    private String deviceType;
    private String ward;
    private String purchaseDateStart;
    private String purchaseDateEnd;
    private Long pageNum;
    private Long pageSize;
}
