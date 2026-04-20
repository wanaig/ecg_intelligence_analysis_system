package com.hnkjzy.ecg_collection.service.monitor;

import com.hnkjzy.ecg_collection.model.dto.monitor.MonitorAddKeyDto;
import com.hnkjzy.ecg_collection.model.dto.monitor.MonitorCancelKeyDto;
import com.hnkjzy.ecg_collection.model.dto.monitor.MonitorRealTimeListQueryDto;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorAiAccuracyVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorDeptStatVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorGlobalStatVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorKeyMonitorResultVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorOverviewStatVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorPatientDetailVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorPatientListVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorScreenListVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorWardDistributionVo;
import com.hnkjzy.ecg_collection.service.BaseService;

/**
 * 实时监护模块服务。
 */
public interface MonitorService extends BaseService {

    MonitorOverviewStatVo getOverviewStat();

    MonitorPatientListVo listRealTimePatients(MonitorRealTimeListQueryDto queryDto);

    MonitorPatientListVo listKeyPatients();

    MonitorDeptStatVo getDeptStat();

    MonitorKeyMonitorResultVo addKeyMonitor(MonitorAddKeyDto addKeyDto);

    MonitorKeyMonitorResultVo cancelKeyMonitor(MonitorCancelKeyDto cancelKeyDto);

    MonitorGlobalStatVo getGlobalStat();

    MonitorScreenListVo getRealtimePatients();

    MonitorWardDistributionVo getWardDistribution();

    MonitorAiAccuracyVo getAiAccuracy();

    MonitorPatientDetailVo getPatientDetail(Long patientId);
}
