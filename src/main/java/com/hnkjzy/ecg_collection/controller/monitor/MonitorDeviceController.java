package com.hnkjzy.ecg_collection.controller.monitor;

import com.hnkjzy.ecg_collection.common.result.ApiResponse;
import com.hnkjzy.ecg_collection.controller.BaseController;
import com.hnkjzy.ecg_collection.model.dto.monitor.MonitorDeviceCreateDto;
import com.hnkjzy.ecg_collection.model.dto.monitor.MonitorDevicePageQueryDto;
import com.hnkjzy.ecg_collection.model.dto.monitor.MonitorDeviceUpdateDto;
import com.hnkjzy.ecg_collection.model.vo.common.PageResultVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorDeviceDeleteResultVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorDeviceDictVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorDeviceMaintainRecordVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorDevicePageItemVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorDeviceSaveResultVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorDeviceUsageDetailVo;
import com.hnkjzy.ecg_collection.service.monitor.MonitorDeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 实时监护-设备管理接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/monitor/device", "/monitor/device"})
public class MonitorDeviceController extends BaseController {

    private final MonitorDeviceService monitorDeviceService;

    @PostMapping("/page")
    public ApiResponse<PageResultVo<MonitorDevicePageItemVo>> page(@RequestBody(required = false) MonitorDevicePageQueryDto queryDto) {
        return ApiResponse.success(monitorDeviceService.pageDevices(queryDto));
    }

    @GetMapping("/dicts")
    public ApiResponse<MonitorDeviceDictVo> dicts() {
        return ApiResponse.success(monitorDeviceService.getDeviceDicts());
    }

    @PostMapping
    public ApiResponse<MonitorDeviceSaveResultVo> create(@RequestBody MonitorDeviceCreateDto createDto) {
        return ApiResponse.success(monitorDeviceService.createDevice(createDto));
    }

    @PutMapping
    public ApiResponse<MonitorDeviceSaveResultVo> update(@RequestBody MonitorDeviceUpdateDto updateDto) {
        return ApiResponse.success(monitorDeviceService.updateDevice(updateDto));
    }

    @DeleteMapping
    public ApiResponse<MonitorDeviceDeleteResultVo> delete(@RequestParam("deviceId") Long deviceId) {
        return ApiResponse.success(monitorDeviceService.deleteDevice(deviceId));
    }

    @GetMapping("/detail")
    public ApiResponse<MonitorDeviceUsageDetailVo> detail(@RequestParam("deviceId") Long deviceId) {
        return ApiResponse.success(monitorDeviceService.getDeviceUsageDetail(deviceId));
    }

    @GetMapping("/maintain-records")
    public ApiResponse<PageResultVo<MonitorDeviceMaintainRecordVo>> maintainRecords(@RequestParam("deviceId") Long deviceId,
                                                                                      @RequestParam(value = "pageNum", required = false) Long pageNum,
                                                                                      @RequestParam(value = "pageSize", required = false) Long pageSize) {
        return ApiResponse.success(monitorDeviceService.pageMaintainRecords(deviceId, pageNum, pageSize));
    }
}
