package com.hnkjzy.ecg_collection.service.impl.analysis;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnkjzy.ecg_collection.common.exception.BusinessException;
import com.hnkjzy.ecg_collection.common.result.ResultCode;
import com.hnkjzy.ecg_collection.mapper.analysis.AnalysisMapper;
import com.hnkjzy.ecg_collection.mapper.system.SystemUserMapper;
import com.hnkjzy.ecg_collection.model.dto.analysis.AnalysisDashboardPageQueryDto;
import com.hnkjzy.ecg_collection.model.dto.analysis.AnalysisDashboardQueryDto;
import com.hnkjzy.ecg_collection.model.dto.analysis.AnalysisTimeRangeQueryDto;
import com.hnkjzy.ecg_collection.model.dto.analysis.AnalysisWarningIncludeDto;
import com.hnkjzy.ecg_collection.model.dto.analysis.AnalysisWarningFullPageQueryDto;
import com.hnkjzy.ecg_collection.model.entity.system.SysOperationLogEntity;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisCoreMetricsVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisDashboardCoreMetricsVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisDashboardPageResultVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisDeviceUsageStatVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisDictVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisLatestEcgPageItemVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisPendingWarningPageItemVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisReportDeviceStatVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisReportStatusStatVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisWardMeasureStatVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisWardWarningTopItemVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisWarningDailyCountVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisWarningDimensionStatVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisWarningLevelDistributionVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisWarningLevelStatVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisWarningDetailVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisWarningFullPageInitVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisWarningFullPageItemVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisWarningIncludeResultVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisWarningIncludeSourceVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisWarningTrendVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisWarningTypeRatioItemVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisWarningTypeWardTopVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisWarningTypeStatVo;
import com.hnkjzy.ecg_collection.model.vo.common.DictOptionVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemUserOperatorVo;
import com.hnkjzy.ecg_collection.service.analysis.AnalysisService;
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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据分析模块服务实现。
 */
@Service
@RequiredArgsConstructor
public class AnalysisServiceImpl extends BaseServiceImpl implements AnalysisService {

    private static final long DEFAULT_PAGE_NUM = 1L;
    private static final long DEFAULT_PAGE_SIZE = 10L;
    private static final long MAX_PAGE_SIZE = 200L;
    private static final long MONITOR_ID_BASE = 2600L;
    private static final long RESEARCH_ID_BASE = 3000L;
    private static final long OP_LOG_ID_BASE = 1400L;
    private static final long ID_MAX_ALLOWED = 9_999_999_999L;
    private static final String MODULE_WARNING_INCLUDE = "数据分析预警";
    private static final String OPERATION_WARNING_INCLUDE = "纳入";

    private static final List<DateTimeFormatter> DATE_TIME_FORMATTERS = List.of(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
            DateTimeFormatter.ISO_LOCAL_DATE_TIME
    );

    private final AnalysisMapper analysisMapper;
    private final SystemUserMapper systemUserMapper;
    private final JwtUtil jwtUtil;

    @Override
    public AnalysisDashboardCoreMetricsVo getDashboardCoreMetrics(AnalysisTimeRangeQueryDto queryDto) {
        AnalysisTimeRangeQueryDto query = normalizeTimeRangeQuery(queryDto);
        DateRange dateRange = resolveDateTimeRange(query.getStartTime(), query.getEndTime());

        AnalysisDashboardCoreMetricsVo metricsVo = analysisMapper.selectDashboardCoreMetrics(
                dateRange.getStartTime(),
                dateRange.getEndTime()
        );
        if (metricsVo == null) {
            metricsVo = new AnalysisDashboardCoreMetricsVo();
        }

        metricsVo.setEcgTotal(defaultLong(metricsVo.getEcgTotal()));
        metricsVo.setPendingAnalyse(defaultLong(metricsVo.getPendingAnalyse()));
        metricsVo.setPendingAudit(defaultLong(metricsVo.getPendingAudit()));
        metricsVo.setAbnormalWarning(defaultLong(metricsVo.getAbnormalWarning()));
        metricsVo.setWarningTotal(defaultLong(metricsVo.getWarningTotal()));
        metricsVo.setReportTotal(defaultLong(metricsVo.getReportTotal()));
        metricsVo.setAiAccuracy(defaultDecimal(metricsVo.getAiAccuracy()));
        return metricsVo;
    }

