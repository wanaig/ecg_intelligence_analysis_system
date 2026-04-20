package com.hnkjzy.ecg_collection.controller.workbench;

import com.hnkjzy.ecg_collection.common.result.ApiResponse;
import com.hnkjzy.ecg_collection.controller.BaseController;
import com.hnkjzy.ecg_collection.model.dto.workbench.EcgDataPageQueryDto;
import com.hnkjzy.ecg_collection.model.dto.workbench.EcgDataStatusUpdateDto;
import com.hnkjzy.ecg_collection.model.vo.workbench.EcgDataDictVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.EcgDataPageItemVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.EcgDataStatusUpdateResultVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.EcgDataUploadResultVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.EcgDataWaveformVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.WorkbenchEcgDetailVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.WorkbenchPageListVo;
import com.hnkjzy.ecg_collection.service.workbench.EcgDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * ECG data list APIs.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/ecg-data", "/ecg-data"})
public class EcgDataController extends BaseController {

    private final EcgDataService ecgDataService;

    @PostMapping("/page")
    public ApiResponse<WorkbenchPageListVo<EcgDataPageItemVo>> page(
            @RequestBody(required = false) EcgDataPageQueryDto queryDto) {
        return ApiResponse.success(ecgDataService.pageEcgData(queryDto));
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<EcgDataUploadResultVo> upload(@RequestParam("file") MultipartFile file,
                                                      @RequestParam("patientName") String patientName,
                                                      @RequestParam("inpatientNo") String inpatientNo,
                                                      @RequestParam("deviceNo") String deviceNo) {
        return ApiResponse.success(ecgDataService.uploadData(file, patientName, inpatientNo, deviceNo));
    }

    @GetMapping("/{ecgId}")
    public ApiResponse<WorkbenchEcgDetailVo> detail(@PathVariable("ecgId") Long ecgId) {
        return ApiResponse.success(ecgDataService.getEcgDetail(ecgId));
    }

    @PutMapping("/status")
    public ApiResponse<EcgDataStatusUpdateResultVo> updateStatus(@RequestBody EcgDataStatusUpdateDto updateDto) {
        return ApiResponse.success(ecgDataService.updateStatus(updateDto));
    }

    @GetMapping("/dicts")
    public ApiResponse<EcgDataDictVo> dicts() {
        return ApiResponse.success(ecgDataService.getDicts());
    }

    @GetMapping("/{ecgId}/waveform")
    public ApiResponse<EcgDataWaveformVo> waveform(@PathVariable("ecgId") Long ecgId) {
        return ApiResponse.success(ecgDataService.getWaveform(ecgId));
    }
}
