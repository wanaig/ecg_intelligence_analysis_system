package com.hnkjzy.ecg_collection.service.impl.monitor;

import com.hnkjzy.ecg_collection.common.exception.BusinessException;
import com.hnkjzy.ecg_collection.common.result.ResultCode;
import com.hnkjzy.ecg_collection.mapper.monitor.MonitorMapper;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorAiAccuracyVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorContinuousRecordVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorEcgTrendPointVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorGlobalStatVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorPatientCardVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorPatientDetailVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorScreenListVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorWardDistributionItemVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorWardDistributionVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorWarningHistoryVo;
import com.hnkjzy.ecg_collection.service.impl.BaseServiceImpl;
import com.hnkjzy.ecg_collection.service.monitor.MonitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * 实时监护模块服务实现。
 */
@Service
@RequiredArgsConstructor
public class MonitorServiceImpl extends BaseServiceImpl implements MonitorService {

    private final MonitorMapper monitorMapper;

    @Override
    public MonitorGlobalStatVo getGlobalStat() {
        MonitorGlobalStatVo statVo = monitorMapper.selectGlobalStat();
        if (statVo == null) {
            statVo = new MonitorGlobalStatVo();
            statVo.setTodayCollect(0L);
            statVo.setPendingAnalyse(0L);
            statVo.setPendingAudit(0L);
            statVo.setAlertTotal(0L);
            return statVo;
        }
        statVo.setTodayCollect(defaultLong(statVo.getTodayCollect()));
        statVo.setPendingAnalyse(defaultLong(statVo.getPendingAnalyse()));
        statVo.setPendingAudit(defaultLong(statVo.getPendingAudit()));
        statVo.setAlertTotal(defaultLong(statVo.getAlertTotal()));
        return statVo;
    }

    @Override
    public MonitorScreenListVo getRealtimePatients() {
        List<MonitorPatientCardVo> list = monitorMapper.selectRealtimePatients();
        MonitorScreenListVo response = new MonitorScreenListVo();
        response.setList(list == null ? Collections.emptyList() : list);
        return response;
    }

    @Override
    public MonitorWardDistributionVo getWardDistribution() {
        List<MonitorWardDistributionItemVo> dataList = monitorMapper.selectWardDistribution();
        MonitorWardDistributionVo response = new MonitorWardDistributionVo();
        response.setDataList(dataList == null ? Collections.emptyList() : dataList);
        return response;
    }

    @Override
    public MonitorAiAccuracyVo getAiAccuracy() {
        BigDecimal accuracyRate = monitorMapper.selectAiAccuracyRate();
        MonitorAiAccuracyVo response = new MonitorAiAccuracyVo();
        response.setAccuracyRate(accuracyRate == null ? BigDecimal.ZERO : accuracyRate);
        return response;
    }

    @Override
    public MonitorPatientDetailVo getPatientDetail(Long patientId) {
        if (patientId == null || patientId <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "patientId 参数不合法");
        }

        MonitorPatientDetailVo detailVo = monitorMapper.selectMonitorPatientBase(patientId);
        if (detailVo == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "患者监护数据不存在");
        }

        List<MonitorEcgTrendPointVo> ecgTrend = monitorMapper.selectEcgTrend(patientId);
        List<MonitorWarningHistoryVo> warningHistory = monitorMapper.selectWarningHistory(patientId);
        List<MonitorContinuousRecordVo> continuousRecords = monitorMapper.selectContinuousRecords(patientId);

        detailVo.setEcgTrend(ecgTrend == null ? Collections.emptyList() : ecgTrend);
        detailVo.setWarningHistory(warningHistory == null ? Collections.emptyList() : warningHistory);
        detailVo.setContinuousRecords(continuousRecords == null ? Collections.emptyList() : continuousRecords);
        return detailVo;
    }

    private Long defaultLong(Long value) {
        return value == null ? 0L : value;
    }
}
