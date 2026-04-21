package com.hnkjzy.ecg_collection.model.vo.analysis;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * Full warning page row item.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AnalysisWarningFullPageItemVo extends BaseVo {

    private Long warningId;
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
