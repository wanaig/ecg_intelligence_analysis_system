package com.hnkjzy.ecg_collection.model.vo.analysis;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * AI diagnosis overview metrics.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AiDiagnosisOverviewVo extends BaseVo {

    private Long totalCount;
    private Long pendingAuditCount;
    private Long auditedCount;
    private BigDecimal avgConfidence;
}
