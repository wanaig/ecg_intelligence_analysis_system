package com.hnkjzy.ecg_collection.model.vo.monitor;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 设备使用详情-基础信息。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorDeviceBasicInfoVo extends BaseVo {

    private Long deviceId;
    private String deviceCode;
    private String deviceName;
    private Integer deviceType;
    private String deviceTypeText;
    private String deviceModel;
    private String manufacturer;
    private Integer deviceStatus;
    private String deviceStatusText;
    private Long wardId;
    private String wardName;
    private LocalDate installDate;
}
