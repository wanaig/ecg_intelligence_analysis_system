package com.hnkjzy.ecg_collection.model.vo.analysis;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Internal daily trend count row.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AiDiagnosisDailyTrendCountVo extends BaseVo {

    private String statDate;
    private Long pendingAuditCount;
    private Long passCount;
    private Long rejectCount;
}
