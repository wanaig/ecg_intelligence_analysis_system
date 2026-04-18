package com.hnkjzy.ecg_collection.model.vo.monitor;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 设备新增/编辑返回。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorDeviceSaveResultVo extends BaseVo {

    private Long deviceId;
    private String deviceCode;
    private String deviceName;
    private Integer deviceType;
    private String deviceTypeText;
    private String deviceModel;
    private String manufacturer;
    private String supplier;
    private LocalDate installDate;
    private Long wardId;
    private String wardName;
    private Integer deviceStatus;
    private String deviceStatusText;
    private LocalDate lastMaintainTime;
    private LocalDate nextMaintainTime;
}
