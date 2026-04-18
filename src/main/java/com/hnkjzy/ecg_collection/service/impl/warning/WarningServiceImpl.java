package com.hnkjzy.ecg_collection.service.impl.warning;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnkjzy.ecg_collection.common.exception.BusinessException;
import com.hnkjzy.ecg_collection.common.result.ResultCode;
import com.hnkjzy.ecg_collection.mapper.warning.WarningMapper;
import com.hnkjzy.ecg_collection.model.dto.warning.WarningHandleDto;
import com.hnkjzy.ecg_collection.model.dto.warning.WarningPageQueryDto;
import com.hnkjzy.ecg_collection.model.vo.common.DictOptionVo;
import com.hnkjzy.ecg_collection.model.vo.common.PageResultVo;
import com.hnkjzy.ecg_collection.model.vo.warning.WarningDetailVo;
import com.hnkjzy.ecg_collection.model.vo.warning.WarningDictVo;
import com.hnkjzy.ecg_collection.model.vo.warning.WarningGlobalStatVo;
import com.hnkjzy.ecg_collection.model.vo.warning.WarningHandleResultVo;
import com.hnkjzy.ecg_collection.model.vo.warning.WarningPageItemVo;
import com.hnkjzy.ecg_collection.service.impl.BaseServiceImpl;
import com.hnkjzy.ecg_collection.service.warning.WarningService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 预警模块服务实现。
 */
@Service
@RequiredArgsConstructor
public class WarningServiceImpl extends BaseServiceImpl implements WarningService {

    private static final long DEFAULT_PAGE_NUM = 1L;
    private static final long DEFAULT_PAGE_SIZE = 15L;
    private static final long MAX_PAGE_SIZE = 200L;
    private static final String SYSTEM_HANDLE_USER = "系统处理";

    private static final List<DateTimeFormatter> DATETIME_FORMATTERS = List.of(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
            DateTimeFormatter.ISO_LOCAL_DATE_TIME
    );

    private final WarningMapper warningMapper;

    @Override
    public WarningGlobalStatVo getGlobalStat() {
        WarningGlobalStatVo statVo = warningMapper.selectGlobalStat();
        if (statVo == null) {
            statVo = new WarningGlobalStatVo();
            statVo.setHighRiskCount(0L);
            statVo.setPendingHandleCount(0L);
            return statVo;
        }
        statVo.setHighRiskCount(defaultLong(statVo.getHighRiskCount()));
        statVo.setPendingHandleCount(defaultLong(statVo.getPendingHandleCount()));
        return statVo;
    }

    @Override
    public PageResultVo<WarningPageItemVo> pageWarningList(WarningPageQueryDto queryDto) {
        WarningPageQueryDto query = normalizeQuery(queryDto);
        Integer alertLevelCode = parseAlertLevel(query.getAlertLevel());
        Integer alertStatusCode = parseAlertStatus(query.getAlertStatus());
        LocalDateTime startTime = parseDateTime(query.getStartTime(), "startTime", false);
        LocalDateTime endTime = parseDateTime(query.getEndTime(), "endTime", true);

        if (startTime != null && endTime != null && startTime.isAfter(endTime)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "startTime 不能晚于 endTime");
        }

        long pageNum = normalizePageNum(query.getPageNum());
        long pageSize = normalizePageSize(query.getPageSize());

        Page<WarningPageItemVo> page = new Page<>(pageNum, pageSize);
        IPage<WarningPageItemVo> pageData = warningMapper.selectWarningPage(page, query, alertLevelCode, alertStatusCode, startTime, endTime);

        List<WarningPageItemVo> records = pageData.getRecords();
        if (records == null) {
            records = Collections.emptyList();
        }

        long baseRowNum = (pageNum - 1) * pageSize;
        for (int i = 0; i < records.size(); i++) {
            WarningPageItemVo item = records.get(i);
            item.setRowNum(baseRowNum + i + 1);
        }

