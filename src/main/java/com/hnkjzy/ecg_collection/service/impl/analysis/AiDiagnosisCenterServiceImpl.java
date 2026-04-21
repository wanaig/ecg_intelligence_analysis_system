package com.hnkjzy.ecg_collection.service.impl.analysis;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnkjzy.ecg_collection.common.exception.BusinessException;
import com.hnkjzy.ecg_collection.common.result.ResultCode;
import com.hnkjzy.ecg_collection.mapper.analysis.AiDiagnosisCenterMapper;
import com.hnkjzy.ecg_collection.mapper.system.SystemUserMapper;
import com.hnkjzy.ecg_collection.model.dto.analysis.AiDiagnosisAuditSubmitDto;
import com.hnkjzy.ecg_collection.model.dto.analysis.AiDiagnosisAuditSubmitV2Dto;
import com.hnkjzy.ecg_collection.model.dto.analysis.AiDiagnosisDashboardPageQueryDto;
import com.hnkjzy.ecg_collection.model.dto.analysis.AiDiagnosisDashboardTimeRangeQueryDto;
import com.hnkjzy.ecg_collection.model.dto.analysis.AiDiagnosisPageQueryDto;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisAbnormalTypeRatioVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisAbnormalPointVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisAuditBaseVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisAuditDetailVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisAuditSubmitResultVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisAuditSubmitV2ResultVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisAuditWarningVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisCoreMetricsSnapshotVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisCoreMetricsVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisDailyTrendCountVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisDashboardPageItemVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisDetailVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisEngineStatusVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisLiteDetailVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisOverviewVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisPageItemVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisPageResultVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisStatusDictVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisTrendVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisWaveformAnnotationVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisWaveformPointVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisWaveformSegmentVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisWaveformVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisDashboardPageResultVo;
import com.hnkjzy.ecg_collection.model.vo.common.DictOptionVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemUserOperatorVo;
import com.hnkjzy.ecg_collection.service.analysis.AiDiagnosisCenterService;
import com.hnkjzy.ecg_collection.service.impl.BaseServiceImpl;
import com.hnkjzy.ecg_collection.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI diagnosis center service implementation.
 */
@Service
@RequiredArgsConstructor
public class AiDiagnosisCenterServiceImpl extends BaseServiceImpl implements AiDiagnosisCenterService {

    private static final long DEFAULT_PAGE_NUM = 1L;
    private static final long DEFAULT_PAGE_SIZE = 10L;
    private static final long MAX_PAGE_SIZE = 200L;
    private static final long REPORT_ID_BASE = 2300L;
    private static final long REPORT_ID_MAX_ALLOWED = 9_999_999_999L;
    private static final int DEFAULT_SAMPLING_RATE = 250;
    private static final int DEFAULT_LEAD_COUNT = 12;
    private static final int WAVEFORM_POINT_COUNT = 1200;
        private static final int DEFAULT_DASHBOARD_RANGE_DAYS = 30;
        private static final int DEFAULT_TREND_DAYS = 7;
        private static final int MAX_TREND_DAYS = 30;

        private static final String AUDIT_RESULT_PASS = "PASS";
        private static final String AUDIT_RESULT_REJECT = "REJECT";

    private static final DateTimeFormatter REPORT_NO_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        private static final List<DateTimeFormatter> DATE_TIME_FORMATTERS = List.of(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
            DateTimeFormatter.ISO_LOCAL_DATE_TIME
        );

    private static final List<String> AUDIT_PERMISSION_CODES = List.of(
            "report:audit",
            "diagnosis:write",
            "ai:diagnosis:audit",
            "analysis:ai-diagnosis:audit"
    );

    private final AiDiagnosisCenterMapper aiDiagnosisCenterMapper;
    private final SystemUserMapper systemUserMapper;
    private final JwtUtil jwtUtil;

    @Override
    public AiDiagnosisEngineStatusVo getEngineStatus() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();

        AiDiagnosisEngineStatusVo statusVo = aiDiagnosisCenterMapper.selectEngineStatus(startOfDay, now.plusSeconds(1));
        if (statusVo == null) {
            statusVo = new AiDiagnosisEngineStatusVo();
        }

