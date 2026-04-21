package com.hnkjzy.ecg_collection.model.vo.analysis;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Internal metrics snapshot for AI diagnosis dashboard.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AiDiagnosisCoreMetricsSnapshotVo extends BaseVo {

    private Long diagnosisTotal;
    private Long pendingAuditCount;
    private Long auditedPassCount;
    private Long abnormalCount;
    private Long normalCount;
}
