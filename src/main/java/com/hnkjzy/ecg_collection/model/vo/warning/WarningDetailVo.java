package com.hnkjzy.ecg_collection.model.vo.warning;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 预警详情。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WarningDetailVo extends BaseVo {

    private Long alertId;

    private LocalDateTime warningTime;
    private Integer alertLevel;
    private String alertLevelText;
    private Integer alertStatus;
    private String alertStatusText;

    private String patientName;
    private Integer age;
    private String genderText;
    private String inpatientNo;
    private String wardName;

    private String warningType;
    private String warningDesc;
    private String aiConclusion;
    private String clinicalManifestation;
    private String lisHint;

    private String handleUserName;
    private LocalDateTime handleTime;
    private String handleRemark;
}
