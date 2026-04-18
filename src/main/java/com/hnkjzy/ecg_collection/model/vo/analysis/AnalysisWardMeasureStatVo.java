package com.hnkjzy.ecg_collection.model.vo.analysis;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 病区心电测量统计。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AnalysisWardMeasureStatVo extends BaseVo {

    private Long wardId;
    private String wardName;
    private Long normalCount;
    private Long abnormalCount;
}
