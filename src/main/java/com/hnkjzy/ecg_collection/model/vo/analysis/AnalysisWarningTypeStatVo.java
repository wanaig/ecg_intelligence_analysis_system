package com.hnkjzy.ecg_collection.model.vo.analysis;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 预警类型统计项。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AnalysisWarningTypeStatVo extends BaseVo {

    private String warningType;
    private Long count;
}
