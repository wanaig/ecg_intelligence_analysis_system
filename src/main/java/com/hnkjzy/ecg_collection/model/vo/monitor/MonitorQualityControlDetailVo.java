package com.hnkjzy.ecg_collection.model.vo.monitor;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 质控记录详情。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorQualityControlDetailVo extends BaseVo {

    private Long qcId;
    private String qcNo;
    private Long deviceId;
    private String deviceName;
    private Long deptId;
    private String deptName;
    private LocalDateTime testTime;
    private String testType;
    private Long testUserId;
    private String testUserName;
    private String deviceStatus;
    private String testResult;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private MonitorQualityControlDeviceSnapshotVo deviceInfo;
    private MonitorQualityControlTesterSnapshotVo testerInfo;
    private MonitorQualityControlParamVo qualityParams;
    private MonitorQualityControlTimeSnapshotVo timeSnapshot;
    private List<MonitorQualityControlIndicatorVo> indicatorDetails;
}
