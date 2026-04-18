package com.hnkjzy.ecg_collection.model.vo.analysis;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 报告与设备统计。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AnalysisReportDeviceStatVo extends BaseVo {

    private List<AnalysisReportStatusStatVo> reportStatusStats;
    private List<AnalysisDeviceUsageStatVo> deviceUsageStats;
}
