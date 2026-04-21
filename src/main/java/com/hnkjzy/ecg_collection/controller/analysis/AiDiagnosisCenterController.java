package com.hnkjzy.ecg_collection.controller.analysis;

import com.hnkjzy.ecg_collection.common.result.ApiResponse;
import com.hnkjzy.ecg_collection.controller.BaseController;
import com.hnkjzy.ecg_collection.model.dto.analysis.AiDiagnosisAuditSubmitDto;
import com.hnkjzy.ecg_collection.model.dto.analysis.AiDiagnosisAuditSubmitV2Dto;
import com.hnkjzy.ecg_collection.model.dto.analysis.AiDiagnosisDashboardPageQueryDto;
import com.hnkjzy.ecg_collection.model.dto.analysis.AiDiagnosisDashboardTimeRangeQueryDto;
import com.hnkjzy.ecg_collection.model.dto.analysis.AiDiagnosisPageQueryDto;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisAbnormalTypeRatioVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisAuditDetailVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisAuditSubmitResultVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisAuditSubmitV2ResultVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisCoreMetricsVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisDashboardPageItemVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisDetailVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisEngineStatusVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisLiteDetailVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisOverviewVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisPageResultVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisStatusDictVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisTrendVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisWaveformVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisDashboardPageResultVo;
import com.hnkjzy.ecg_collection.service.analysis.AiDiagnosisCenterService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * AI diagnosis center APIs.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/analysis/ai-diagnosis", "/analysis/ai-diagnosis"})
public class AiDiagnosisCenterController extends BaseController {

    private final AiDiagnosisCenterService aiDiagnosisCenterService;

    @GetMapping("/dashboard/engine-status")
    public ApiResponse<AiDiagnosisEngineStatusVo> engineStatus() {
        return ApiResponse.success(aiDiagnosisCenterService.getEngineStatus());
    }

    @GetMapping("/dashboard/core-metrics")
    public ApiResponse<AiDiagnosisCoreMetricsVo> dashboardCoreMetrics(AiDiagnosisDashboardTimeRangeQueryDto queryDto) {
        return ApiResponse.success(aiDiagnosisCenterService.getDashboardCoreMetrics(queryDto));
    }

    @GetMapping("/dashboard/warning-trend")
    public ApiResponse<AiDiagnosisTrendVo> warningTrend(@RequestParam(value = "timeRange", required = false) String timeRange) {
        return ApiResponse.success(aiDiagnosisCenterService.getWarningTrend(timeRange));
    }

    @GetMapping("/dashboard/abnormal-type-ratio")
    public ApiResponse<List<AiDiagnosisAbnormalTypeRatioVo>> abnormalTypeRatio(AiDiagnosisDashboardTimeRangeQueryDto queryDto) {
        return ApiResponse.success(aiDiagnosisCenterService.getAbnormalTypeRatios(queryDto));
    }

    @PostMapping("/dashboard/page")
    public ApiResponse<AnalysisDashboardPageResultVo<AiDiagnosisDashboardPageItemVo>> dashboardPage(
            @RequestBody(required = false) AiDiagnosisDashboardPageQueryDto queryDto) {
        return ApiResponse.success(aiDiagnosisCenterService.pageDashboardDiagnoses(queryDto));
    }

    @GetMapping("/dashboard/{diagnosisId}/lite")
    public ApiResponse<AiDiagnosisLiteDetailVo> liteDetail(@PathVariable("diagnosisId") String diagnosisId) {
        return ApiResponse.success(aiDiagnosisCenterService.getLiteDetail(diagnosisId));
    }

    @GetMapping("/dashboard/{diagnosisId}/audit-detail")
    public ApiResponse<AiDiagnosisAuditDetailVo> auditDetail(@PathVariable("diagnosisId") String diagnosisId) {
        return ApiResponse.success(aiDiagnosisCenterService.getAuditDetail(diagnosisId));
    }

    @PostMapping("/dashboard/audit/submit")
    public ApiResponse<AiDiagnosisAuditSubmitV2ResultVo> dashboardAuditSubmit(@RequestBody AiDiagnosisAuditSubmitV2Dto submitDto,
                                                                               HttpServletRequest request) {
        return ApiResponse.success(aiDiagnosisCenterService.submitAuditV2(submitDto, request));
    }

    @GetMapping("/overview")
    public ApiResponse<AiDiagnosisOverviewVo> overview() {
        return ApiResponse.success(aiDiagnosisCenterService.getOverview());
    }

    @PostMapping("/page")
    public ApiResponse<AiDiagnosisPageResultVo> page(@RequestBody(required = false) AiDiagnosisPageQueryDto queryDto) {
        return ApiResponse.success(aiDiagnosisCenterService.pageDiagnoses(queryDto));
    }

    @GetMapping("/dicts")
    public ApiResponse<AiDiagnosisStatusDictVo> dicts() {
        return ApiResponse.success(aiDiagnosisCenterService.getStatusDicts());
    }

    @GetMapping("/{diagnosisId}")
    public ApiResponse<AiDiagnosisDetailVo> detail(@PathVariable("diagnosisId") String diagnosisId) {
        return ApiResponse.success(aiDiagnosisCenterService.getDiagnosisDetail(diagnosisId));
    }

    @GetMapping("/ecg/{ecgId}/waveform")
    public ApiResponse<AiDiagnosisWaveformVo> waveform(@PathVariable("ecgId") Long ecgId) {
        return ApiResponse.success(aiDiagnosisCenterService.getWaveform(ecgId));
    }

    @PostMapping("/audit")
    public ApiResponse<AiDiagnosisAuditSubmitResultVo> audit(@RequestBody AiDiagnosisAuditSubmitDto submitDto,
                                                              HttpServletRequest request) {
        return ApiResponse.success(aiDiagnosisCenterService.submitAudit(submitDto, request));
    }
}
