package com.hnkjzy.ecg_collection.service.monitor;

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
import com.hnkjzy.ecg_collection.service.BaseService;

/**
 * 实时监护-设备管理服务。
 */
public interface MonitorDeviceService extends BaseService {

    PageResultVo<MonitorDevicePageItemVo> pageDevices(MonitorDevicePageQueryDto queryDto);

    MonitorDeviceDictVo getDeviceDicts();

    MonitorDeviceSaveResultVo createDevice(MonitorDeviceCreateDto createDto);

    MonitorDeviceSaveResultVo updateDevice(MonitorDeviceUpdateDto updateDto);

    MonitorDeviceDeleteResultVo deleteDevice(Long deviceId);

    MonitorDeviceUsageDetailVo getDeviceUsageDetail(Long deviceId);

    PageResultVo<MonitorDeviceMaintainRecordVo> pageMaintainRecords(Long deviceId, Long pageNum, Long pageSize);
}
