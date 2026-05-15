package com.hnkjzy.ecg_collection.controller.analysis;

import com.hnkjzy.ecg_collection.common.result.ApiResponse;
import com.hnkjzy.ecg_collection.controller.BaseController;
import com.hnkjzy.ecg_collection.model.dto.analysis.AnalysisDashboardPageQueryDto;
import com.hnkjzy.ecg_collection.model.dto.analysis.AnalysisDashboardQueryDto;
import com.hnkjzy.ecg_collection.model.dto.analysis.AnalysisTimeRangeQueryDto;
import com.hnkjzy.ecg_collection.model.dto.analysis.AnalysisWarningIncludeDto;
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
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisWarningIncludeResultVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisWarningTrendVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisWarningTypeWardTopVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisWarningDimensionStatVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisPushEligibleCountVo;
import jakarta.servlet.http.HttpServletRequest;
import com.hnkjzy.ecg_collection.service.analysis.AnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 数据分析模块接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/analysis", "/analysis"})
public class AnalysisController extends BaseController {

    private final AnalysisService analysisService;

    @GetMapping("/dashboard/core-metrics")
    public ApiResponse<AnalysisDashboardCoreMetricsVo> dashboardCoreMetrics(AnalysisTimeRangeQueryDto queryDto) {
        return ApiResponse.success(analysisService.getDashboardCoreMetrics(queryDto));
    }

    @GetMapping("/dashboard/warning-level-distribution")
    public ApiResponse<AnalysisWarningLevelDistributionVo> warningLevelDistribution(AnalysisTimeRangeQueryDto queryDto) {
        return ApiResponse.success(analysisService.getWarningLevelDistribution(queryDto));
    }

    @GetMapping("/dashboard/warning-type-ward-top")
    public ApiResponse<AnalysisWarningTypeWardTopVo> warningTypeWardTop(AnalysisTimeRangeQueryDto queryDto) {
        return ApiResponse.success(analysisService.getWarningTypeWardTop(queryDto));
    }

    @GetMapping("/dashboard/warning-trend-7d")
    public ApiResponse<AnalysisWarningTrendVo> warningTrend7d(AnalysisTimeRangeQueryDto queryDto) {
        return ApiResponse.success(analysisService.getWarningTrend7d(queryDto));
    }

    @PostMapping("/dashboard/pending-warnings/page")
    public ApiResponse<AnalysisDashboardPageResultVo<AnalysisPendingWarningPageItemVo>> pendingWarningsPage(
            @RequestBody(required = false) AnalysisDashboardPageQueryDto queryDto) {
        return ApiResponse.success(analysisService.pagePendingWarnings(queryDto));
    }

    @PostMapping("/dashboard/latest-ecg/page")
    public ApiResponse<AnalysisDashboardPageResultVo<AnalysisLatestEcgPageItemVo>> latestEcgPage(
            @RequestBody(required = false) AnalysisDashboardPageQueryDto queryDto) {
        return ApiResponse.success(analysisService.pageLatestEcgRecords(queryDto));
    }

    @GetMapping("/dashboard/push/eligible-count")
    public ApiResponse<AnalysisPushEligibleCountVo> pushEligibleCount(AnalysisTimeRangeQueryDto queryDto) {
        return ApiResponse.success(analysisService.countPushEligiblePatients(queryDto));
    }

    @PostMapping("/dashboard/push/eligible-patients/page")
    public ApiResponse<AnalysisDashboardPageResultVo<AnalysisPendingWarningPageItemVo>> pushEligiblePatientsPage(
            @RequestBody(required = false) AnalysisDashboardPageQueryDto queryDto) {
        return ApiResponse.success(analysisService.pagePushEligiblePatients(queryDto));
    }

    @GetMapping("/dashboard/warnings/{alertId}/detail")
    public ApiResponse<AnalysisWarningDetailVo> warningDetail(@PathVariable("alertId") Long alertId) {
        return ApiResponse.success(analysisService.getDashboardWarningDetail(alertId));
    }

    @PostMapping("/dashboard/warnings/full-page/init")
    public ApiResponse<AnalysisWarningFullPageInitVo> fullWarningPageInit(
            @RequestBody(required = false) AnalysisWarningFullPageQueryDto queryDto) {
        return ApiResponse.success(analysisService.getFullWarningPageInitData(queryDto));
    }

    @PostMapping("/dashboard/warnings/include")
    public ApiResponse<AnalysisWarningIncludeResultVo> includeWarning(
            @RequestBody AnalysisWarningIncludeDto includeDto,
            HttpServletRequest request) {
        return ApiResponse.success(analysisService.includeWarning(includeDto, request));
    }

    @GetMapping("/core-metrics")
    public ApiResponse<AnalysisCoreMetricsVo> coreMetrics(AnalysisDashboardQueryDto queryDto) {
        return ApiResponse.success(analysisService.getCoreMetrics(queryDto));
    }

    @GetMapping("/ward-ecg-stats")
    public ApiResponse<List<AnalysisWardMeasureStatVo>> wardEcgStats() {
        return ApiResponse.success(analysisService.getWardMeasureStats());
    }

    @GetMapping("/warning-dimensions")
    public ApiResponse<AnalysisWarningDimensionStatVo> warningDimensions() {
        return ApiResponse.success(analysisService.getWarningDimensionStats());
    }

    @GetMapping("/report-device-stats")
    public ApiResponse<AnalysisReportDeviceStatVo> reportDeviceStats() {
        return ApiResponse.success(analysisService.getReportDeviceStats());
    }

    @GetMapping("/dicts")
    public ApiResponse<AnalysisDictVo> dicts() {
        return ApiResponse.success(analysisService.getAnalysisDicts());
    }
}
