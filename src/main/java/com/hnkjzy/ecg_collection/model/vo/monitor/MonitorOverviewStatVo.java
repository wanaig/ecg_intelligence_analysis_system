package com.hnkjzy.ecg_collection.model.vo.monitor;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 监护大屏顶部统计。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorOverviewStatVo extends BaseVo {

    private Long todayCollect;
    private Long pendingAnalyse;
    private Long pendingAudit;
    private Long alertCount;
}
