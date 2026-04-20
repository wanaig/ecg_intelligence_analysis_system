package com.hnkjzy.ecg_collection.mapper.monitor;

import com.hnkjzy.ecg_collection.mapper.BaseMapperX;
import com.hnkjzy.ecg_collection.model.dto.monitor.MonitorRealTimeListQueryDto;
import com.hnkjzy.ecg_collection.model.entity.monitor.EcgRealTimeMonitorEntity;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorContinuousRecordVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorDeptStatItemVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorEcgTrendPointVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorGlobalStatVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorOverviewStatVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorPatientCardVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorPatientDetailVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorPatientListItemVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorWarningHistoryVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorWardDistributionItemVo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 实时监护 Mapper。
 */
public interface MonitorMapper extends BaseMapperX<EcgRealTimeMonitorEntity> {

    MonitorOverviewStatVo selectOverviewStat();

    List<MonitorPatientListItemVo> selectRealTimeList(@Param("req") MonitorRealTimeListQueryDto req);

    List<MonitorPatientListItemVo> selectKeyList();

    List<MonitorDeptStatItemVo> selectDeptStat();

    Long countMonitorPatient(@Param("patientId") Long patientId);

    int updateKeyMonitorFlag(@Param("patientId") Long patientId, @Param("isKeyMonitor") Integer isKeyMonitor);

    MonitorGlobalStatVo selectGlobalStat();

    List<MonitorPatientCardVo> selectRealtimePatients();

    List<MonitorWardDistributionItemVo> selectWardDistribution();

    BigDecimal selectAiAccuracyRate();

    MonitorPatientDetailVo selectMonitorPatientBase(@Param("patientId") Long patientId);

    List<MonitorEcgTrendPointVo> selectEcgTrend(@Param("patientId") Long patientId);

    List<MonitorWarningHistoryVo> selectWarningHistory(@Param("patientId") Long patientId);

    List<MonitorContinuousRecordVo> selectContinuousRecords(@Param("patientId") Long patientId);
}
