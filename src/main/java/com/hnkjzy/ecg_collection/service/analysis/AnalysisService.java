package com.hnkjzy.ecg_collection.service.analysis;

import com.hnkjzy.ecg_collection.model.dto.analysis.AnalysisDashboardPageQueryDto;
import com.hnkjzy.ecg_collection.model.dto.analysis.AnalysisDashboardQueryDto;
import com.hnkjzy.ecg_collection.model.dto.analysis.AnalysisTimeRangeQueryDto;
import com.hnkjzy.ecg_collection.model.dto.analysis.AnalysisWarningFullPageQueryDto;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisDashboardCoreMetricsVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisDashboardPageResultVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisCoreMetricsVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisDictVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisLatestEcgPageItemVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisPendingWarningPageItemVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisReportDeviceStatVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisWardMeasureStatVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisWarningLevelDistributionVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisWarningDetailVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisWarningFullPageInitVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisWarningTrendVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisWarningTypeWardTopVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisWarningDimensionStatVo;
import com.hnkjzy.ecg_collection.service.BaseService;

import java.util.List;

/**
 * 数据分析模块服务。
 */
public interface AnalysisService extends BaseService {

    AnalysisDashboardCoreMetricsVo getDashboardCoreMetrics(AnalysisTimeRangeQueryDto queryDto);

    AnalysisWarningLevelDistributionVo getWarningLevelDistribution(AnalysisTimeRangeQueryDto queryDto);

    AnalysisWarningTypeWardTopVo getWarningTypeWardTop(AnalysisTimeRangeQueryDto queryDto);

    AnalysisWarningTrendVo getWarningTrend7d();

    AnalysisDashboardPageResultVo<AnalysisPendingWarningPageItemVo> pagePendingWarnings(AnalysisDashboardPageQueryDto queryDto);

    AnalysisDashboardPageResultVo<AnalysisLatestEcgPageItemVo> pageLatestEcgRecords(AnalysisDashboardPageQueryDto queryDto);

    AnalysisWarningDetailVo getDashboardWarningDetail(Long alertId);

    AnalysisWarningFullPageInitVo getFullWarningPageInitData(AnalysisWarningFullPageQueryDto queryDto);

    AnalysisCoreMetricsVo getCoreMetrics(AnalysisDashboardQueryDto queryDto);

    List<AnalysisWardMeasureStatVo> getWardMeasureStats();

    AnalysisWarningDimensionStatVo getWarningDimensionStats();

    AnalysisReportDeviceStatVo getReportDeviceStats();

    AnalysisDictVo getAnalysisDicts();
}
