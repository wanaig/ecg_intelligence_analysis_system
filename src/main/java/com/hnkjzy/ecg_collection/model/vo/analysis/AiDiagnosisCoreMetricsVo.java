package com.hnkjzy.ecg_collection.model.vo.analysis;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * AI diagnosis dashboard core metrics.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AiDiagnosisCoreMetricsVo extends BaseVo {

    private Long diagnosisTotal;
    private BigDecimal diagnosisYoYRate;
    private Long pendingAuditCount;
    private BigDecimal pendingAuditYoYRate;
    private Long auditedPassCount;
    private BigDecimal auditedPassYoYRate;
    private Long abnormalCount;
    private BigDecimal abnormalYoYRate;
    private Long normalCount;
    private BigDecimal normalYoYRate;
}
