package com.hnkjzy.ecg_collection.model.vo.workbench;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Workbench overview statistics.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WorkbenchOverviewStatVo extends BaseVo {

    private Long ecgTotalCount;
    private Long todayAddCount;
    private Long pendingAnalyseCount;
    private Long pendingAuditCount;
    private Long alertTotalCount;
    private Long alertHandledCount;
}
