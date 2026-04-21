package com.hnkjzy.ecg_collection.model.vo.analysis;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 预警类型占比 + 病区 TOP。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AnalysisWarningTypeWardTopVo extends BaseVo {

    private List<AnalysisWarningTypeRatioItemVo> warningTypeStats;
    private List<AnalysisWardWarningTopItemVo> wardTopStats;
}
