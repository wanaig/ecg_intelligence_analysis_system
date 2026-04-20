package com.hnkjzy.ecg_collection.controller.analysis;

import com.hnkjzy.ecg_collection.common.result.ApiResponse;
import com.hnkjzy.ecg_collection.controller.BaseController;
import com.hnkjzy.ecg_collection.model.dto.analysis.AiDiagnosisAuditSubmitDto;
import com.hnkjzy.ecg_collection.model.dto.analysis.AiDiagnosisPageQueryDto;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisAuditSubmitResultVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisDetailVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisOverviewVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisPageResultVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisStatusDictVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisWaveformVo;
import com.hnkjzy.ecg_collection.service.analysis.AiDiagnosisCenterService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AI diagnosis center APIs.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/analysis/ai-diagnosis", "/analysis/ai-diagnosis"})
public class AiDiagnosisCenterController extends BaseController {

    private final AiDiagnosisCenterService aiDiagnosisCenterService;

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
