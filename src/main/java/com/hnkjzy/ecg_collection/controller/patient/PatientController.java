package com.hnkjzy.ecg_collection.controller.patient;

import com.hnkjzy.ecg_collection.common.result.ApiResponse;
import com.hnkjzy.ecg_collection.controller.BaseController;
import com.hnkjzy.ecg_collection.model.dto.patient.PatientPageQueryDto;
import com.hnkjzy.ecg_collection.model.vo.common.PageResultVo;
import com.hnkjzy.ecg_collection.model.vo.patient.PatientDashboardStatVo;
import com.hnkjzy.ecg_collection.model.vo.patient.PatientDetailVo;
import com.hnkjzy.ecg_collection.model.vo.patient.PatientDictVo;
import com.hnkjzy.ecg_collection.model.vo.patient.PatientPageItemVo;
import com.hnkjzy.ecg_collection.service.patient.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 患者查询接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/patient", "/patient"})
public class PatientController extends BaseController {

    private final PatientService patientService;

    @GetMapping("/statistics")
    public ApiResponse<PatientDashboardStatVo> statistics() {
        return ApiResponse.success(patientService.getPatientStats());
    }

    @PostMapping("/page")
    public ApiResponse<PageResultVo<PatientPageItemVo>> page(@RequestBody(required = false) PatientPageQueryDto queryDto) {
        return ApiResponse.success(patientService.pagePatientList(queryDto));
    }

    @GetMapping("/detail")
    public ApiResponse<PatientDetailVo> detail(@RequestParam("patientId") Long patientId) {
        return ApiResponse.success(patientService.getPatientDetail(patientId));
    }

    @GetMapping("/dicts")
    public ApiResponse<PatientDictVo> dicts() {
        return ApiResponse.success(patientService.getPatientDicts());
    }
}
