package com.hnkjzy.ecg_collection.model.vo.monitor;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 质控关联设备基础信息。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorQualityControlDeviceInfoVo extends BaseVo {

    private Long deviceId;
    private String deviceName;
    private Long deptId;
    private String deptName;
}
