package com.hnkjzy.ecg_collection.service.impl.workbench;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnkjzy.ecg_collection.common.exception.BusinessException;
import com.hnkjzy.ecg_collection.common.result.ResultCode;
import com.hnkjzy.ecg_collection.mapper.workbench.WorkbenchMapper;
import com.hnkjzy.ecg_collection.model.dto.workbench.WorkbenchPageQueryDto;
import com.hnkjzy.ecg_collection.model.dto.workbench.WorkbenchTimeQueryDto;
import com.hnkjzy.ecg_collection.model.vo.common.DictOptionVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.WorkbenchAlertDetailVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.WorkbenchDictVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.WorkbenchEcgDetailVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.WorkbenchHistoryEcgItemVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.WorkbenchLatestEcgItemVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.WorkbenchOverviewStatVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.WorkbenchPageListVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.WorkbenchPendingAlertItemVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.WorkbenchWaveformPointVo;
import com.hnkjzy.ecg_collection.service.impl.BaseServiceImpl;
import com.hnkjzy.ecg_collection.service.workbench.WorkbenchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Workbench service implementation.
 */
@Service
@RequiredArgsConstructor
public class WorkbenchServiceImpl extends BaseServiceImpl implements WorkbenchService {

    private static final long DEFAULT_PAGE_NUM = 1L;
    private static final long DEFAULT_PAGE_SIZE = 10L;
    private static final long MAX_PAGE_SIZE = 200L;

    private static final List<DateTimeFormatter> DATE_TIME_FORMATTERS = List.of(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
            DateTimeFormatter.ISO_LOCAL_DATE_TIME
    );

    private final WorkbenchMapper workbenchMapper;

    @Override
    public WorkbenchOverviewStatVo getOverview(WorkbenchTimeQueryDto queryDto) {
        WorkbenchTimeQueryDto query = normalizeTimeQuery(queryDto);
        DateRange range = resolveDateRange(query.getTimeType(), query.getStartTime(), query.getEndTime());

        WorkbenchOverviewStatVo statVo = workbenchMapper.selectOverview(range.getStartTime(), range.getEndTime());
        if (statVo == null) {
            statVo = new WorkbenchOverviewStatVo();
        }
        statVo.setEcgTotalCount(defaultLong(statVo.getEcgTotalCount()));
        statVo.setTodayAddCount(defaultLong(statVo.getTodayAddCount()));
        statVo.setPendingAnalyseCount(defaultLong(statVo.getPendingAnalyseCount()));
        statVo.setPendingAuditCount(defaultLong(statVo.getPendingAuditCount()));
        statVo.setAlertTotalCount(defaultLong(statVo.getAlertTotalCount()));
        statVo.setAlertHandledCount(defaultLong(statVo.getAlertHandledCount()));
        return statVo;
    }

    @Override
    public WorkbenchPageListVo<WorkbenchPendingAlertItemVo> pagePendingAlerts(WorkbenchPageQueryDto queryDto) {
        WorkbenchPageQueryDto query = normalizePageQuery(queryDto);
        DateRange range = resolveDateRange(query.getTimeType(), query.getStartTime(), query.getEndTime());

        long pageNum = normalizePageNum(query.getPageNum());
        long pageSize = normalizePageSize(query.getPageSize());
        Page<WorkbenchPendingAlertItemVo> page = new Page<>(pageNum, pageSize);
        IPage<WorkbenchPendingAlertItemVo> pageData =
                workbenchMapper.selectPendingAlertPage(page, range.getStartTime(), range.getEndTime());

        List<WorkbenchPendingAlertItemVo> records = pageData.getRecords();
        if (records == null) {
            records = Collections.emptyList();
        }

        return WorkbenchPageListVo.<WorkbenchPendingAlertItemVo>builder()
                .total(pageData.getTotal())
                .list(records)
                .build();
    }

