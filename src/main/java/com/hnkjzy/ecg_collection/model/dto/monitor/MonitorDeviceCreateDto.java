package com.hnkjzy.ecg_collection.model.dto.monitor;

import com.hnkjzy.ecg_collection.model.dto.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 新增设备请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorDeviceCreateDto extends BaseDto {

    private String deviceCode;
    private String deviceName;
    private Integer deviceType;
    private String deviceModel;
    private String manufacturer;
    private String supplier;
    private String installDate;
    private Long bindDeptId;
    private String bindDeptName;
    private String lastMaintainTime;
    private String nextMaintainTime;
    private Integer deviceStatus;
}
