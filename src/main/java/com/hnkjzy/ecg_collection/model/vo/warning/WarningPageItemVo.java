package com.hnkjzy.ecg_collection.model.vo.warning;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 预警分页列表项。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WarningPageItemVo extends BaseVo {

    private Long rowNum;
    private Long alertId;
    private LocalDateTime warningTime;
    private String patientName;
    private String genderText;
    private Integer age;
    private String patientInfo;
    private String inpatientNo;
    private String wardName;
    private String warningType;
    private Integer alertLevel;
    private String alertLevelText;
    private Integer alertStatus;
    private String alertStatusText;
    private LocalDateTime handleTime;
}
