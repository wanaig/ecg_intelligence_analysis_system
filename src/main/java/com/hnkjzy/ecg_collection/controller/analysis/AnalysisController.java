package com.hnkjzy.ecg_collection.controller.analysis;

import com.hnkjzy.ecg_collection.common.result.ApiResponse;
import com.hnkjzy.ecg_collection.controller.BaseController;
import com.hnkjzy.ecg_collection.model.dto.analysis.AnalysisDashboardQueryDto;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisCoreMetricsVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisDictVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisReportDeviceStatVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisWardMeasureStatVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisWarningDimensionStatVo;
import com.hnkjzy.ecg_collection.service.analysis.AnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
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
