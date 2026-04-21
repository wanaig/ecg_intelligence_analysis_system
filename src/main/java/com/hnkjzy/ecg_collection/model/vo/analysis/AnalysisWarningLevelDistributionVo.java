package com.hnkjzy.ecg_collection.model.vo.analysis;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 预警级别分布。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AnalysisWarningLevelDistributionVo extends BaseVo {

    private Long lowRiskCount;
    private Long middleRiskCount;
    private Long highRiskCount;
}
