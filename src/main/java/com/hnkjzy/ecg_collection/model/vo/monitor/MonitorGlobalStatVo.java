package com.hnkjzy.ecg_collection.model.vo.monitor;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 实时监护顶部统计。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorGlobalStatVo extends BaseVo {

    private Long todayCollect;
    private Long pendingAnalyse;
    private Long pendingAudit;
    private Long alertTotal;
}
