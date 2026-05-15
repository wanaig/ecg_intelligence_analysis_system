package com.hnkjzy.ecg_collection.model.vo.analysis;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 推送符合条件的患者数统计。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AnalysisPushEligibleCountVo extends BaseVo {

    private Integer eligibleCount;
}
