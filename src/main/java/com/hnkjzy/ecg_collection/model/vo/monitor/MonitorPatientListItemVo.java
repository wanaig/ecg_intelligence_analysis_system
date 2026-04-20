package com.hnkjzy.ecg_collection.model.vo.monitor;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 监护患者列表项。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorPatientListItemVo extends BaseVo {

    private Long patientId;
    private String patientName;
    private String wardBed;
    private Integer heartRate;
    private String status;
    private String updateTime;
    private List<String> actionPermissions;
}
