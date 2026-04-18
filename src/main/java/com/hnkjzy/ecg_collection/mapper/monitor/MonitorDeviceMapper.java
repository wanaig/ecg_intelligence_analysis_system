package com.hnkjzy.ecg_collection.mapper.monitor;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnkjzy.ecg_collection.mapper.BaseMapperX;
import com.hnkjzy.ecg_collection.model.dto.monitor.MonitorDevicePageQueryDto;
import com.hnkjzy.ecg_collection.model.entity.monitor.EcgDeviceEntity;
import com.hnkjzy.ecg_collection.model.vo.common.DictOptionVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorDeviceBasicInfoVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorDeviceCurrentPatientVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorDeviceMaintainRecordVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorDevicePageItemVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorDeviceSaveResultVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorDeviceUsageStatVo;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 实时监护-设备管理 Mapper。
 */
public interface MonitorDeviceMapper extends BaseMapperX<EcgDeviceEntity> {

    IPage<MonitorDevicePageItemVo> selectDevicePage(Page<MonitorDevicePageItemVo> page,
                                                    @Param("req") MonitorDevicePageQueryDto req,
                                                    @Param("deviceTypeCode") Integer deviceTypeCode,
                                                    @Param("purchaseStart") LocalDate purchaseStart,
                                                    @Param("purchaseEnd") LocalDate purchaseEnd);

    List<DictOptionVo> selectWardOptions();

    String selectWardNameById(@Param("wardId") Long wardId);

    Long countDeviceCode(@Param("deviceCode") String deviceCode, @Param("excludeDeviceId") Long excludeDeviceId);

    int insertDevice(EcgDeviceEntity entity);

    int updateDeviceById(EcgDeviceEntity entity);

    MonitorDeviceSaveResultVo selectDeviceSaveResult(@Param("deviceId") Long deviceId);

    Integer countActiveMonitorBinding(@Param("deviceId") Long deviceId);

    int logicalDeleteDevice(@Param("deviceId") Long deviceId);

    MonitorDeviceBasicInfoVo selectDeviceBasicInfo(@Param("deviceId") Long deviceId);

    MonitorDeviceUsageStatVo selectDeviceUsageStat(@Param("deviceId") Long deviceId);

    List<String> selectDeviceDeptTags(@Param("deviceId") Long deviceId);

    List<MonitorDeviceCurrentPatientVo> selectCurrentPatients(@Param("deviceId") Long deviceId);

    IPage<MonitorDeviceMaintainRecordVo> selectMaintainRecordPage(Page<MonitorDeviceMaintainRecordVo> page,
                                                                   @Param("deviceId") Long deviceId);
}