    @Override
    public WorkbenchPageListVo<WorkbenchLatestEcgItemVo> pageLatestEcg(WorkbenchPageQueryDto queryDto) {
        WorkbenchPageQueryDto query = normalizePageQuery(queryDto);
        DateRange range = resolveDateRange(query.getTimeType(), query.getStartTime(), query.getEndTime());

        long pageNum = normalizePageNum(query.getPageNum());
        long pageSize = normalizePageSize(query.getPageSize());
        Page<WorkbenchLatestEcgItemVo> page = new Page<>(pageNum, pageSize);
        IPage<WorkbenchLatestEcgItemVo> pageData =
                workbenchMapper.selectLatestEcgPage(page, range.getStartTime(), range.getEndTime());

        List<WorkbenchLatestEcgItemVo> records = pageData.getRecords();
        if (records == null) {
            records = Collections.emptyList();
        }

        return WorkbenchPageListVo.<WorkbenchLatestEcgItemVo>builder()
                .total(pageData.getTotal())
                .list(records)
                .build();
    }

    @Override
    public WorkbenchAlertDetailVo getAlertDetail(Long alertId) {
        if (alertId == null || alertId <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "alertId parameter is invalid");
        }

        WorkbenchAlertDetailVo detailVo = workbenchMapper.selectAlertDetail(alertId);
        if (detailVo == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "alert record not found");
        }

        List<WorkbenchHistoryEcgItemVo> historyList;
        if (detailVo.getPatientId() == null) {
            historyList = Collections.emptyList();
        } else {
            historyList = workbenchMapper.selectAlertHistoryEcg(detailVo.getPatientId());
            if (historyList == null) {
                historyList = Collections.emptyList();
            }
        }
        detailVo.setHistoryEcgList(historyList);
        return detailVo;
    }

    @Override
    public WorkbenchEcgDetailVo getEcgDetail(Long ecgId) {
        if (ecgId == null || ecgId <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "ecgId parameter is invalid");
        }

        WorkbenchEcgDetailVo detailVo = workbenchMapper.selectEcgDetail(ecgId);
        if (detailVo == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "ecg data not found");
        }

        if (!StringUtils.hasText(detailVo.getAiConclusion()) && StringUtils.hasText(detailVo.getAiConclusionShort())) {
            detailVo.setAiConclusion(detailVo.getAiConclusionShort());
        }

        Integer samplingRate = detailVo.getSamplingRate() == null ? 250 : detailVo.getSamplingRate();
        detailVo.setSamplingRate(samplingRate);
        detailVo.setWaveformPreview(buildWaveformPreview(ecgId, samplingRate));
        return detailVo;
    }

    @Override
    public WorkbenchDictVo getDicts() {
        WorkbenchDictVo dictVo = new WorkbenchDictVo();

        List<DictOptionVo> timeTypeOptions = new ArrayList<>();
        timeTypeOptions.add(buildOption("today", "今日"));
        timeTypeOptions.add(buildOption("week", "本周"));
        timeTypeOptions.add(buildOption("month", "本月"));
        timeTypeOptions.add(buildOption("year", "本年"));

        List<DictOptionVo> alertLevelOptions = new ArrayList<>();
        alertLevelOptions.add(buildOption("3", "高危"));
        alertLevelOptions.add(buildOption("2", "中危"));
        alertLevelOptions.add(buildOption("1", "低危"));

        List<DictOptionVo> alertStatusOptions = new ArrayList<>();
        alertStatusOptions.add(buildOption("0", "待确认"));
        alertStatusOptions.add(buildOption("1", "待处理"));
        alertStatusOptions.add(buildOption("2", "处理中"));
        alertStatusOptions.add(buildOption("3", "已处理"));
        alertStatusOptions.add(buildOption("4", "已忽略"));

        List<DictOptionVo> deviceTypeOptions = new ArrayList<>();
        deviceTypeOptions.add(buildOption("1", "静态心电设备"));
        deviceTypeOptions.add(buildOption("2", "动态心电设备"));
        deviceTypeOptions.add(buildOption("3", "居家监护设备"));
        deviceTypeOptions.add(buildOption("4", "床边监护仪"));

        dictVo.setTimeTypeOptions(timeTypeOptions);
        dictVo.setAlertLevelOptions(alertLevelOptions);
        dictVo.setAlertStatusOptions(alertStatusOptions);
        dictVo.setDeviceTypeOptions(deviceTypeOptions);
        return dictVo;
    }

    private WorkbenchTimeQueryDto normalizeTimeQuery(WorkbenchTimeQueryDto queryDto) {
        WorkbenchTimeQueryDto query = queryDto == null ? new WorkbenchTimeQueryDto() : queryDto;
        query.setTimeType(trimToNull(query.getTimeType()));
        query.setStartTime(trimToNull(query.getStartTime()));
        query.setEndTime(trimToNull(query.getEndTime()));
        return query;
    }

    private WorkbenchPageQueryDto normalizePageQuery(WorkbenchPageQueryDto queryDto) {
        WorkbenchPageQueryDto query = queryDto == null ? new WorkbenchPageQueryDto() : queryDto;
        query.setTimeType(trimToNull(query.getTimeType()));
        query.setStartTime(trimToNull(query.getStartTime()));
        query.setEndTime(trimToNull(query.getEndTime()));
        return query;
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

    private DateRange resolveDateRange(String timeType, String startTimeText, String endTimeText) {
        LocalDateTime startTime = parseDateTime(startTimeText, false);
        LocalDateTime endTime = parseDateTime(endTimeText, true);

        if (startTime != null || endTime != null) {
            if (startTime == null) {
                startTime = endTime.minusDays(1);
            }
            if (endTime == null) {
                endTime = LocalDateTime.now().plusSeconds(1);
            }
        } else {
            String normalizedType = parseTimeType(timeType);
            if (normalizedType == null) {
                return new DateRange(null, null);
            }

            LocalDate today = LocalDate.now();
            switch (normalizedType) {
                case "today":
                    startTime = today.atStartOfDay();
                    endTime = startTime.plusDays(1);
                    break;
                case "week":
                    startTime = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).atStartOfDay();
                    endTime = startTime.plusDays(7);
                    break;
                case "month":
                    startTime = today.withDayOfMonth(1).atStartOfDay();
                    endTime = startTime.plusMonths(1);
                    break;
                case "year":
                    startTime = today.withDayOfYear(1).atStartOfDay();
                    endTime = startTime.plusYears(1);
                    break;
                default:
                    throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "timeType parameter is invalid");
            }
        }

        if (startTime.isAfter(endTime)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "startTime cannot be later than endTime");
        }
        return new DateRange(startTime, endTime);
    }

    private String parseTimeType(String timeType) {
        if (!StringUtils.hasText(timeType)) {
            return null;
        }

        String value = timeType.trim().toLowerCase();
        switch (value) {
            case "today":
            case "day":
            case "1":
            case "d":
            case "\u4eca\u65e5":
                return "today";
            case "week":
            case "2":
            case "w":
            case "\u672c\u5468":
                return "week";
            case "month":
            case "3":
            case "m":
            case "\u672c\u6708":
                return "month";
            case "year":
            case "4":
            case "y":
            case "\u672c\u5e74":
                return "year";
            default:
                throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "timeType parameter is invalid");
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
                throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "time format is invalid");
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
        throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "time format is invalid");
    }

    private List<WorkbenchWaveformPointVo> buildWaveformPreview(Long ecgId, Integer samplingRate) {
        int pointCount = 120;
        double offset = ecgId == null ? 0 : (ecgId % 97);
        double sampleDivider = samplingRate == null || samplingRate <= 0 ? 10.0 : Math.max(samplingRate / 25.0, 6.0);

        List<WorkbenchWaveformPointVo> points = new ArrayList<>(pointCount);
        for (int i = 0; i < pointCount; i++) {
            double angle = (i + offset) / sampleDivider;
            double value = Math.sin(angle) * 1.1 + Math.sin(angle * 0.35) * 0.25;
            value = Math.round(value * 10000D) / 10000D;
            points.add(new WorkbenchWaveformPointVo(i, value));
        }
        return points;
    }

    private DictOptionVo buildOption(String value, String label) {
        return DictOptionVo.builder().value(value).label(label).build();
    }

    private Long defaultLong(Long value) {
        return value == null ? 0L : value;
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

        public LocalDateTime getStartTime() {
            return startTime;
        }

        public LocalDateTime getEndTime() {
            return endTime;
        }
    }
}
