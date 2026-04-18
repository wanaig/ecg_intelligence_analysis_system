package com.hnkjzy.ecg_collection.service.monitor;

import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorAiAccuracyVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorGlobalStatVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorPatientDetailVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorScreenListVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorWardDistributionVo;
import com.hnkjzy.ecg_collection.service.BaseService;

/**
 * 实时监护模块服务。
 */
public interface MonitorService extends BaseService {

    MonitorGlobalStatVo getGlobalStat();

    MonitorScreenListVo getRealtimePatients();

    MonitorWardDistributionVo getWardDistribution();

    MonitorAiAccuracyVo getAiAccuracy();

    MonitorPatientDetailVo getPatientDetail(Long patientId);
}
