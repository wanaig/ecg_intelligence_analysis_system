package com.hnkjzy.ecg_collection.model.vo.workbench;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Workbench alert detail.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WorkbenchAlertDetailVo extends BaseVo {

    private Long alertId;
    private LocalDateTime alertTime;
    private String alertType;
    private String alertLevel;
    private String alertStatus;

    private Long patientId;
    private String patientName;
    private String gender;
    private Integer age;
    private String inpatientNo;
    private String ward;
    private String bedNo;
    private String primaryDiagnosis;

    private String sourceType;
    private Long sourceRecordId;
    private String sourceEcgNo;
    private String sourceDeviceName;
    private LocalDateTime sourceCollectTime;

    private String clinicalSymptom;
    private String lisCheckData;

    private List<WorkbenchHistoryEcgItemVo> historyEcgList;
}
