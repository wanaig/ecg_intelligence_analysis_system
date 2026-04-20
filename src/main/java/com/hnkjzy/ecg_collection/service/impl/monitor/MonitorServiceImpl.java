package com.hnkjzy.ecg_collection.service.impl.monitor;

import com.hnkjzy.ecg_collection.common.exception.BusinessException;
import com.hnkjzy.ecg_collection.common.result.ResultCode;
import com.hnkjzy.ecg_collection.mapper.monitor.MonitorMapper;
import com.hnkjzy.ecg_collection.model.dto.monitor.MonitorAddKeyDto;
import com.hnkjzy.ecg_collection.model.dto.monitor.MonitorCancelKeyDto;
import com.hnkjzy.ecg_collection.model.dto.monitor.MonitorRealTimeListQueryDto;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorAiAccuracyVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorContinuousRecordVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorDeptStatItemVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorDeptStatVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorEcgTrendPointVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorGlobalStatVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorKeyMonitorResultVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorOverviewStatVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorPatientCardVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorPatientDetailVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorPatientListItemVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorPatientListVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorScreenListVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorWardDistributionItemVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorWardDistributionVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorWarningHistoryVo;
import com.hnkjzy.ecg_collection.service.impl.BaseServiceImpl;
import com.hnkjzy.ecg_collection.service.monitor.MonitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * 实时监护模块服务实现。
 */
@Service
@RequiredArgsConstructor
public class MonitorServiceImpl extends BaseServiceImpl implements MonitorService {

    private static final List<String> REAL_TIME_ACTION_PERMISSIONS = List.of("monitor:opt:addKey");
    private static final List<String> KEY_MONITOR_ACTION_PERMISSIONS = List.of("monitor:opt:cancelKey");

    private final MonitorMapper monitorMapper;

    @Override
    public MonitorOverviewStatVo getOverviewStat() {
        MonitorOverviewStatVo statVo = monitorMapper.selectOverviewStat();
        if (statVo == null) {
            statVo = new MonitorOverviewStatVo();
            statVo.setTodayCollect(0L);
            statVo.setPendingAnalyse(0L);
            statVo.setPendingAudit(0L);
            statVo.setAlertCount(0L);
            return statVo;
        }

        statVo.setTodayCollect(defaultLong(statVo.getTodayCollect()));
        statVo.setPendingAnalyse(defaultLong(statVo.getPendingAnalyse()));
        statVo.setPendingAudit(defaultLong(statVo.getPendingAudit()));
        statVo.setAlertCount(defaultLong(statVo.getAlertCount()));
        return statVo;
    }

    @Override
    public MonitorPatientListVo listRealTimePatients(MonitorRealTimeListQueryDto queryDto) {
        MonitorRealTimeListQueryDto query = normalizeRealTimeQuery(queryDto);
        List<MonitorPatientListItemVo> list = monitorMapper.selectRealTimeList(query);
        if (list == null) {
            list = Collections.emptyList();
        }

        for (MonitorPatientListItemVo item : list) {
            item.setActionPermissions(REAL_TIME_ACTION_PERMISSIONS);
        }

        MonitorPatientListVo response = new MonitorPatientListVo();
        response.setList(list);
        return response;
    }

    @Override
    public MonitorPatientListVo listKeyPatients() {
        List<MonitorPatientListItemVo> list = monitorMapper.selectKeyList();
        if (list == null) {
            list = Collections.emptyList();
        }

        for (MonitorPatientListItemVo item : list) {
            item.setActionPermissions(KEY_MONITOR_ACTION_PERMISSIONS);
        }

        MonitorPatientListVo response = new MonitorPatientListVo();
        response.setList(list);
        return response;
    }

    @Override
    public MonitorDeptStatVo getDeptStat() {
        List<MonitorDeptStatItemVo> list = monitorMapper.selectDeptStat();
        MonitorDeptStatVo response = new MonitorDeptStatVo();
        response.setList(list == null ? Collections.emptyList() : list);
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MonitorKeyMonitorResultVo addKeyMonitor(MonitorAddKeyDto addKeyDto) {
        if (addKeyDto == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "请求体不能为空");
        }

        Long patientId = requirePatientId(addKeyDto.getPatientId());
        String monitorType = trimToNull(addKeyDto.getMonitorType());
        if (!StringUtils.hasText(monitorType)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "monitorType 参数不合法");
        }

        requireMonitorPatientExists(patientId);
        monitorMapper.updateKeyMonitorFlag(patientId, 1);

        MonitorKeyMonitorResultVo resultVo = new MonitorKeyMonitorResultVo();
        resultVo.setPatientId(patientId);
        resultVo.setKeyMonitor(true);
        resultVo.setMonitorStatus("重点监护");
        resultVo.setMonitorType(monitorType);
        return resultVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MonitorKeyMonitorResultVo cancelKeyMonitor(MonitorCancelKeyDto cancelKeyDto) {
        if (cancelKeyDto == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "请求体不能为空");
        }

        Long patientId = requirePatientId(cancelKeyDto.getPatientId());
        requireMonitorPatientExists(patientId);
        monitorMapper.updateKeyMonitorFlag(patientId, 0);

        MonitorKeyMonitorResultVo resultVo = new MonitorKeyMonitorResultVo();
        resultVo.setPatientId(patientId);
        resultVo.setKeyMonitor(false);
        resultVo.setMonitorStatus("普通监护");
        resultVo.setMonitorType("普通监护");
        return resultVo;
    }

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

    private MonitorRealTimeListQueryDto normalizeRealTimeQuery(MonitorRealTimeListQueryDto queryDto) {
        MonitorRealTimeListQueryDto query = queryDto == null ? new MonitorRealTimeListQueryDto() : queryDto;
        if (query.getWardId() != null && query.getWardId() <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "wardId 参数不合法");
        }
        if (query.getLevel() != null && (query.getLevel() < 0 || query.getLevel() > 3)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "level 参数不合法");
        }
        return query;
    }

    private Long requirePatientId(Long patientId) {
        if (patientId == null || patientId <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "patientId 参数不合法");
        }
        return patientId;
    }

    private void requireMonitorPatientExists(Long patientId) {
        Long count = monitorMapper.countMonitorPatient(patientId);
        if (count == null || count <= 0) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "患者监护数据不存在");
        }
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private Long defaultLong(Long value) {
        return value == null ? 0L : value;
    }
}