        statusVo.setEngineStatus(defaultString(statusVo.getEngineStatus(), "IDLE"));
        statusVo.setEngineStatusText(defaultString(statusVo.getEngineStatusText(), "空闲"));
        statusVo.setEngineVersion(defaultString(statusVo.getEngineVersion(), "v3.2.1"));
        statusVo.setRunningInstanceCount(defaultLong(statusVo.getRunningInstanceCount()));
        statusVo.setQueueBacklogCount(defaultLong(statusVo.getQueueBacklogCount()));
        statusVo.setTodayAnalysisCount(defaultLong(statusVo.getTodayAnalysisCount()));
        statusVo.setAvgAnalysisSeconds(defaultDecimal(statusVo.getAvgAnalysisSeconds()));
        statusVo.setLastHeartbeatTime(statusVo.getLastHeartbeatTime() == null ? now : statusVo.getLastHeartbeatTime());
        statusVo.setStatusMessage(defaultString(statusVo.getStatusMessage(), "AI引擎空闲，暂无待处理任务"));
        return statusVo;
    }

    @Override
    public AiDiagnosisCoreMetricsVo getDashboardCoreMetrics(AiDiagnosisDashboardTimeRangeQueryDto queryDto) {
        AiDiagnosisDashboardTimeRangeQueryDto query = normalizeDashboardRangeQuery(queryDto);
        DateRange currentRange = resolveDateTimeRange(query.getStartTime(), query.getEndTime(), DEFAULT_DASHBOARD_RANGE_DAYS);

        AiDiagnosisCoreMetricsSnapshotVo currentSnapshot = normalizeCoreMetricsSnapshot(
                aiDiagnosisCenterMapper.selectCoreMetricsSnapshot(currentRange.getStartTime(), currentRange.getEndTime())
        );

        long seconds = Math.max(Duration.between(currentRange.getStartTime(), currentRange.getEndTime()).getSeconds(), 24L * 3600L);
        LocalDateTime previousStart = currentRange.getStartTime().minusSeconds(seconds);
        LocalDateTime previousEnd = currentRange.getStartTime();

        AiDiagnosisCoreMetricsSnapshotVo previousSnapshot = normalizeCoreMetricsSnapshot(
                aiDiagnosisCenterMapper.selectCoreMetricsSnapshot(previousStart, previousEnd)
        );

        AiDiagnosisCoreMetricsVo metricsVo = new AiDiagnosisCoreMetricsVo();
        metricsVo.setDiagnosisTotal(currentSnapshot.getDiagnosisTotal());
        metricsVo.setDiagnosisYoYRate(calculateChangeRate(currentSnapshot.getDiagnosisTotal(), previousSnapshot.getDiagnosisTotal()));

        metricsVo.setPendingAuditCount(currentSnapshot.getPendingAuditCount());
        metricsVo.setPendingAuditYoYRate(calculateChangeRate(currentSnapshot.getPendingAuditCount(), previousSnapshot.getPendingAuditCount()));

        metricsVo.setAuditedPassCount(currentSnapshot.getAuditedPassCount());
        metricsVo.setAuditedPassYoYRate(calculateChangeRate(currentSnapshot.getAuditedPassCount(), previousSnapshot.getAuditedPassCount()));

        metricsVo.setAbnormalCount(currentSnapshot.getAbnormalCount());
        metricsVo.setAbnormalYoYRate(calculateChangeRate(currentSnapshot.getAbnormalCount(), previousSnapshot.getAbnormalCount()));

        metricsVo.setNormalCount(currentSnapshot.getNormalCount());
        metricsVo.setNormalYoYRate(calculateChangeRate(currentSnapshot.getNormalCount(), previousSnapshot.getNormalCount()));
        return metricsVo;
    }

    @Override
    public AiDiagnosisTrendVo getWarningTrend(String timeRange) {
        int days = parseTrendDays(timeRange);
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1L);

        List<AiDiagnosisDailyTrendCountVo> rows = aiDiagnosisCenterMapper.selectTrendCounts(startDate, endDate);
        Map<String, AiDiagnosisDailyTrendCountVo> rowMap = new HashMap<>();
        if (rows != null) {
            for (AiDiagnosisDailyTrendCountVo row : rows) {
                if (row != null && StringUtils.hasText(row.getStatDate())) {
                    rowMap.put(row.getStatDate(), row);
                }
            }
        }

        List<String> dateList = new ArrayList<>(days);
        List<Long> pendingAuditList = new ArrayList<>(days);
        List<Long> passList = new ArrayList<>(days);
        List<Long> rejectList = new ArrayList<>(days);

        for (int i = 0; i < days; i++) {
            String dateText = startDate.plusDays(i).format(DateTimeFormatter.ISO_LOCAL_DATE);
            dateList.add(dateText);

            AiDiagnosisDailyTrendCountVo row = rowMap.get(dateText);
            pendingAuditList.add(row == null ? 0L : defaultLong(row.getPendingAuditCount()));
            passList.add(row == null ? 0L : defaultLong(row.getPassCount()));
            rejectList.add(row == null ? 0L : defaultLong(row.getRejectCount()));
        }

        AiDiagnosisTrendVo trendVo = new AiDiagnosisTrendVo();
        trendVo.setDateList(dateList);
        trendVo.setPendingAuditList(pendingAuditList);
        trendVo.setPassList(passList);
        trendVo.setRejectList(rejectList);
        return trendVo;
    }

    @Override
    public List<AiDiagnosisAbnormalTypeRatioVo> getAbnormalTypeRatios(AiDiagnosisDashboardTimeRangeQueryDto queryDto) {
        AiDiagnosisDashboardTimeRangeQueryDto query = normalizeDashboardRangeQuery(queryDto);
        DateRange dateRange = resolveDateTimeRange(query.getStartTime(), query.getEndTime(), DEFAULT_DASHBOARD_RANGE_DAYS);

        List<AiDiagnosisAbnormalTypeRatioVo> rows = aiDiagnosisCenterMapper.selectAbnormalTypeRatios(
                dateRange.getStartTime(),
                dateRange.getEndTime()
        );
        if (rows == null || rows.isEmpty()) {
            return Collections.emptyList();
        }

        long total = 0L;
        for (AiDiagnosisAbnormalTypeRatioVo row : rows) {
            if (row != null) {
                total += defaultLong(row.getCount());
            }
        }

        for (AiDiagnosisAbnormalTypeRatioVo row : rows) {
            if (row == null) {
                continue;
            }
            long count = defaultLong(row.getCount());
            row.setCount(count);
            row.setAbnormalType(defaultString(trimToNull(row.getAbnormalType()), "其他异常"));
            row.setRatio(calculateRatio(count, total));
        }
        return rows;
    }

    @Override
    public AnalysisDashboardPageResultVo<AiDiagnosisDashboardPageItemVo> pageDashboardDiagnoses(AiDiagnosisDashboardPageQueryDto queryDto) {
        AiDiagnosisDashboardPageQueryDto query = normalizeDashboardPageQuery(queryDto);
        DateRange dateRange = resolveDateTimeRange(query.getStartTime(), query.getEndTime(), DEFAULT_DASHBOARD_RANGE_DAYS);

        long pageNum = normalizePageNum(query.getPageNum());
        long pageSize = normalizePageSize(query.getPageSize());
        Page<AiDiagnosisDashboardPageItemVo> page = new Page<>(pageNum, pageSize);

        IPage<AiDiagnosisDashboardPageItemVo> pageData = aiDiagnosisCenterMapper.selectDashboardPage(
                page,
                query,
                dateRange.getStartTime(),
                dateRange.getEndTime()
        );

        List<AiDiagnosisDashboardPageItemVo> records = pageData.getRecords();
        if (records == null) {
            records = Collections.emptyList();
        }
        for (AiDiagnosisDashboardPageItemVo item : records) {
            if (item == null) {
                continue;
            }
            item.setAbnormalCount(defaultInteger(item.getAbnormalCount()));
            item.setConfidence(defaultDecimal(item.getConfidence()));
            item.setReportStatus(item.getReportStatus() == null ? 1 : item.getReportStatus());
            item.setReportStatusText(defaultString(item.getReportStatusText(), reportStatusDetailText(item.getReportStatus())));
            item.setWarningLevel(item.getWarningLevel() == null ? 0 : item.getWarningLevel());
            item.setWarningLevelText(defaultString(item.getWarningLevelText(), warningLevelText(item.getWarningLevel())));
        }

        return AnalysisDashboardPageResultVo.<AiDiagnosisDashboardPageItemVo>builder()
                .total(pageData.getTotal())
                .pages(pageData.getPages())
                .list(records)
                .build();
    }

    @Override
    public AiDiagnosisLiteDetailVo getLiteDetail(String diagnosisId) {
        String diagnosisKey = trimToNull(diagnosisId);
        if (diagnosisKey == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "diagnosisId 参数不合法");
        }

        AiDiagnosisLiteDetailVo detailVo = aiDiagnosisCenterMapper.selectLiteDetail(diagnosisKey);
        if (detailVo == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "AI 诊断记录不存在");
        }

        normalizeLiteDetail(detailVo);
        return detailVo;
    }

    @Override
    public AiDiagnosisAuditDetailVo getAuditDetail(String diagnosisId) {
        String diagnosisKey = trimToNull(diagnosisId);
        if (diagnosisKey == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "diagnosisId 参数不合法");
        }

        AiDiagnosisAuditDetailVo detailVo = aiDiagnosisCenterMapper.selectAuditDetail(diagnosisKey);
        if (detailVo == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "AI 诊断记录不存在");
        }

        detailVo.setAbnormalCount(defaultInteger(detailVo.getAbnormalCount()));
        detailVo.setAbnormalLevel(defaultInteger(detailVo.getAbnormalLevel()));
        detailVo.setConfidence(defaultDecimal(detailVo.getConfidence()));
        detailVo.setReportStatus(detailVo.getReportStatus() == null ? 1 : detailVo.getReportStatus());
        detailVo.setReportStatusText(defaultString(detailVo.getReportStatusText(), reportStatusDetailText(detailVo.getReportStatus())));

        List<AiDiagnosisAuditWarningVo> warningList = aiDiagnosisCenterMapper.selectAuditWarnings(
                detailVo.getAiDiagnosisId(),
                detailVo.getEcgId()
        );
        if (warningList == null) {
            warningList = Collections.emptyList();
        }
        for (AiDiagnosisAuditWarningVo warningVo : warningList) {
            if (warningVo == null) {
                continue;
            }
            warningVo.setWarningLevel(warningVo.getWarningLevel() == null ? 0 : warningVo.getWarningLevel());
            warningVo.setWarningLevelText(defaultString(warningVo.getWarningLevelText(), warningLevelText(warningVo.getWarningLevel())));
            warningVo.setHandleStatus(warningVo.getHandleStatus() == null ? 0 : warningVo.getHandleStatus());
            warningVo.setHandleStatusText(defaultString(warningVo.getHandleStatusText(), handleStatusText(warningVo.getHandleStatus())));
        }
        detailVo.setWarningList(warningList);
        return detailVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AiDiagnosisAuditSubmitV2ResultVo submitAuditV2(AiDiagnosisAuditSubmitV2Dto submitDto, HttpServletRequest request) {
        AiDiagnosisAuditSubmitV2Dto submit = normalizeAuditSubmitV2(submitDto);

        SystemUserOperatorVo operator = resolveCurrentOperator(request);
        validateAuditPermission(operator);

        AiDiagnosisAuditBaseVo auditBase = aiDiagnosisCenterMapper.selectAuditBase(submit.getDiagnosisId());
        if (auditBase == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "AI 诊断记录不存在");
        }

        AiDiagnosisLiteDetailVo liteDetail = aiDiagnosisCenterMapper.selectLiteDetail(submit.getDiagnosisId());
        if (liteDetail == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "AI 诊断记录不存在");
        }
        normalizeLiteDetail(liteDetail);

        Integer currentStatus = auditBase.getReportStatus();
        if (currentStatus != null && (currentStatus == 2 || currentStatus == 4)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "该诊断记录已审核，不能重复提交");
        }

        AuditDecision decision = resolveAuditDecision(submit.getFinalAuditResult());
        LocalDateTime now = LocalDateTime.now();
        String operatorName = resolveOperatorName(operator);

        String auditOpinion = trimToNull(submit.getAuditOpinion());
        if (auditOpinion == null) {
            auditOpinion = decision.getReportStatus() == 2 ? "审核通过" : "审核驳回";
        }

        Long reportId = auditBase.getReportId();
        String reportNo = auditBase.getReportNo();
        if (reportId == null) {
            reportId = nextReportId();
            reportNo = buildReportNo(now, reportId);
            aiDiagnosisCenterMapper.insertAuditReportV2(
                    reportId,
                    reportNo,
                    auditBase.getEcgId(),
                    auditBase.getAiDiagnosisId(),
                    auditBase.getPatientId(),
                    auditBase.getPatientName(),
                    auditBase.getGender(),
                    auditBase.getAge(),
                    auditBase.getHospitalNo(),
                    auditBase.getCollectionTime(),
                    auditBase.getAiConclusion(),
                    submit.getDoctorConclusion(),
                    submit.getDoctorSuggestion(),
                    operator.getUserId(),
                    operatorName,
                    now,
                    now,
                    auditOpinion,
                    decision.getReportStatus()
            );
        } else {
            aiDiagnosisCenterMapper.updateReportAuditV2(
                    reportId,
                    submit.getDoctorConclusion(),
                    submit.getDoctorSuggestion(),
                    operator.getUserId(),
                    operatorName,
                    now,
                    auditOpinion,
                    decision.getReportStatus()
            );
        }

        int displayStatus = decision.getReportStatus() == 2 ? 2 : 3;
        aiDiagnosisCenterMapper.updateCollectionReportStatus(auditBase.getEcgId(), decision.getReportStatus(), displayStatus);

        Integer adoptAiFlag = normalizeBinaryFlag(submit.getAdoptAiFlag(), "adoptAiFlag");
        Integer markNormalFlag = normalizeBinaryFlag(submit.getMarkNormalFlag(), "markNormalFlag");

        int isAbnormal = markNormalFlag == 1 ? 0 : 1;
        int abnormalLevel = markNormalFlag == 1 ? 0 : Math.max(1, defaultInteger(liteDetail.getAbnormalLevel()));
        int abnormalCount = markNormalFlag == 1 ? 0 : Math.max(1, defaultInteger(liteDetail.getAbnormalCount()));
        String abnormalType;
        if (markNormalFlag == 1) {
            abnormalType = null;
        } else if (adoptAiFlag == 1) {
            abnormalType = normalizeAbnormalType(liteDetail.getAbnormalType());
        } else {
            abnormalType = normalizeAbnormalType(liteDetail.getAbnormalType());
            if (abnormalType == null) {
                abnormalType = "人工复核异常";
            }
        }

        aiDiagnosisCenterMapper.updateAiDiagnosisAuditSync(
                auditBase.getAiDiagnosisId(),
                isAbnormal,
                abnormalLevel,
                abnormalType,
                abnormalCount
        );

        Long warningCount = defaultLong(aiDiagnosisCenterMapper.countWarningsByDiagnosis(auditBase.getAiDiagnosisId(), auditBase.getEcgId()));
        if (warningCount > 0) {
            int handleStatus = resolveWarningHandleStatus(decision.getReportStatus(), markNormalFlag);
            aiDiagnosisCenterMapper.updateWarningHandleStatusByDiagnosis(
                    auditBase.getAiDiagnosisId(),
                    auditBase.getEcgId(),
                    handleStatus,
                    operator.getUserId(),
                    operatorName,
                    now,
                    buildWarningOpinion(decision.getReportStatus(), markNormalFlag, auditOpinion)
            );
        }

        AiDiagnosisAuditSubmitV2ResultVo resultVo = new AiDiagnosisAuditSubmitV2ResultVo();
        resultVo.setDiagnosisId(auditBase.getDiagnosisId());
        resultVo.setFinalAuditResult(decision.getResultText());
        resultVo.setReportStatus(decision.getReportStatus());
        resultVo.setStatus(reportStatusDetailText(decision.getReportStatus()));
        resultVo.setAuditedBy(operatorName);
        resultVo.setAuditedTime(now);
        resultVo.setReportId(reportId);
        resultVo.setReportNo(reportNo);
        resultVo.setWarningSyncCount(Math.toIntExact(Math.min(warningCount, Integer.MAX_VALUE)));
        resultVo.setMarkNormalFlag(markNormalFlag);
        resultVo.setAdoptAiFlag(adoptAiFlag);
        return resultVo;
    }

    @Override
    public AiDiagnosisOverviewVo getOverview() {
        AiDiagnosisOverviewVo overviewVo = aiDiagnosisCenterMapper.selectOverview();
        if (overviewVo == null) {
            overviewVo = new AiDiagnosisOverviewVo();
        }
        overviewVo.setTotalCount(defaultLong(overviewVo.getTotalCount()));
        overviewVo.setPendingAuditCount(defaultLong(overviewVo.getPendingAuditCount()));
        overviewVo.setAuditedCount(defaultLong(overviewVo.getAuditedCount()));
        overviewVo.setAvgConfidence(defaultDecimal(overviewVo.getAvgConfidence()));
        return overviewVo;
    }

    @Override
    public AiDiagnosisPageResultVo pageDiagnoses(AiDiagnosisPageQueryDto queryDto) {
        AiDiagnosisPageQueryDto query = normalizePageQuery(queryDto);
        Integer statusCode = parseStatusCode(query.getStatus(), false);

        long pageNum = normalizePageNum(query.getPageNum());
        long pageSize = normalizePageSize(query.getPageSize());

        Page<AiDiagnosisPageItemVo> page = new Page<>(pageNum, pageSize);
        IPage<AiDiagnosisPageItemVo> pageData = aiDiagnosisCenterMapper.selectDiagnosisPage(page, query, statusCode);

        List<AiDiagnosisPageItemVo> records = pageData.getRecords();
        if (records == null) {
            records = Collections.emptyList();
        }

        return AiDiagnosisPageResultVo.builder()
                .total(pageData.getTotal())
                .pages(pageData.getPages())
                .list(records)
                .build();
    }

    @Override
    public AiDiagnosisStatusDictVo getStatusDicts() {
        AiDiagnosisStatusDictVo dictVo = new AiDiagnosisStatusDictVo();

        List<DictOptionVo> statusList = new ArrayList<>();
        statusList.add(buildOption("", "全部状态"));
        statusList.add(buildOption("待审核", "待审核"));
        statusList.add(buildOption("已审核", "已审核"));

        dictVo.setStatusList(statusList);
        return dictVo;
    }

    @Override
    public AiDiagnosisDetailVo getDiagnosisDetail(String diagnosisId) {
        String diagnosisKey = trimToNull(diagnosisId);
        if (diagnosisKey == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "diagnosisId 参数不合法");
        }

        AiDiagnosisDetailVo detailVo = aiDiagnosisCenterMapper.selectDiagnosisDetail(diagnosisKey);
        if (detailVo == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "AI 诊断记录不存在");
        }

        detailVo.setAbnormalCount(defaultInteger(detailVo.getAbnormalCount()));
        detailVo.setConfidence(defaultDecimal(detailVo.getConfidence()));
        if (!StringUtils.hasText(detailVo.getStatus())) {
            detailVo.setStatus(reportStatusToText(detailVo.getReportStatus()));
        }
        detailVo.setAbnormalPointList(buildAbnormalPointList(detailVo.getAbnormalType(), detailVo.getAbnormalCount(), detailVo.getConfidence()));
        return detailVo;
    }

    @Override
    public AiDiagnosisWaveformVo getWaveform(Long ecgId) {
        if (ecgId == null || ecgId <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "ecgId 参数不合法");
        }

        AiDiagnosisWaveformVo waveformVo = aiDiagnosisCenterMapper.selectWaveformMeta(ecgId);
        if (waveformVo == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "心电记录不存在");
        }

        int samplingRate = waveformVo.getSamplingRate() == null ? DEFAULT_SAMPLING_RATE : waveformVo.getSamplingRate();
        int leadCount = waveformVo.getLeadCount() == null ? DEFAULT_LEAD_COUNT : waveformVo.getLeadCount();
        waveformVo.setSamplingRate(samplingRate);
        waveformVo.setLeadCount(leadCount);

        List<AiDiagnosisWaveformPointVo> waveform = buildWaveform(ecgId, samplingRate, leadCount);
        waveformVo.setWaveform(waveform);
        waveformVo.setSegments(buildWaveSegments(waveform.size()));
        waveformVo.setAnnotations(buildWaveAnnotations(waveform.size(), ecgId));
        return waveformVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AiDiagnosisAuditSubmitResultVo submitAudit(AiDiagnosisAuditSubmitDto submitDto, HttpServletRequest request) {
        AiDiagnosisAuditSubmitDto submit = normalizeAuditSubmit(submitDto);

        SystemUserOperatorVo operator = resolveCurrentOperator(request);
        validateAuditPermission(operator);

        AiDiagnosisAuditBaseVo auditBase = aiDiagnosisCenterMapper.selectAuditBase(submit.getDiagnosisId());
        if (auditBase == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "AI 诊断记录不存在");
        }

        Integer status = auditBase.getReportStatus();
        if (status != null && (status == 2 || status == 3 || status == 4)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "该诊断记录已审核，不能重复提交");
        }

        LocalDateTime now = LocalDateTime.now();
        Long reportId = auditBase.getReportId();
        String reportNo = auditBase.getReportNo();
        String operatorName = resolveOperatorName(operator);
        String auditOpinion = "审核通过";

        if (reportId == null) {
            reportId = nextReportId();
            reportNo = buildReportNo(now, reportId);
            aiDiagnosisCenterMapper.insertAuditReport(
                    reportId,
                    reportNo,
                    auditBase.getEcgId(),
                    auditBase.getAiDiagnosisId(),
                    auditBase.getPatientId(),
                    auditBase.getPatientName(),
                    auditBase.getGender(),
                    auditBase.getAge(),
                    auditBase.getHospitalNo(),
                    auditBase.getCollectionTime(),
                    auditBase.getAiConclusion(),
                    submit.getDoctorConclusion(),
                    operator.getUserId(),
                    operatorName,
                    now,
                    auditOpinion,
                    2
            );
        } else {
            aiDiagnosisCenterMapper.updateReportAudit(
                    reportId,
                    submit.getDoctorConclusion(),
                    operator.getUserId(),
                    operatorName,
                    now,
                    auditOpinion,
                    2
            );
        }

        aiDiagnosisCenterMapper.updateCollectionReportStatus(auditBase.getEcgId(), 2, 2);

        AiDiagnosisAuditSubmitResultVo resultVo = new AiDiagnosisAuditSubmitResultVo();
        resultVo.setDiagnosisId(auditBase.getDiagnosisId());
        resultVo.setStatus("已审核");
        resultVo.setAuditedBy(operatorName);
        resultVo.setAuditedTime(now);
        resultVo.setReportDraftId(reportId);
        resultVo.setReportNo(reportNo);
        return resultVo;
    }

    private AiDiagnosisDashboardTimeRangeQueryDto normalizeDashboardRangeQuery(AiDiagnosisDashboardTimeRangeQueryDto queryDto) {
        AiDiagnosisDashboardTimeRangeQueryDto query = queryDto == null ? new AiDiagnosisDashboardTimeRangeQueryDto() : queryDto;
        query.setStartTime(trimToNull(query.getStartTime()));
        query.setEndTime(trimToNull(query.getEndTime()));
        return query;
    }

    private AiDiagnosisDashboardPageQueryDto normalizeDashboardPageQuery(AiDiagnosisDashboardPageQueryDto queryDto) {
        AiDiagnosisDashboardPageQueryDto query = queryDto == null ? new AiDiagnosisDashboardPageQueryDto() : queryDto;
        query.setKeyword(trimToNull(query.getKeyword()));
        query.setStartTime(trimToNull(query.getStartTime()));
        query.setEndTime(trimToNull(query.getEndTime()));
        return query;
    }

    private AiDiagnosisAuditSubmitV2Dto normalizeAuditSubmitV2(AiDiagnosisAuditSubmitV2Dto submitDto) {
        if (submitDto == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "请求参数不能为空");
        }

        String diagnosisId = trimToNull(submitDto.getDiagnosisId());
        String finalAuditResult = trimToNull(submitDto.getFinalAuditResult());
        String doctorConclusion = trimToNull(submitDto.getDoctorConclusion());
        String doctorSuggestion = trimToNull(submitDto.getDoctorSuggestion());
        String auditOpinion = trimToNull(submitDto.getAuditOpinion());

        if (diagnosisId == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "diagnosisId 参数不合法");
        }
        if (finalAuditResult == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "finalAuditResult 参数不合法");
        }
        if (doctorConclusion == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "doctorConclusion 参数不合法");
        }
        if (doctorConclusion.length() > 2000) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "doctorConclusion 长度不能超过 2000");
        }
        if (doctorSuggestion != null && doctorSuggestion.length() > 2000) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "doctorSuggestion 长度不能超过 2000");
        }
        if (auditOpinion != null && auditOpinion.length() > 256) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "auditOpinion 长度不能超过 256");
        }

        submitDto.setDiagnosisId(diagnosisId);
        submitDto.setFinalAuditResult(finalAuditResult);
        submitDto.setDoctorConclusion(doctorConclusion);
        submitDto.setDoctorSuggestion(doctorSuggestion);
        submitDto.setAuditOpinion(auditOpinion);
        submitDto.setAdoptAiFlag(submitDto.getAdoptAiFlag() == null ? 1 : submitDto.getAdoptAiFlag());
        submitDto.setMarkNormalFlag(submitDto.getMarkNormalFlag() == null ? 0 : submitDto.getMarkNormalFlag());

        normalizeBinaryFlag(submitDto.getAdoptAiFlag(), "adoptAiFlag");
        normalizeBinaryFlag(submitDto.getMarkNormalFlag(), "markNormalFlag");
        resolveAuditDecision(finalAuditResult);
        return submitDto;
    }

    private int normalizeBinaryFlag(Integer flag, String fieldName) {
        if (flag == null || (flag != 0 && flag != 1)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), fieldName + " 参数不合法");
        }
        return flag;
    }

    private AuditDecision resolveAuditDecision(String finalAuditResult) {
        String value = trimToNull(finalAuditResult);
        if (value == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "finalAuditResult 参数不合法");
        }

        if ("2".equals(value)
                || AUDIT_RESULT_PASS.equalsIgnoreCase(value)
                || "通过".equals(value)
                || "审核通过".equals(value)
                || "pass".equalsIgnoreCase(value)) {
            return new AuditDecision(2, "审核通过");
        }
        if ("3".equals(value)
                || AUDIT_RESULT_REJECT.equalsIgnoreCase(value)
                || "驳回".equals(value)
                || "审核驳回".equals(value)
                || "reject".equalsIgnoreCase(value)) {
            return new AuditDecision(3, "审核驳回");
        }
        throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "finalAuditResult 参数不合法");
    }

    private int parseTrendDays(String timeRange) {
        String value = trimToNull(timeRange);
        if (value == null) {
            return DEFAULT_TREND_DAYS;
        }
        if (!value.matches("^\\d+$")) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "timeRange 参数不合法");
        }

        int days = Integer.parseInt(value);
        if (days != 7 && days != 15 && days != 30) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "timeRange 仅支持 7、15、30");
        }
        return Math.min(days, MAX_TREND_DAYS);
    }

    private DateRange resolveDateTimeRange(String startTimeText, String endTimeText, int defaultDays) {
        int days = defaultDays <= 0 ? DEFAULT_DASHBOARD_RANGE_DAYS : defaultDays;

        LocalDateTime startTime = parseDateTime(startTimeText, false);
        LocalDateTime endTime = parseDateTime(endTimeText, true);

        if (startTime == null && endTime == null) {
            endTime = LocalDateTime.now().plusSeconds(1);
            startTime = endTime.minusDays(days);
        } else {
            if (endTime == null) {
                endTime = LocalDateTime.now().plusSeconds(1);
            }
            if (startTime == null) {
                startTime = endTime.minusDays(days);
            }
        }

        if (startTime.isAfter(endTime)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "startTime 不能晚于 endTime");
        }
        return new DateRange(startTime, endTime);
    }

    private LocalDateTime parseDateTime(String value, boolean endExclusive) {
        if (!StringUtils.hasText(value)) {
            return null;
        }

        String trimValue = value.trim();
        try {
            LocalDate date = LocalDate.parse(trimValue, DateTimeFormatter.ISO_LOCAL_DATE);
            return endExclusive ? date.plusDays(1).atStartOfDay() : date.atStartOfDay();
        } catch (DateTimeParseException ignored) {
            // Fallback to datetime formats.
        }

        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                return LocalDateTime.parse(trimValue, formatter);
            } catch (DateTimeParseException ignored) {
                // Continue trying available formatters.
            }
        }

        throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), (endExclusive ? "endTime" : "startTime") + " 参数格式不合法");
    }

    private AiDiagnosisCoreMetricsSnapshotVo normalizeCoreMetricsSnapshot(AiDiagnosisCoreMetricsSnapshotVo snapshotVo) {
        AiDiagnosisCoreMetricsSnapshotVo snapshot = snapshotVo == null ? new AiDiagnosisCoreMetricsSnapshotVo() : snapshotVo;
        snapshot.setDiagnosisTotal(defaultLong(snapshot.getDiagnosisTotal()));
        snapshot.setPendingAuditCount(defaultLong(snapshot.getPendingAuditCount()));
        snapshot.setAuditedPassCount(defaultLong(snapshot.getAuditedPassCount()));
        snapshot.setAbnormalCount(defaultLong(snapshot.getAbnormalCount()));
        snapshot.setNormalCount(defaultLong(snapshot.getNormalCount()));
        return snapshot;
    }

    private BigDecimal calculateChangeRate(Long currentValue, Long previousValue) {
        long current = defaultLong(currentValue);
        long previous = defaultLong(previousValue);

        if (previous <= 0) {
            return current > 0 ? new BigDecimal("100.00") : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        return BigDecimal.valueOf(current - previous)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(previous), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateRatio(long numerator, long denominator) {
        if (denominator <= 0 || numerator <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.valueOf(numerator)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(denominator), 2, RoundingMode.HALF_UP);
    }

    private void normalizeLiteDetail(AiDiagnosisLiteDetailVo detailVo) {
        detailVo.setAbnormalCount(defaultInteger(detailVo.getAbnormalCount()));
        detailVo.setAbnormalLevel(defaultInteger(detailVo.getAbnormalLevel()));
        detailVo.setConfidence(defaultDecimal(detailVo.getConfidence()));
        detailVo.setReportStatus(detailVo.getReportStatus() == null ? 1 : detailVo.getReportStatus());
        detailVo.setReportStatusText(defaultString(detailVo.getReportStatusText(), reportStatusDetailText(detailVo.getReportStatus())));
        detailVo.setWarningLevel(detailVo.getWarningLevel() == null ? 0 : detailVo.getWarningLevel());
        detailVo.setWarningLevelText(defaultString(detailVo.getWarningLevelText(), warningLevelText(detailVo.getWarningLevel())));
    }

    private int resolveWarningHandleStatus(Integer reportStatus, Integer markNormalFlag) {
        if (reportStatus != null && reportStatus == 2) {
            return markNormalFlag != null && markNormalFlag == 1 ? 4 : 3;
        }
        return 2;
    }

    private String buildWarningOpinion(Integer reportStatus, Integer markNormalFlag, String auditOpinion) {
        String baseOpinion;
        if (reportStatus != null && reportStatus == 2) {
            baseOpinion = markNormalFlag != null && markNormalFlag == 1 ? "审核判定正常，预警自动忽略" : "诊断审核通过，预警已闭环";
        } else {
            baseOpinion = "诊断审核驳回，预警保持处理中";
        }

        String opinion = trimToNull(auditOpinion);
        if (opinion == null) {
            return baseOpinion;
        }
        return baseOpinion + "；" + opinion;
    }

    private String normalizeAbnormalType(String abnormalType) {
        String value = trimToNull(abnormalType);
        if (value == null) {
            return null;
        }
        if ("-".equals(value) || "正常".equals(value)) {
            return null;
        }
        return value;
    }

    private String reportStatusDetailText(Integer reportStatus) {
        if (reportStatus == null || reportStatus == 1) {
            return "待审核";
        }
        if (reportStatus == 2) {
            return "审核通过";
        }
        if (reportStatus == 3) {
            return "已驳回";
        }
        if (reportStatus == 4) {
            return "已作废";
        }
        return "待审核";
    }

    private String warningLevelText(Integer warningLevel) {
        if (warningLevel == null || warningLevel <= 0) {
            return "正常";
        }
        if (warningLevel == 1) {
            return "低危";
        }
        if (warningLevel == 2) {
            return "中危";
        }
        if (warningLevel == 3) {
            return "高危";
        }
        return "正常";
    }

    private String handleStatusText(Integer handleStatus) {
        if (handleStatus == null || handleStatus == 0) {
            return "待确认";
        }
        if (handleStatus == 1) {
            return "待处理";
        }
        if (handleStatus == 2) {
            return "处理中";
        }
        if (handleStatus == 3) {
            return "已处理";
        }
        if (handleStatus == 4) {
            return "已忽略";
        }
        return "待确认";
    }

    private AiDiagnosisPageQueryDto normalizePageQuery(AiDiagnosisPageQueryDto queryDto) {
        AiDiagnosisPageQueryDto query = queryDto == null ? new AiDiagnosisPageQueryDto() : queryDto;
        query.setEcgNo(trimToNull(query.getEcgNo()));
        query.setPatientName(trimToNull(query.getPatientName()));
        query.setStatus(trimToNull(query.getStatus()));
        return query;
    }

    private AiDiagnosisAuditSubmitDto normalizeAuditSubmit(AiDiagnosisAuditSubmitDto submitDto) {
        if (submitDto == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "请求参数不能为空");
        }

        String diagnosisId = trimToNull(submitDto.getDiagnosisId());
        String doctorConclusion = trimToNull(submitDto.getDoctorConclusion());

        if (diagnosisId == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "diagnosisId 参数不合法");
        }
        if (doctorConclusion == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "doctorConclusion 参数不合法");
        }
        if (doctorConclusion.length() > 2000) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "doctorConclusion 长度不能超过 2000");
        }

        submitDto.setDiagnosisId(diagnosisId);
        submitDto.setDoctorConclusion(doctorConclusion);
        return submitDto;
    }

    private long normalizePageNum(Long pageNum) {
        if (pageNum == null || pageNum < 1) {
            return DEFAULT_PAGE_NUM;
        }
        return pageNum;
    }

    private long normalizePageSize(Long pageSize) {
        if (pageSize == null || pageSize < 1) {
            return DEFAULT_PAGE_SIZE;
        }
        if (pageSize > MAX_PAGE_SIZE) {
            return MAX_PAGE_SIZE;
        }
        return pageSize;
    }

    private Integer parseStatusCode(String status, boolean required) {
        if (!StringUtils.hasText(status)) {
            if (required) {
                throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "status 参数不合法");
            }
            return null;
        }

        String value = status.trim();
        if ("1".equals(value) || "待审核".equals(value) || "pending".equalsIgnoreCase(value)) {
            return 1;
        }
        if ("2".equals(value) || "已审核".equals(value) || "audited".equalsIgnoreCase(value)) {
            return 2;
        }
        throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "status 参数不合法");
    }

    private List<AiDiagnosisAbnormalPointVo> buildAbnormalPointList(String abnormalType, Integer abnormalCount, BigDecimal confidence) {
        int count = abnormalCount == null ? 0 : Math.max(abnormalCount, 0);
        if (count == 0) {
            return Collections.emptyList();
        }

        String[] types = splitAbnormalTypes(abnormalType);
        List<AiDiagnosisAbnormalPointVo> pointList = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            String type = types[i % types.length];
            int pointIndex = 120 + i * 180;
            String level = resolveAbnormalLevel(i, confidence);
            String description = type + " 波段出现形态异常，建议医生重点复核。";
            pointList.add(new AiDiagnosisAbnormalPointVo(pointIndex, type, description, level));
        }
        return pointList;
    }

    private String[] splitAbnormalTypes(String abnormalType) {
        if (!StringUtils.hasText(abnormalType)) {
            return new String[]{"异常节律"};
        }
        String normalized = abnormalType.trim();
        String[] items = normalized.split("[,，;；\\s]+");
        List<String> valid = new ArrayList<>();
        for (String item : items) {
            if (StringUtils.hasText(item)) {
                valid.add(item.trim());
            }
        }
        if (valid.isEmpty()) {
            return new String[]{"异常节律"};
        }
        return valid.toArray(new String[0]);
    }

    private String resolveAbnormalLevel(int index, BigDecimal confidence) {
        BigDecimal value = defaultDecimal(confidence);
        if (index == 0 && value.compareTo(new BigDecimal("90")) < 0) {
            return "高危";
        }
        if (index == 0) {
            return "中危";
        }
        return "低危";
    }

    private List<AiDiagnosisWaveformPointVo> buildWaveform(Long ecgId, Integer samplingRate, Integer leadCount) {
        double offset = ecgId == null ? 0 : (ecgId % 113);
        double samplingDivider = samplingRate == null || samplingRate <= 0 ? 8.0 : Math.max(samplingRate / 30.0, 8.0);
        double leadFactor = leadCount == null || leadCount <= 0 ? 1.0 : Math.min(leadCount / 12.0, 1.4);

        List<AiDiagnosisWaveformPointVo> points = new ArrayList<>(WAVEFORM_POINT_COUNT);
        for (int i = 0; i < WAVEFORM_POINT_COUNT; i++) {
            double angle = (i + offset) / samplingDivider;
            double value = Math.sin(angle) * 0.18 + Math.sin(angle * 0.21) * 0.04;

            int cyclePoint = i % 180;
            if (cyclePoint == 34) {
                value += 0.95;
            } else if (cyclePoint == 35) {
                value -= 0.25;
            } else if (cyclePoint == 72) {
                value += 0.30;
            }

            value = value * leadFactor;
            value = Math.round(value * 100000D) / 100000D;
            points.add(new AiDiagnosisWaveformPointVo(i, value));
        }
        return points;
    }

    private List<AiDiagnosisWaveformSegmentVo> buildWaveSegments(int pointCount) {
        List<AiDiagnosisWaveformSegmentVo> segments = new ArrayList<>();
        for (int start = 0; start < pointCount; start += 180) {
            addSegment(segments, "P", start + 20, start + 33, pointCount);
            addSegment(segments, "QRS", start + 34, start + 46, pointCount);
            addSegment(segments, "T", start + 70, start + 95, pointCount);
        }
        return segments;
    }

    private void addSegment(List<AiDiagnosisWaveformSegmentVo> segments,
                            String type,
                            int start,
                            int end,
                            int pointCount) {
        if (start >= pointCount) {
            return;
        }
        int segmentEnd = Math.min(end, pointCount - 1);
        segments.add(new AiDiagnosisWaveformSegmentVo(type, start, segmentEnd));
    }

    private List<AiDiagnosisWaveformAnnotationVo> buildWaveAnnotations(int pointCount, Long ecgId) {
        int seed = ecgId == null ? 0 : (int) Math.abs(ecgId % 37);
        List<AiDiagnosisWaveformAnnotationVo> annotations = new ArrayList<>();

        addAnnotation(annotations, 180 + seed, "室早疑似", "中危", pointCount);
        addAnnotation(annotations, 540 + seed, "ST段压低", "中危", pointCount);
        addAnnotation(annotations, 900 + seed, "房颤可疑", "高危", pointCount);
        return annotations;
    }

    private void addAnnotation(List<AiDiagnosisWaveformAnnotationVo> annotations,
                               int pointIndex,
                               String label,
                               String level,
                               int pointCount) {
        if (pointIndex >= pointCount) {
            return;
        }
        annotations.add(new AiDiagnosisWaveformAnnotationVo(pointIndex, label, level));
    }

    private String reportStatusToText(Integer reportStatus) {
        if (reportStatus == null || reportStatus == 1) {
            return "待审核";
        }
        if (reportStatus == 2 || reportStatus == 3 || reportStatus == 4) {
            return "已审核";
        }
        return "待审核";
    }

    private DictOptionVo buildOption(String value, String label) {
        return DictOptionVo.builder().value(value).label(label).build();
    }

    private String buildReportNo(LocalDateTime now, Long reportId) {
        long suffix = Math.abs(reportId % 10000);
        return "REP" + now.format(REPORT_NO_TIME_FORMAT) + String.format("%04d", suffix);
    }

    private String resolveOperatorName(SystemUserOperatorVo operator) {
        if (operator == null) {
            return "系统审核医生";
        }
        if (StringUtils.hasText(operator.getRealName())) {
            return operator.getRealName().trim();
        }
        if (StringUtils.hasText(operator.getUserName())) {
            return operator.getUserName().trim();
        }
        return "系统审核医生";
    }

    private Long nextReportId() {
        Long maxReportId = aiDiagnosisCenterMapper.selectMaxReportIdInRangeForUpdate(REPORT_ID_MAX_ALLOWED);
        long base = maxReportId == null ? REPORT_ID_BASE : maxReportId;
        if (base >= REPORT_ID_MAX_ALLOWED) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR.getCode(), "诊断报告ID已达到上限，请联系管理员");
        }
        return base + 1;
    }

    private SystemUserOperatorVo resolveCurrentOperator(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        String authorization = request.getHeader("Authorization");
        if (!StringUtils.hasText(authorization)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        Claims claims;
        try {
            claims = jwtUtil.parseToken(authorization);
        } catch (Exception ex) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        Long userId = extractUserId(claims);
        String subject = claims == null ? null : claims.getSubject();

        SystemUserOperatorVo operator = null;
        if (userId != null && userId > 0) {
            operator = systemUserMapper.selectOperatorById(userId);
        }
        if (operator == null && StringUtils.hasText(subject)) {
            String value = subject.trim();
            if (value.matches("^\\d+$")) {
                operator = systemUserMapper.selectOperatorById(Long.parseLong(value));
            } else {
                operator = systemUserMapper.selectOperatorByUserName(value);
            }
        }

        if (operator == null || operator.getStatus() == null || operator.getStatus() != 1) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return operator;
    }

    private Long extractUserId(Claims claims) {
        if (claims == null) {
            return null;
        }

        Object value = claims.get("userId");
        if (value == null) {
            value = claims.get("uid");
        }
        if (value == null) {
            value = claims.get("id");
        }
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value instanceof String && ((String) value).trim().matches("^\\d+$")) {
            return Long.parseLong(((String) value).trim());
        }
        return null;
    }

    private void validateAuditPermission(SystemUserOperatorVo operator) {
        if (operator.getRoleId() == null) {
            throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "无 AI 诊断审核权限");
        }
        Long count = systemUserMapper.countRolePermission(operator.getRoleId(), AUDIT_PERMISSION_CODES);
        if (count == null || count <= 0) {
            throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "无 AI 诊断审核权限");
        }
    }

    private Long defaultLong(Long value) {
        return value == null ? 0L : value;
    }

    private Integer defaultInteger(Integer value) {
        return value == null ? 0 : value;
    }

    private BigDecimal defaultDecimal(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String defaultString(String value, String defaultValue) {
        return StringUtils.hasText(value) ? value : defaultValue;
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private static final class DateRange {

        private final LocalDateTime startTime;
        private final LocalDateTime endTime;

        private DateRange(LocalDateTime startTime, LocalDateTime endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
        }

        private LocalDateTime getStartTime() {
            return startTime;
        }

        private LocalDateTime getEndTime() {
            return endTime;
        }
    }

    private static final class AuditDecision {

        private final Integer reportStatus;
        private final String resultText;

        private AuditDecision(Integer reportStatus, String resultText) {
            this.reportStatus = reportStatus;
            this.resultText = resultText;
        }

        private Integer getReportStatus() {
            return reportStatus;
        }

        private String getResultText() {
            return resultText;
        }
    }
}
