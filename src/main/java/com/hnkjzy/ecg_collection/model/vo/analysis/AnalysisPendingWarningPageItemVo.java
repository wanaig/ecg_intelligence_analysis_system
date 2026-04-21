package com.hnkjzy.ecg_collection.model.vo.analysis;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 待处理预警分页项。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AnalysisPendingWarningPageItemVo extends BaseVo {

    private Long warningId;
    private LocalDateTime warningTime;
    private String patientInfo;
    private String ward;
    private String clinicalIndicator;
    private String warningLevel;
    private String status;
}
