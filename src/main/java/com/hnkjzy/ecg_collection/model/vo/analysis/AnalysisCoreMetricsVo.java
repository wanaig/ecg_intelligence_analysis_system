package com.hnkjzy.ecg_collection.model.vo.analysis;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 核心大盘指标。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AnalysisCoreMetricsVo extends BaseVo {

    private Long totalMeasureCount;
    private Long normalMeasureCount;
    private Long abnormalMeasureCount;

    private Long warningTotalCount;
    private Long warningHandledCount;
    private BigDecimal avgWarningHandleMinutes;

    private Long reportTotalCount;
    private Long reportAuditedCount;
    private BigDecimal avgReportAuditMinutes;

    private BigDecimal aiAccuracyRate;
    private Long aiSampleTotal;
}
