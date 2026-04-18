package com.hnkjzy.ecg_collection.model.vo.analysis;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 预警级别统计项。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AnalysisWarningLevelStatVo extends BaseVo {

    private String level;
    private Long count;
}
