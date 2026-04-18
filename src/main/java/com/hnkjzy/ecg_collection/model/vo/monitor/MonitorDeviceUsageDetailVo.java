package com.hnkjzy.ecg_collection.model.vo.monitor;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 设备使用详情。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorDeviceUsageDetailVo extends BaseVo {

    private MonitorDeviceBasicInfoVo basicInfo;
    private MonitorDeviceUsageStatVo usageStat;
    private List<String> deptTags;
    private List<MonitorDeviceCurrentPatientVo> currentPatients;
}
