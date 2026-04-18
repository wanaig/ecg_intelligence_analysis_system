package com.hnkjzy.ecg_collection.controller.monitor;

import com.hnkjzy.ecg_collection.common.result.ApiResponse;
import com.hnkjzy.ecg_collection.controller.BaseController;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorAiAccuracyVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorGlobalStatVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorPatientDetailVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorScreenListVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorWardDistributionVo;
import com.hnkjzy.ecg_collection.service.monitor.MonitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 实时监护模块接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/monitor")
public class MonitorController extends BaseController {

    private final MonitorService monitorService;

    @GetMapping("/statistics")
    public ApiResponse<MonitorGlobalStatVo> statistics() {
        return ApiResponse.success(monitorService.getGlobalStat());
    }

    @GetMapping("/patients")
    public ApiResponse<MonitorScreenListVo> patients() {
        return ApiResponse.success(monitorService.getRealtimePatients());
    }

    @GetMapping("/ward-distribution")
    public ApiResponse<MonitorWardDistributionVo> wardDistribution() {
        return ApiResponse.success(monitorService.getWardDistribution());
    }

    @GetMapping("/ai-accuracy")
    public ApiResponse<MonitorAiAccuracyVo> aiAccuracy() {
        return ApiResponse.success(monitorService.getAiAccuracy());
    }

    @GetMapping("/detail")
    public ApiResponse<MonitorPatientDetailVo> detail(@RequestParam("patientId") Long patientId) {
        return ApiResponse.success(monitorService.getPatientDetail(patientId));
    }
}
