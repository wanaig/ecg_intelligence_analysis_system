package com.hnkjzy.ecg_collection.model.vo.analysis;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 近7日预警趋势。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AnalysisWarningTrendVo extends BaseVo {

    private List<String> dateList;
    private List<Long> warningCountList;
}
