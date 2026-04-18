package com.hnkjzy.ecg_collection.service.analysis;

import com.hnkjzy.ecg_collection.model.dto.analysis.AnalysisDashboardQueryDto;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisCoreMetricsVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisDictVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisReportDeviceStatVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisWardMeasureStatVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisWarningDimensionStatVo;
import com.hnkjzy.ecg_collection.service.BaseService;

import java.util.List;

/**
 * 数据分析模块服务。
 */
public interface AnalysisService extends BaseService {

    AnalysisCoreMetricsVo getCoreMetrics(AnalysisDashboardQueryDto queryDto);

    List<AnalysisWardMeasureStatVo> getWardMeasureStats();

    AnalysisWarningDimensionStatVo getWarningDimensionStats();

    AnalysisReportDeviceStatVo getReportDeviceStats();

    AnalysisDictVo getAnalysisDicts();
}
