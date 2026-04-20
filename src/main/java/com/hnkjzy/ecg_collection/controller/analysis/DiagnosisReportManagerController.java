package com.hnkjzy.ecg_collection.controller.analysis;

import com.hnkjzy.ecg_collection.common.result.ApiResponse;
import com.hnkjzy.ecg_collection.controller.BaseController;
import com.hnkjzy.ecg_collection.model.dto.analysis.DiagnosisReportPageQueryDto;
import com.hnkjzy.ecg_collection.model.vo.analysis.DiagnosisReportDetailVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.DiagnosisReportPageResultVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.DiagnosisReportPdfFileVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.DiagnosisReportStatusDictVo;
import com.hnkjzy.ecg_collection.service.analysis.DiagnosisReportManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

/**
 * Diagnosis report management APIs.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/analysis/diagnosis-report", "/analysis/diagnosis-report"})
public class DiagnosisReportManagerController extends BaseController {

    private final DiagnosisReportManagerService diagnosisReportManagerService;

    @PostMapping("/page")
    public ApiResponse<DiagnosisReportPageResultVo> page(@RequestBody(required = false) DiagnosisReportPageQueryDto queryDto) {
        return ApiResponse.success(diagnosisReportManagerService.pageReports(queryDto));
    }

    @GetMapping("/dicts")
    public ApiResponse<DiagnosisReportStatusDictVo> dicts() {
        return ApiResponse.success(diagnosisReportManagerService.getStatusDicts());
    }

    @GetMapping("/{reportId}")
    public ApiResponse<DiagnosisReportDetailVo> detail(@PathVariable("reportId") Long reportId) {
        return ApiResponse.success(diagnosisReportManagerService.getReportDetail(reportId));
    }

    @GetMapping(value = "/{reportId}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadPdf(@PathVariable("reportId") Long reportId) {
        DiagnosisReportPdfFileVo pdfFileVo = diagnosisReportManagerService.downloadReportPdf(reportId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename(pdfFileVo.getFileName(), StandardCharsets.UTF_8)
                .build());
        headers.setCacheControl("no-cache, no-store, must-revalidate");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfFileVo.getContent());
    }
}
