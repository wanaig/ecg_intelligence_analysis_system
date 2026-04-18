package com.hnkjzy.ecg_collection.controller.monitor;

import com.hnkjzy.ecg_collection.common.result.ApiResponse;
import com.hnkjzy.ecg_collection.controller.BaseController;
import com.hnkjzy.ecg_collection.model.dto.monitor.MonitorQualityControlCreateDto;
import com.hnkjzy.ecg_collection.model.dto.monitor.MonitorQualityControlPageQueryDto;
import com.hnkjzy.ecg_collection.model.dto.monitor.MonitorQualityControlUpdateDto;
import com.hnkjzy.ecg_collection.model.vo.common.PageResultVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorQualityControlDetailVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorQualityControlDictVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorQualityControlPageItemVo;
import com.hnkjzy.ecg_collection.service.monitor.MonitorQualityControlService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 实时监护-质控管理接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/monitor/quality-control")
public class MonitorQualityControlController extends BaseController {

    private final MonitorQualityControlService monitorQualityControlService;

    @PostMapping("/page")
    public ApiResponse<PageResultVo<MonitorQualityControlPageItemVo>> page(@RequestBody(required = false) MonitorQualityControlPageQueryDto queryDto) {
        return ApiResponse.success(monitorQualityControlService.pageQualityControls(queryDto));
    }

    @GetMapping("/dicts")
    public ApiResponse<MonitorQualityControlDictVo> dicts() {
        return ApiResponse.success(monitorQualityControlService.getQualityControlDicts());
    }

    @PostMapping
    public ApiResponse<MonitorQualityControlDetailVo> create(@RequestBody MonitorQualityControlCreateDto createDto) {
        return ApiResponse.success(monitorQualityControlService.createQualityControl(createDto));
    }

    @PutMapping
    public ApiResponse<MonitorQualityControlDetailVo> update(@RequestBody MonitorQualityControlUpdateDto updateDto) {
        return ApiResponse.success(monitorQualityControlService.updateQualityControl(updateDto));
    }

    @GetMapping("/detail")
    public ApiResponse<MonitorQualityControlDetailVo> detail(@RequestParam("qcId") Long qcId) {
        return ApiResponse.success(monitorQualityControlService.getQualityControlDetail(qcId));
    }
}
