package com.hnkjzy.ecg_collection.controller.analysis;

import com.hnkjzy.ecg_collection.common.result.ApiResponse;
import com.hnkjzy.ecg_collection.controller.BaseController;
import com.hnkjzy.ecg_collection.model.dto.analysis.ResearchDataPageQueryDto;
import com.hnkjzy.ecg_collection.model.dto.analysis.ResearchDataSelectedExportDto;
import com.hnkjzy.ecg_collection.model.vo.analysis.ResearchDataExportFileVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.ResearchDataPageResultVo;
import com.hnkjzy.ecg_collection.service.analysis.ResearchDataManagerService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

/**
 * Research data management APIs.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/analysis/research-data", "/analysis/research-data"})
public class ResearchDataManagerController extends BaseController {

    private final ResearchDataManagerService researchDataManagerService;

    @GetMapping("/page")
    public ApiResponse<ResearchDataPageResultVo> page(ResearchDataPageQueryDto queryDto) {
        return ApiResponse.success(researchDataManagerService.pageResearchData(queryDto));
    }

    @PostMapping("/export/selected")
    public ResponseEntity<byte[]> exportSelected(@RequestBody ResearchDataSelectedExportDto exportDto,
                                                 HttpServletRequest request) {
        ResearchDataExportFileVo fileVo = researchDataManagerService.exportSelectedMaskedData(exportDto, request);
        return buildFileResponse(fileVo);
    }

    @PostMapping("/export/all")
    public ResponseEntity<byte[]> exportAll(@RequestBody(required = false) ResearchDataPageQueryDto queryDto,
                                            HttpServletRequest request) {
        ResearchDataExportFileVo fileVo = researchDataManagerService.exportFilteredMaskedData(queryDto, request);
        return buildFileResponse(fileVo);
    }

    private ResponseEntity<byte[]> buildFileResponse(ResearchDataExportFileVo fileVo) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(fileVo.getContentType()));
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename(fileVo.getFileName(), StandardCharsets.UTF_8)
                .build());
        headers.setCacheControl("no-cache, no-store, must-revalidate");

        return ResponseEntity.ok()
                .headers(headers)
                .body(fileVo.getContent());
    }
}
