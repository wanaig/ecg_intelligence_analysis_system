package com.hnkjzy.ecg_collection.model.vo.monitor;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 当前使用该设备的患者。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorDeviceCurrentPatientVo extends BaseVo {

    private Long patientId;
    private String patientName;
    private String inpatientNo;
    private String wardBed;
    private Integer heartRate;
    private String monitorStatus;
    private LocalDateTime updateTime;
}