        return PageResultVo.<WarningPageItemVo>builder()
                .pageNum(pageData.getCurrent())
                .pageSize(pageData.getSize())
                .total(pageData.getTotal())
                .pages(pageData.getPages())
                .records(records)
                .build();
    }

    @Override
    public WarningDetailVo getWarningDetail(Long alertId) {
        if (alertId == null || alertId <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "alertId 参数不合法");
        }

        WarningDetailVo detailVo = warningMapper.selectWarningDetail(alertId);
        if (detailVo == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "预警不存在");
        }
        return detailVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WarningHandleResultVo handleWarning(WarningHandleDto handleDto) {
        if (handleDto == null || handleDto.getAlertId() == null || handleDto.getAlertId() <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "alertId 参数不合法");
        }

        String handleRemark = trimToNull(handleDto.getHandleRemark());
        if (handleRemark != null && handleRemark.length() > 256) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "handleRemark 长度不能超过 256");
        }

        WarningDetailVo current = warningMapper.selectWarningDetail(handleDto.getAlertId());
        if (current == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "预警不存在");
        }
        if (current.getAlertStatus() != null && current.getAlertStatus() == 3) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR.getCode(), "预警已处理，请勿重复提交");
        }
        if (current.getAlertStatus() != null && current.getAlertStatus() == 4) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR.getCode(), "预警已忽略，无法处理");
        }

        String finalRemark = StringUtils.hasText(handleRemark) ? handleRemark : "已处理";
        warningMapper.updateHandleWarning(handleDto.getAlertId(), SYSTEM_HANDLE_USER, finalRemark);

        WarningDetailVo latest = warningMapper.selectWarningDetail(handleDto.getAlertId());
        WarningHandleResultVo resultVo = new WarningHandleResultVo();
        resultVo.setAlertId(latest.getAlertId());
        resultVo.setAlertStatus(latest.getAlertStatus());
        resultVo.setAlertStatusText(latest.getAlertStatusText());
        resultVo.setHandleTime(latest.getHandleTime());
        resultVo.setHandleRemark(latest.getHandleRemark());
        return resultVo;
    }

    @Override
    public WarningDictVo getWarningDicts() {
        WarningDictVo dictVo = new WarningDictVo();

        List<DictOptionVo> alertLevelOptions = new ArrayList<>();
        alertLevelOptions.add(buildOption("", "全部级别"));
        alertLevelOptions.add(buildOption("1", "低危"));
        alertLevelOptions.add(buildOption("2", "中危"));
        alertLevelOptions.add(buildOption("3", "高危"));

        List<DictOptionVo> alertStatusOptions = new ArrayList<>();
        alertStatusOptions.add(buildOption("", "全部状态"));
        alertStatusOptions.add(buildOption("0", "待确认"));
        alertStatusOptions.add(buildOption("1", "待处理"));
        alertStatusOptions.add(buildOption("2", "处理中"));
        alertStatusOptions.add(buildOption("3", "已处理"));
        alertStatusOptions.add(buildOption("4", "已忽略"));

        dictVo.setAlertLevelOptions(alertLevelOptions);
        dictVo.setAlertStatusOptions(alertStatusOptions);
        return dictVo;
    }

    private DictOptionVo buildOption(String value, String label) {
        return DictOptionVo.builder().value(value).label(label).build();
    }

    private WarningPageQueryDto normalizeQuery(WarningPageQueryDto queryDto) {
        WarningPageQueryDto query = queryDto == null ? new WarningPageQueryDto() : queryDto;
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

        switch (alertLevel) {
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

        switch (alertStatus) {
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

    private LocalDateTime parseDateTime(String value, String fieldName, boolean endOfDayWhenOnlyDate) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        String trimValue = value.trim();

        if (trimValue.length() == 10) {
            try {
                LocalDate date = LocalDate.parse(trimValue, DateTimeFormatter.ISO_LOCAL_DATE);
                return endOfDayWhenOnlyDate ? date.atTime(23, 59, 59) : date.atStartOfDay();
            } catch (DateTimeParseException ex) {
                throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), fieldName + " 参数格式错误");
            }
        }

        for (DateTimeFormatter formatter : DATETIME_FORMATTERS) {
            try {
                return LocalDateTime.parse(trimValue, formatter);
            } catch (DateTimeParseException ignored) {
                // try next formatter
            }
        }
        throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), fieldName + " 参数格式错误");
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
