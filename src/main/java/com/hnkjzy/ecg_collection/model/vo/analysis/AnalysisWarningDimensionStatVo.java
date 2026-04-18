package com.hnkjzy.ecg_collection.model.vo.analysis;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 预警维度统计。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AnalysisWarningDimensionStatVo extends BaseVo {

    private List<AnalysisWarningLevelStatVo> levelStats;
    private List<AnalysisWarningTypeStatVo> typeStats;
}
