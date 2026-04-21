package com.hnkjzy.ecg_collection.model.vo.analysis;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 预警日趋势统计项。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AnalysisWarningDailyCountVo extends BaseVo {

    private String warningDate;
    private Long warningCount;
}
