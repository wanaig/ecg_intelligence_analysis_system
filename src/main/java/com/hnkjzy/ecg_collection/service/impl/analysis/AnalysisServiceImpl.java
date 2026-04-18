package com.hnkjzy.ecg_collection.service.impl.analysis;

import com.hnkjzy.ecg_collection.common.exception.BusinessException;
import com.hnkjzy.ecg_collection.common.result.ResultCode;
import com.hnkjzy.ecg_collection.mapper.analysis.AnalysisMapper;
import com.hnkjzy.ecg_collection.model.dto.analysis.AnalysisDashboardQueryDto;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisCoreMetricsVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisDeviceUsageStatVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisDictVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisReportDeviceStatVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisReportStatusStatVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisWardMeasureStatVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisWarningDimensionStatVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisWarningLevelStatVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisWarningTypeStatVo;
import com.hnkjzy.ecg_collection.model.vo.common.DictOptionVo;
import com.hnkjzy.ecg_collection.service.analysis.AnalysisService;
import com.hnkjzy.ecg_collection.service.impl.BaseServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
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

    private final AnalysisMapper analysisMapper;

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

    private Long defaultLong(Long value) {
        return value == null ? 0L : value;
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