    @Override
    public AnalysisWarningLevelDistributionVo getWarningLevelDistribution(AnalysisTimeRangeQueryDto queryDto) {
        AnalysisTimeRangeQueryDto query = normalizeTimeRangeQuery(queryDto);
        DateRange dateRange = resolveDateTimeRange(query.getStartTime(), query.getEndTime());

        AnalysisWarningLevelDistributionVo vo = analysisMapper.selectWarningLevelDistribution(
                dateRange.getStartTime(),
                dateRange.getEndTime()
        );
        if (vo == null) {
            vo = new AnalysisWarningLevelDistributionVo();
        }

        vo.setLowRiskCount(defaultLong(vo.getLowRiskCount()));
        vo.setMiddleRiskCount(defaultLong(vo.getMiddleRiskCount()));
        vo.setHighRiskCount(defaultLong(vo.getHighRiskCount()));
        return vo;
    }

    @Override
    public AnalysisWarningTypeWardTopVo getWarningTypeWardTop(AnalysisTimeRangeQueryDto queryDto) {
        AnalysisTimeRangeQueryDto query = normalizeTimeRangeQuery(queryDto);
        DateRange dateRange = resolveDateTimeRange(query.getStartTime(), query.getEndTime());

        AnalysisWarningTypeWardTopVo resultVo = new AnalysisWarningTypeWardTopVo();

        List<AnalysisWarningTypeRatioItemVo> warningTypeStats = analysisMapper.selectWarningTypeCounts(
                dateRange.getStartTime(),
                dateRange.getEndTime()
        );
        if (warningTypeStats == null) {
            warningTypeStats = new ArrayList<>();
        }

        long warningTotal = 0L;
        for (AnalysisWarningTypeRatioItemVo item : warningTypeStats) {
            Long count = defaultLong(item.getCount());
            item.setCount(count);
            warningTotal += count;
        }
        for (AnalysisWarningTypeRatioItemVo item : warningTypeStats) {
            BigDecimal ratio = BigDecimal.ZERO;
            if (warningTotal > 0) {
                ratio = BigDecimal.valueOf(item.getCount())
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(warningTotal), 2, RoundingMode.HALF_UP);
            }
            item.setRatio(ratio);
        }

        List<AnalysisWardWarningTopItemVo> wardTopStats = analysisMapper.selectWardWarningTop(
                dateRange.getStartTime(),
                dateRange.getEndTime()
        );
        if (wardTopStats == null) {
            wardTopStats = Collections.emptyList();
        }
        for (AnalysisWardWarningTopItemVo item : wardTopStats) {
            item.setWarningCount(defaultLong(item.getWarningCount()));
        }

