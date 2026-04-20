package com.hnkjzy.ecg_collection.model.vo.monitor;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 质控详情-设备信息快照。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorQualityControlDeviceSnapshotVo extends BaseVo {

    private Long deviceId;
    private String deviceName;
    private Long deptId;
    private String deptName;
    private String deviceStatus;
}
