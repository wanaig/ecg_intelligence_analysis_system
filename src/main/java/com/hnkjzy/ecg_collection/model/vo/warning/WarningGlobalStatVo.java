package com.hnkjzy.ecg_collection.model.vo.warning;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 预警全局统计。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WarningGlobalStatVo extends BaseVo {

    private Long highRiskCount;
    private Long pendingHandleCount;
}
