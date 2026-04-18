package com.hnkjzy.ecg_collection.model.vo.patient;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 患者预警历史。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PatientWarningVo extends BaseVo {

    private Long warningId;
    private LocalDateTime warningTime;
    private String warningType;
    private Integer warningLevel;
    private String warningLevelText;
    private String warningDesc;
    private Integer handleStatus;
    private String handleStatusText;
    private String handleUserName;
    private LocalDateTime handleTime;
    private String handleOpinion;
}
