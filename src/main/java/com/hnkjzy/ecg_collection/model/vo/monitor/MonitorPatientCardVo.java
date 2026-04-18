package com.hnkjzy.ecg_collection.model.vo.monitor;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 实时监护大屏患者卡片。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorPatientCardVo extends BaseVo {

    private Long patientId;
    private String patientName;
    private String wardBed;
    private Integer heartRate;
    private String monitorStatus;
    private String updateTime;
}
