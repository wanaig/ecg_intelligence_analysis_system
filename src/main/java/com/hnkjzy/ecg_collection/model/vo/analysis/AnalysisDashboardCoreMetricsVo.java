package com.hnkjzy.ecg_collection.model.vo.analysis;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 数据分析大盘核心统计指标。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AnalysisDashboardCoreMetricsVo extends BaseVo {

    private Long ecgTotal;
    private Long pendingAnalyse;
    private Long pendingAudit;
    private Long abnormalWarning;
    private Long warningTotal;
    private Long reportTotal;
    private BigDecimal aiAccuracy;
}
