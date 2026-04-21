package com.hnkjzy.ecg_collection.model.vo.analysis;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 数据分析-预警纳入结果。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AnalysisWarningIncludeResultVo extends BaseVo {

    private Long warningId;
    private Long patientId;
    private Boolean keyMonitorIncluded;
    private Boolean researchIncluded;
    private Long monitorId;
    private Long researchId;
    private String includedBy;
    private LocalDateTime includedTime;
}
