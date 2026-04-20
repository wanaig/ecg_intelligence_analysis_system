package com.hnkjzy.ecg_collection.model.vo.workbench;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * Pending alert page item.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WorkbenchPendingAlertItemVo extends BaseVo {

    private Long alertId;
    private LocalDateTime alertTime;
    private String patientInfo;
    private String ward;
    private String alertType;
    private String alertLevel;
    private String alertStatus;
    private String sourceType;
    private String clinicalSymptom;
    private String lisCheckData;
}