        resultVo.setWarningTypeStats(warningTypeStats);
        resultVo.setWardTopStats(wardTopStats);
        return resultVo;
    }

    @Override
    public AnalysisWarningTrendVo getWarningTrend7d() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);

        List<AnalysisWarningDailyCountVo> dailyCounts = analysisMapper.selectWarningDailyCounts(
                startDate.atStartOfDay(),
                endDate.plusDays(1).atStartOfDay()
        );

        Map<String, Long> countMap = new HashMap<>();
        if (dailyCounts != null) {
            for (AnalysisWarningDailyCountVo item : dailyCounts) {
                if (item != null && StringUtils.hasText(item.getWarningDate())) {
                    countMap.put(item.getWarningDate(), defaultLong(item.getWarningCount()));
                }
            }
        }

        List<String> dateList = new ArrayList<>(7);
        List<Long> warningCountList = new ArrayList<>(7);
        for (int i = 0; i < 7; i++) {
            String dateText = startDate.plusDays(i).format(DateTimeFormatter.ISO_LOCAL_DATE);
            dateList.add(dateText);
            warningCountList.add(countMap.getOrDefault(dateText, 0L));
        }

        AnalysisWarningTrendVo trendVo = new AnalysisWarningTrendVo();
        trendVo.setDateList(dateList);
        trendVo.setWarningCountList(warningCountList);
        return trendVo;
    }

    @Override
    public AnalysisDashboardPageResultVo<AnalysisPendingWarningPageItemVo> pagePendingWarnings(
            AnalysisDashboardPageQueryDto queryDto) {
        AnalysisDashboardPageQueryDto query = normalizeDashboardPageQuery(queryDto);
        DateRange dateRange = resolveDateTimeRange(query.getStartTime(), query.getEndTime());

        long pageNum = normalizePageNum(query.getPageNum());
        long pageSize = normalizePageSize(query.getPageSize());
        Page<AnalysisPendingWarningPageItemVo> page = new Page<>(pageNum, pageSize);
        IPage<AnalysisPendingWarningPageItemVo> pageData = analysisMapper.selectPendingWarningPage(
                page,
                dateRange.getStartTime(),
                dateRange.getEndTime()
        );

        List<AnalysisPendingWarningPageItemVo> records = pageData.getRecords();
        if (records == null) {
            records = Collections.emptyList();
        }

        return AnalysisDashboardPageResultVo.<AnalysisPendingWarningPageItemVo>builder()
                .total(pageData.getTotal())
                .pages(pageData.getPages())
                .list(records)
                .build();
    }

    @Override
    public AnalysisDashboardPageResultVo<AnalysisLatestEcgPageItemVo> pageLatestEcgRecords(
            AnalysisDashboardPageQueryDto queryDto) {
        AnalysisDashboardPageQueryDto query = normalizeDashboardPageQuery(queryDto);
        DateRange dateRange = resolveDateTimeRange(query.getStartTime(), query.getEndTime());

        long pageNum = normalizePageNum(query.getPageNum());
        long pageSize = normalizePageSize(query.getPageSize());
        Page<AnalysisLatestEcgPageItemVo> page = new Page<>(pageNum, pageSize);
        IPage<AnalysisLatestEcgPageItemVo> pageData = analysisMapper.selectLatestEcgPage(
                page,
                dateRange.getStartTime(),
                dateRange.getEndTime()
        );

        List<AnalysisLatestEcgPageItemVo> records = pageData.getRecords();
        if (records == null) {
            records = Collections.emptyList();
        }
        for (AnalysisLatestEcgPageItemVo item : records) {
            if (!StringUtils.hasText(item.getAiConclusionShort()) && StringUtils.hasText(item.getAiConclusion())) {
                item.setAiConclusionShort(item.getAiConclusion());
            }
        }

        return AnalysisDashboardPageResultVo.<AnalysisLatestEcgPageItemVo>builder()
                .total(pageData.getTotal())
                .pages(pageData.getPages())
                .list(records)
                .build();
    }

    @Override
    public AnalysisWarningDetailVo getDashboardWarningDetail(Long alertId) {
        if (alertId == null || alertId <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "alertId 参数不合法");
        }

        AnalysisWarningDetailVo detailVo = analysisMapper.selectDashboardWarningDetail(alertId);
        if (detailVo == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "预警不存在");
        }

        detailVo.setConfidence(defaultDecimal(detailVo.getConfidence()));
        detailVo.setAbnormalCount(detailVo.getAbnormalCount() == null ? 0 : detailVo.getAbnormalCount());
        detailVo.setAbnormalLevel(detailVo.getAbnormalLevel() == null ? 0 : detailVo.getAbnormalLevel());
        detailVo.setAbnormalLevelText(defaultString(detailVo.getAbnormalLevelText(), abnormalLevelText(detailVo.getAbnormalLevel())));
        detailVo.setAnalysisStatus(detailVo.getAnalysisStatus() == null ? 0 : detailVo.getAnalysisStatus());
        detailVo.setAnalysisStatusText(defaultString(detailVo.getAnalysisStatusText(), analysisStatusText(detailVo.getAnalysisStatus())));
        return detailVo;
    }

    @Override
    public AnalysisWarningFullPageInitVo getFullWarningPageInitData(AnalysisWarningFullPageQueryDto queryDto) {
        AnalysisWarningFullPageQueryDto query = normalizeFullWarningPageQuery(queryDto);
        Integer alertLevelCode = parseAlertLevel(query.getAlertLevel());
        Integer alertStatusCode = parseAlertStatus(query.getAlertStatus());
        DateRange dateRange = resolveDateTimeRange(query.getStartTime(), query.getEndTime());

        long pageNum = normalizePageNum(query.getPageNum());
        long pageSize = normalizePageSize(query.getPageSize());

        Page<AnalysisWarningFullPageItemVo> page = new Page<>(pageNum, pageSize);
        IPage<AnalysisWarningFullPageItemVo> pageData = analysisMapper.selectDashboardFullWarningPage(
                page,
                query,
                alertLevelCode,
                alertStatusCode,
                dateRange.getStartTime(),
                dateRange.getEndTime()
        );

        List<AnalysisWarningFullPageItemVo> records = pageData.getRecords();
        if (records == null) {
            records = Collections.emptyList();
        }

        AnalysisWarningFullPageInitVo resultVo = new AnalysisWarningFullPageInitVo();
        resultVo.setHighRiskCount(defaultLong(analysisMapper.countDashboardHighRiskWarnings(
                dateRange.getStartTime(),
                dateRange.getEndTime()
        )));
        resultVo.setPendingHandleCount(defaultLong(analysisMapper.countDashboardPendingHandleWarnings(
                dateRange.getStartTime(),
                dateRange.getEndTime()
        )));

        List<DictOptionVo> wardOptions = analysisMapper.selectWardOptions();
        if (wardOptions == null) {
            wardOptions = new ArrayList<>();
        }
        wardOptions.add(0, DictOptionVo.builder().value("").label("全部病区").build());
        resultVo.setWardOptions(wardOptions);

        resultVo.setAlertLevelOptions(buildAlertLevelOptions());
        resultVo.setAlertStatusOptions(buildAlertStatusOptions());

        resultVo.setPageData(AnalysisDashboardPageResultVo.<AnalysisWarningFullPageItemVo>builder()
                .total(pageData.getTotal())
                .pages(pageData.getPages())
                .list(records)
                .build());
        return resultVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AnalysisWarningIncludeResultVo includeWarning(AnalysisWarningIncludeDto includeDto, HttpServletRequest request) {
        if (includeDto == null || includeDto.getWarningId() == null || includeDto.getWarningId() <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "warningId 参数不合法");
        }

        AnalysisWarningIncludeSourceVo source = analysisMapper.selectWarningIncludeSource(includeDto.getWarningId());
        if (source == null || source.getPatientId() == null || source.getPatientId() <= 0) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "预警不存在");
        }

        if (!StringUtils.hasText(source.getPatientName())) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR.getCode(), "预警关联患者信息不完整");
        }

        SystemUserOperatorVo operator = resolveCurrentOperator(request);
        String operatorName = resolveOperatorName(operator);
        LocalDateTime now = LocalDateTime.now();

        Long monitorId = analysisMapper.selectMonitorIdByPatientId(source.getPatientId());
        if (monitorId == null) {
            monitorId = nextMonitorId();
            int inserted = analysisMapper.insertMonitorIncludeRecord(monitorId, source, now);
            if (inserted <= 0) {
                throw new BusinessException(ResultCode.BUSINESS_ERROR.getCode(), "重点监护纳入失败");
            }
        } else {
            analysisMapper.updateMonitorIncludeState(source.getPatientId(), defaultWarningLevel(source.getWarningLevel()));
        }

        Long researchId = null;
        if (source.getRecordId() != null && source.getRecordId() > 0) {
            researchId = analysisMapper.selectResearchIdByPatientRecord(source.getPatientId(), source.getRecordId());
        }
        if (researchId == null) {
            researchId = nextResearchId();
            int inserted = analysisMapper.insertResearchIncludeRecord(researchId, source, operatorName, now);
            if (inserted <= 0) {
                throw new BusinessException(ResultCode.BUSINESS_ERROR.getCode(), "科研纳入失败");
            }
        }

        insertIncludeOperationLog(source.getWarningId(), source.getPatientId(), operator, operatorName, request, now);

        AnalysisWarningIncludeResultVo resultVo = new AnalysisWarningIncludeResultVo();
        resultVo.setWarningId(source.getWarningId());
        resultVo.setPatientId(source.getPatientId());
        resultVo.setKeyMonitorIncluded(true);
        resultVo.setResearchIncluded(true);
        resultVo.setMonitorId(monitorId);
        resultVo.setResearchId(researchId);
        resultVo.setIncludedBy(operatorName);
        resultVo.setIncludedTime(now);
        return resultVo;
    }

    @Override
    public AnalysisCoreMetricsVo getCoreMetrics(AnalysisDashboardQueryDto queryDto) {
        AnalysisDashboardQueryDto query = normalizeQuery(queryDto);
        if (query.getWardId() != null && query.getWardId() <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "wardId 参数不合法");
        }

        String timeType = parseTimeType(query.getTimeType());
        DateRange dateRange = resolveDateRange(timeType, query.getStartDate(), query.getEndDate());

        AnalysisCoreMetricsVo metricsVo = analysisMapper.selectCoreMetrics(dateRange.getStartTime(), dateRange.getEndTime(), query.getWardId());
        if (metricsVo == null) {
            metricsVo = new AnalysisCoreMetricsVo();
        }

        metricsVo.setTotalMeasureCount(defaultLong(metricsVo.getTotalMeasureCount()));
        metricsVo.setNormalMeasureCount(defaultLong(metricsVo.getNormalMeasureCount()));
        metricsVo.setAbnormalMeasureCount(defaultLong(metricsVo.getAbnormalMeasureCount()));

        metricsVo.setWarningTotalCount(defaultLong(metricsVo.getWarningTotalCount()));
        metricsVo.setWarningHandledCount(defaultLong(metricsVo.getWarningHandledCount()));
        metricsVo.setAvgWarningHandleMinutes(defaultDecimal(metricsVo.getAvgWarningHandleMinutes()));

        metricsVo.setReportTotalCount(defaultLong(metricsVo.getReportTotalCount()));
        metricsVo.setReportAuditedCount(defaultLong(metricsVo.getReportAuditedCount()));
        metricsVo.setAvgReportAuditMinutes(defaultDecimal(metricsVo.getAvgReportAuditMinutes()));

        metricsVo.setAiAccuracyRate(defaultDecimal(metricsVo.getAiAccuracyRate()));
        metricsVo.setAiSampleTotal(defaultLong(metricsVo.getAiSampleTotal()));
        return metricsVo;
    }

    @Override
    public List<AnalysisWardMeasureStatVo> getWardMeasureStats() {
        List<AnalysisWardMeasureStatVo> stats = analysisMapper.selectWardMeasureStats();
        if (stats == null) {
            return Collections.emptyList();
        }

        for (AnalysisWardMeasureStatVo stat : stats) {
            stat.setNormalCount(defaultLong(stat.getNormalCount()));
            stat.setAbnormalCount(defaultLong(stat.getAbnormalCount()));
        }
        return stats;
    }

    @Override
    public AnalysisWarningDimensionStatVo getWarningDimensionStats() {
        AnalysisWarningDimensionStatVo result = new AnalysisWarningDimensionStatVo();

        List<AnalysisWarningLevelStatVo> levelStats = analysisMapper.selectWarningLevelStats();
        List<AnalysisWarningTypeStatVo> typeStats = analysisMapper.selectWarningTypeStats();

        Map<String, Long> levelCountMap = new HashMap<>();
        if (levelStats != null) {
            for (AnalysisWarningLevelStatVo item : levelStats) {
                if (item != null && StringUtils.hasText(item.getLevel())) {
                    levelCountMap.put(item.getLevel(), defaultLong(item.getCount()));
                }
            }
        }

        List<AnalysisWarningLevelStatVo> orderedLevelStats = new ArrayList<>();
        orderedLevelStats.add(buildLevelStat("高危", levelCountMap.getOrDefault("高危", 0L)));
        orderedLevelStats.add(buildLevelStat("中危", levelCountMap.getOrDefault("中危", 0L)));
        orderedLevelStats.add(buildLevelStat("低危", levelCountMap.getOrDefault("低危", 0L)));

        if (typeStats == null) {
            typeStats = Collections.emptyList();
        }
        for (AnalysisWarningTypeStatVo item : typeStats) {
            item.setCount(defaultLong(item.getCount()));
        }

        result.setLevelStats(orderedLevelStats);
        result.setTypeStats(typeStats);
        return result;
    }

    @Override
    public AnalysisReportDeviceStatVo getReportDeviceStats() {
        AnalysisReportDeviceStatVo result = new AnalysisReportDeviceStatVo();

        List<AnalysisReportStatusStatVo> reportStatusStats = analysisMapper.selectReportStatusStats();
        List<AnalysisDeviceUsageStatVo> deviceUsageStats = analysisMapper.selectDeviceUsageStats();

        if (reportStatusStats == null) {
            reportStatusStats = Collections.emptyList();
        }
        if (deviceUsageStats == null) {
            deviceUsageStats = Collections.emptyList();
        }

        for (AnalysisReportStatusStatVo item : reportStatusStats) {
            item.setCount(defaultLong(item.getCount()));
        }
        for (AnalysisDeviceUsageStatVo item : deviceUsageStats) {
            item.setCount(defaultLong(item.getCount()));
        }

        result.setReportStatusStats(reportStatusStats);
        result.setDeviceUsageStats(deviceUsageStats);
        return result;
    }

    @Override
    public AnalysisDictVo getAnalysisDicts() {
        AnalysisDictVo dictVo = new AnalysisDictVo();

        List<DictOptionVo> wardOptions = analysisMapper.selectWardOptions();
        if (wardOptions == null) {
            wardOptions = new ArrayList<>();
        }
        wardOptions.add(0, DictOptionVo.builder().value("").label("全部病区").build());

        List<DictOptionVo> timeTypeOptions = new ArrayList<>();
        timeTypeOptions.add(DictOptionVo.builder().value("").label("全部周期").build());
        timeTypeOptions.add(DictOptionVo.builder().value("DAY").label("日").build());
        timeTypeOptions.add(DictOptionVo.builder().value("WEEK").label("周").build());
        timeTypeOptions.add(DictOptionVo.builder().value("MONTH").label("月").build());

        dictVo.setWardOptions(wardOptions);
        dictVo.setTimeTypeOptions(timeTypeOptions);
        return dictVo;
    }

    private AnalysisTimeRangeQueryDto normalizeTimeRangeQuery(AnalysisTimeRangeQueryDto queryDto) {
        AnalysisTimeRangeQueryDto query = queryDto == null ? new AnalysisTimeRangeQueryDto() : queryDto;
        query.setStartTime(trimToNull(query.getStartTime()));
        query.setEndTime(trimToNull(query.getEndTime()));
        return query;
    }

    private AnalysisDashboardPageQueryDto normalizeDashboardPageQuery(AnalysisDashboardPageQueryDto queryDto) {
        AnalysisDashboardPageQueryDto query = queryDto == null ? new AnalysisDashboardPageQueryDto() : queryDto;
        query.setStartTime(trimToNull(query.getStartTime()));
        query.setEndTime(trimToNull(query.getEndTime()));
        return query;
    }

    private AnalysisWarningFullPageQueryDto normalizeFullWarningPageQuery(AnalysisWarningFullPageQueryDto queryDto) {
        AnalysisWarningFullPageQueryDto query = queryDto == null ? new AnalysisWarningFullPageQueryDto() : queryDto;
        query.setKeyword(trimToNull(query.getKeyword()));
        query.setWard(trimToNull(query.getWard()));
        query.setAlertLevel(trimToNull(query.getAlertLevel()));
        query.setAlertStatus(trimToNull(query.getAlertStatus()));
        query.setStartTime(trimToNull(query.getStartTime()));
        query.setEndTime(trimToNull(query.getEndTime()));
        return query;
    }

    private Integer parseAlertLevel(String alertLevel) {
        if (!StringUtils.hasText(alertLevel) || "全部级别".equals(alertLevel)) {
            return null;
        }

        switch (alertLevel.trim()) {
            case "1":
            case "低危":
                return 1;
            case "2":
            case "中危":
                return 2;
            case "3":
            case "高危":
                return 3;
            default:
                throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "alertLevel 参数不合法");
        }
    }

    private Integer parseAlertStatus(String alertStatus) {
        if (!StringUtils.hasText(alertStatus) || "全部状态".equals(alertStatus)) {
            return null;
        }

        switch (alertStatus.trim()) {
            case "0":
            case "待确认":
                return 0;
            case "1":
            case "待处理":
                return 1;
            case "2":
            case "处理中":
                return 2;
            case "3":
            case "已处理":
                return 3;
            case "4":
            case "已忽略":
                return 4;
            default:
                throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "alertStatus 参数不合法");
        }
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

    private DateRange resolveDateTimeRange(String startTimeText, String endTimeText) {
        LocalDateTime startTime = parseDateTime(startTimeText, false);
        LocalDateTime endTime = parseDateTime(endTimeText, true);

        if (startTime == null && endTime == null) {
            return new DateRange(null, null);
        }
        if (startTime == null) {
            startTime = endTime.minusDays(1);
        }
        if (endTime == null) {
            endTime = LocalDateTime.now().plusSeconds(1);
        }
        if (startTime.isAfter(endTime)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "startTime 不能晚于 endTime");
        }
        return new DateRange(startTime, endTime);
    }

    private AnalysisDashboardQueryDto normalizeQuery(AnalysisDashboardQueryDto queryDto) {
        AnalysisDashboardQueryDto query = queryDto == null ? new AnalysisDashboardQueryDto() : queryDto;
        query.setTimeType(trimToNull(query.getTimeType()));
        query.setStartDate(trimToNull(query.getStartDate()));
        query.setEndDate(trimToNull(query.getEndDate()));
        return query;
    }

    private String parseTimeType(String timeType) {
        if (!StringUtils.hasText(timeType)) {
            return null;
        }

        String value = timeType.trim().toUpperCase();
        switch (value) {
            case "DAY":
            case "D":
            case "1":
            case "日":
                return "DAY";
            case "WEEK":
            case "W":
            case "2":
            case "周":
                return "WEEK";
            case "MONTH":
            case "M":
            case "3":
            case "月":
                return "MONTH";
            default:
                throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "timeType 参数不合法");
        }
    }

    private DateRange resolveDateRange(String timeType, String startDateText, String endDateText) {
        LocalDate startDate = parseDate(startDateText, "startDate");
        LocalDate endDate = parseDate(endDateText, "endDate");

        if (startDate != null || endDate != null) {
            if (startDate == null) {
                startDate = endDate;
            }
            if (endDate == null) {
                endDate = startDate;
            }
        } else if (timeType != null) {
            LocalDate today = LocalDate.now();
            switch (timeType) {
                case "DAY":
                    startDate = today;
                    endDate = today;
                    break;
                case "WEEK":
                    startDate = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                    endDate = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
                    break;
                case "MONTH":
                    startDate = today.withDayOfMonth(1);
                    endDate = today.with(TemporalAdjusters.lastDayOfMonth());
                    break;
                default:
                    throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "timeType 参数不合法");
            }
        } else {
            return new DateRange(null, null);
        }

        if (startDate.isAfter(endDate)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "startDate 不能晚于 endDate");
        }

        return new DateRange(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());
    }

    private LocalDate parseDate(String dateText, String fieldName) {
        if (!StringUtils.hasText(dateText)) {
            return null;
        }

        try {
            return LocalDate.parse(dateText.trim(), DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException ex) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), fieldName + " 参数格式错误");
        }
    }

    private LocalDateTime parseDateTime(String value, boolean endExclusive) {
        if (!StringUtils.hasText(value)) {
            return null;
        }

        String trimValue = value.trim();
        if (trimValue.length() == 10) {
            try {
                LocalDate date = LocalDate.parse(trimValue, DateTimeFormatter.ISO_LOCAL_DATE);
                return endExclusive ? date.plusDays(1).atStartOfDay() : date.atStartOfDay();
            } catch (DateTimeParseException ex) {
                throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "time 参数格式错误");
            }
        }

        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                LocalDateTime dateTime = LocalDateTime.parse(trimValue, formatter);
                return endExclusive ? dateTime.plusSeconds(1) : dateTime;
            } catch (DateTimeParseException ignored) {
                // try next formatter
            }
        }
        throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "time 参数格式错误");
    }

    private AnalysisWarningLevelStatVo buildLevelStat(String level, Long count) {
        AnalysisWarningLevelStatVo item = new AnalysisWarningLevelStatVo();
        item.setLevel(level);
        item.setCount(defaultLong(count));
        return item;
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private Integer defaultWarningLevel(Integer warningLevel) {
        if (warningLevel == null || warningLevel < 0) {
            return 0;
        }
        if (warningLevel > 3) {
            return 3;
        }
        return warningLevel;
    }

    private Long nextMonitorId() {
        Long maxId = analysisMapper.selectMaxMonitorIdInRangeForUpdate(ID_MAX_ALLOWED);
        long base = maxId == null ? MONITOR_ID_BASE : maxId;
        if (base >= ID_MAX_ALLOWED) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR.getCode(), "监护记录ID已达到上限，请联系管理员");
        }
        return base + 1;
    }

    private Long nextResearchId() {
        Long maxId = analysisMapper.selectMaxResearchIdInRangeForUpdate(ID_MAX_ALLOWED);
        long base = maxId == null ? RESEARCH_ID_BASE : maxId;
        if (base >= ID_MAX_ALLOWED) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR.getCode(), "科研记录ID已达到上限，请联系管理员");
        }
        return base + 1;
    }

    private Long nextOperationLogId() {
        Long maxId = systemUserMapper.selectMaxOperationLogIdInRangeForUpdate(ID_MAX_ALLOWED);
        long base = maxId == null ? OP_LOG_ID_BASE : maxId;
        if (base >= ID_MAX_ALLOWED) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR.getCode(), "操作日志ID已达到上限，请联系管理员");
        }
        return base + 1;
    }

    private void insertIncludeOperationLog(Long warningId,
                                           Long patientId,
                                           SystemUserOperatorVo operator,
                                           String operatorName,
                                           HttpServletRequest request,
                                           LocalDateTime operationTime) {
        SysOperationLogEntity logEntity = new SysOperationLogEntity();
        logEntity.setLogId(nextOperationLogId());
        logEntity.setUserId(operator.getUserId());
        logEntity.setRealName(operatorName);
        logEntity.setModule(MODULE_WARNING_INCLUDE);
        logEntity.setOperationType(OPERATION_WARNING_INCLUDE);
        logEntity.setOperationContent("预警纳入重点管理 warningId=" + warningId + ", patientId=" + patientId);
        logEntity.setRequestIp(resolveRequestIp(request));
        logEntity.setOperationTime(operationTime);
        logEntity.setIsDeleted(0);
        systemUserMapper.insertOperationLog(logEntity);
    }

    private String resolveOperatorName(SystemUserOperatorVo operator) {
        if (operator == null) {
            return "系统";
        }
        if (StringUtils.hasText(operator.getRealName())) {
            return operator.getRealName().trim();
        }
        if (StringUtils.hasText(operator.getUserName())) {
            return operator.getUserName().trim();
        }
        return "系统";
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
            String value = subject == null ? "" : subject.trim();
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

    private String resolveRequestIp(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String ip = firstNonUnknown(request.getHeader("X-Forwarded-For"));
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        if (!StringUtils.hasText(ip)) {
            ip = firstNonUnknown(request.getHeader("X-Real-IP"));
        }
        if (!StringUtils.hasText(ip)) {
            ip = firstNonUnknown(request.getHeader("Proxy-Client-IP"));
        }
        if (!StringUtils.hasText(ip)) {
            ip = firstNonUnknown(request.getHeader("WL-Proxy-Client-IP"));
        }
        if (!StringUtils.hasText(ip)) {
            ip = request.getRemoteAddr();
        }
        return trimToNull(ip);
    }

    private String firstNonUnknown(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        String trimValue = value.trim();
        if ("unknown".equalsIgnoreCase(trimValue)) {
            return null;
        }
        return trimValue;
    }

    private Long defaultLong(Long value) {
        return value == null ? 0L : value;
    }

    private String defaultString(String value, String defaultValue) {
        return StringUtils.hasText(value) ? value : defaultValue;
    }

    private List<DictOptionVo> buildAlertLevelOptions() {
        List<DictOptionVo> options = new ArrayList<>();
        options.add(DictOptionVo.builder().value("").label("全部级别").build());
        options.add(DictOptionVo.builder().value("1").label("低危").build());
        options.add(DictOptionVo.builder().value("2").label("中危").build());
        options.add(DictOptionVo.builder().value("3").label("高危").build());
        return options;
    }

    private List<DictOptionVo> buildAlertStatusOptions() {
        List<DictOptionVo> options = new ArrayList<>();
        options.add(DictOptionVo.builder().value("").label("全部状态").build());
        options.add(DictOptionVo.builder().value("0").label("待确认").build());
        options.add(DictOptionVo.builder().value("1").label("待处理").build());
        options.add(DictOptionVo.builder().value("2").label("处理中").build());
        options.add(DictOptionVo.builder().value("3").label("已处理").build());
        options.add(DictOptionVo.builder().value("4").label("已忽略").build());
        return options;
    }

    private String abnormalLevelText(Integer abnormalLevel) {
        if (abnormalLevel == null || abnormalLevel <= 0) {
            return "正常";
        }
        if (abnormalLevel == 1) {
            return "低危";
        }
        if (abnormalLevel == 2) {
            return "中危";
        }
        if (abnormalLevel == 3) {
            return "高危";
        }
        return "未知";
    }

    private String analysisStatusText(Integer analysisStatus) {
        if (analysisStatus == null || analysisStatus == 0) {
            return "待分析";
        }
        if (analysisStatus == 1) {
            return "分析中";
        }
        if (analysisStatus == 2) {
            return "已完成";
        }
        if (analysisStatus == 3) {
            return "失败";
        }
        return "未知";
    }

    private BigDecimal defaultDecimal(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private static final class DateRange {
        private final LocalDateTime startTime;
        private final LocalDateTime endTime;

        private DateRange(LocalDateTime startTime, LocalDateTime endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
        }

        public LocalDateTime getStartTime() {
            return startTime;
        }

        public LocalDateTime getEndTime() {
            return endTime;
        }
    }
}
