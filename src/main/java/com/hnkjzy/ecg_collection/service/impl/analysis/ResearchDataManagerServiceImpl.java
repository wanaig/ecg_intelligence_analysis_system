package com.hnkjzy.ecg_collection.service.impl.analysis;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnkjzy.ecg_collection.common.exception.BusinessException;
import com.hnkjzy.ecg_collection.common.result.ResultCode;
import com.hnkjzy.ecg_collection.mapper.analysis.ResearchDataManagerMapper;
import com.hnkjzy.ecg_collection.mapper.system.SystemUserMapper;
import com.hnkjzy.ecg_collection.model.dto.analysis.ResearchDataPageQueryDto;
import com.hnkjzy.ecg_collection.model.dto.analysis.ResearchDataSelectedExportDto;
import com.hnkjzy.ecg_collection.model.entity.system.SysOperationLogEntity;
import com.hnkjzy.ecg_collection.model.vo.analysis.ResearchDataExportFileVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.ResearchDataExportRowVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.ResearchDataPageItemVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.ResearchDataPageResultVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemUserOperatorVo;
import com.hnkjzy.ecg_collection.service.analysis.ResearchDataManagerService;
import com.hnkjzy.ecg_collection.service.impl.BaseServiceImpl;
import com.hnkjzy.ecg_collection.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Research data management service implementation.
 */
@Service
@RequiredArgsConstructor
public class ResearchDataManagerServiceImpl extends BaseServiceImpl implements ResearchDataManagerService {

    private static final long DEFAULT_PAGE_NUM = 1L;
    private static final long DEFAULT_PAGE_SIZE = 10L;
    private static final long MAX_PAGE_SIZE = 200L;
    private static final int UPDATE_BATCH_SIZE = 500;

    private static final long OP_LOG_ID_BASE = 1400L;
    private static final long OP_LOG_ID_MAX_ALLOWED = 9_999_999_999L;

    private static final String MODULE_RESEARCH_DATA = "科研数据管理";
    private static final String OPERATION_EXPORT = "导出";
    private static final String CSV_CONTENT_TYPE = "text/csv;charset=UTF-8";

    private static final DateTimeFormatter FILE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final DateTimeFormatter CSV_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final List<DateTimeFormatter> DATE_TIME_FORMATTERS = List.of(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
            DateTimeFormatter.ISO_LOCAL_DATE_TIME
    );

    private final ResearchDataManagerMapper researchDataManagerMapper;
    private final SystemUserMapper systemUserMapper;
    private final JwtUtil jwtUtil;

    @Override
    public ResearchDataPageResultVo pageResearchData(ResearchDataPageQueryDto queryDto) {
        ResearchDataPageQueryDto query = normalizeQuery(queryDto);
        DateRange dateRange = resolveDateRange(query.getStartTime(), query.getEndTime());

        long pageNum = normalizePageNum(query.getPageNum());
        long pageSize = normalizePageSize(query.getPageSize());

        Page<ResearchDataPageItemVo> page = new Page<>(pageNum, pageSize);
        IPage<ResearchDataPageItemVo> pageData = researchDataManagerMapper.selectResearchPage(
                page,
                query,
                dateRange.getStartTime(),
                dateRange.getEndTime()
        );

        List<ResearchDataPageItemVo> records = pageData.getRecords();
        if (records == null) {
            records = Collections.emptyList();
        }

        long rowStart = (pageNum - 1) * pageSize;
        for (int i = 0; i < records.size(); i++) {
            ResearchDataPageItemVo item = records.get(i);
            if (item == null) {
                continue;
            }
            item.setRowNum(rowStart + i + 1);
            item.setGenderText(defaultString(item.getGenderText(), genderText(item.getGender())));
            item.setIsDataApproved(item.getIsDataApproved() == null ? 0 : item.getIsDataApproved());
            item.setIsDataApprovedText(defaultString(item.getIsDataApprovedText(), approvedText(item.getIsDataApproved())));
            item.setIsExported(item.getIsExported() == null ? 0 : item.getIsExported());
            item.setIsExportedText(defaultString(item.getIsExportedText(), exportedText(item.getIsExported())));
        }

        return ResearchDataPageResultVo.builder()
                .total(pageData.getTotal())
                .pages(pageData.getPages())
                .list(records)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResearchDataExportFileVo exportSelectedMaskedData(ResearchDataSelectedExportDto exportDto,
                                                             HttpServletRequest request) {
        List<Long> researchIdList = normalizeResearchIdList(exportDto);
        SystemUserOperatorVo operator = resolveCurrentOperator(request);

        List<ResearchDataExportRowVo> rows = researchDataManagerMapper.selectExportRowsByResearchIds(researchIdList);
        if (rows == null || rows.isEmpty()) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "未查询到可导出的科研数据");
        }

        byte[] content = buildCsv(rows);
        List<Long> exportedIds = extractResearchIds(rows);
        updateExportStatus(exportedIds);

        insertExportLog(operator, request,
                "科研数据脱敏导出(选中) 条数=" + rows.size() + ", researchIdList=" + summarizeIds(researchIdList, 20));

        return new ResearchDataExportFileVo(buildFileName("selected"), CSV_CONTENT_TYPE, content);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResearchDataExportFileVo exportFilteredMaskedData(ResearchDataPageQueryDto queryDto,
                                                             HttpServletRequest request) {
        ResearchDataPageQueryDto query = normalizeQuery(queryDto);
        DateRange dateRange = resolveDateRange(query.getStartTime(), query.getEndTime());
        SystemUserOperatorVo operator = resolveCurrentOperator(request);

        List<ResearchDataExportRowVo> rows = researchDataManagerMapper.selectExportRowsByFilter(
                query,
                dateRange.getStartTime(),
                dateRange.getEndTime()
        );
        if (rows == null || rows.isEmpty()) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "筛选结果无可导出的科研数据");
        }

        byte[] content = buildCsv(rows);
        List<Long> exportedIds = extractResearchIds(rows);
        updateExportStatus(exportedIds);

        insertExportLog(operator, request, buildFilterExportContent(query, dateRange, rows.size()));
        return new ResearchDataExportFileVo(buildFileName("all"), CSV_CONTENT_TYPE, content);
    }

    private void updateExportStatus(List<Long> researchIds) {
        if (researchIds == null || researchIds.isEmpty()) {
            return;
        }
        for (int start = 0; start < researchIds.size(); start += UPDATE_BATCH_SIZE) {
            int end = Math.min(start + UPDATE_BATCH_SIZE, researchIds.size());
            List<Long> batch = researchIds.subList(start, end);
            researchDataManagerMapper.markExportedByResearchIds(batch);
        }
    }

    private void insertExportLog(SystemUserOperatorVo operator, HttpServletRequest request, String content) {
        SysOperationLogEntity logEntity = new SysOperationLogEntity();
        logEntity.setLogId(nextOperationLogId());
        logEntity.setUserId(operator.getUserId());
        logEntity.setRealName(resolveOperatorName(operator));
        logEntity.setModule(MODULE_RESEARCH_DATA);
        logEntity.setOperationType(OPERATION_EXPORT);
        logEntity.setOperationContent(content);
        logEntity.setRequestIp(resolveRequestIp(request));
        logEntity.setOperationTime(LocalDateTime.now());
        logEntity.setIsDeleted(0);
        systemUserMapper.insertOperationLog(logEntity);
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

    private ResearchDataPageQueryDto normalizeQuery(ResearchDataPageQueryDto queryDto) {
        ResearchDataPageQueryDto query = queryDto == null ? new ResearchDataPageQueryDto() : queryDto;
        query.setPatientKeyword(trimToNull(query.getPatientKeyword()));
        query.setEmrKeyword(trimToNull(query.getEmrKeyword()));
        query.setEcgKeyword(trimToNull(query.getEcgKeyword()));
        query.setStartTime(trimToNull(query.getStartTime()));
        query.setEndTime(trimToNull(query.getEndTime()));
        return query;
    }

    private List<Long> normalizeResearchIdList(ResearchDataSelectedExportDto exportDto) {
        if (exportDto == null || exportDto.getResearchIdList() == null || exportDto.getResearchIdList().isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "researchIdList 参数不合法");
        }

        LinkedHashSet<Long> uniqueSet = new LinkedHashSet<>();
        for (Long researchId : exportDto.getResearchIdList()) {
            if (researchId == null || researchId <= 0) {
                throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "researchIdList 参数不合法");
            }
            uniqueSet.add(researchId);
        }
        return new ArrayList<>(uniqueSet);
    }

    private DateRange resolveDateRange(String startTimeText, String endTimeText) {
        LocalDateTime startTime = parseDateTime(startTimeText, false);
        LocalDateTime endTime = parseDateTime(endTimeText, true);

        if (startTime != null && endTime != null && startTime.isAfter(endTime)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "startTime 不能晚于 endTime");
        }
        return new DateRange(startTime, endTime);
    }

    private LocalDateTime parseDateTime(String value, boolean endInclusive) {
        if (!StringUtils.hasText(value)) {
            return null;
        }

        String trimValue = value.trim();
        try {
            LocalDate date = LocalDate.parse(trimValue, DateTimeFormatter.ISO_LOCAL_DATE);
            return endInclusive ? date.atTime(23, 59, 59) : date.atStartOfDay();
        } catch (DateTimeParseException ignored) {
            // Continue trying datetime formatters.
        }

        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                return LocalDateTime.parse(trimValue, formatter);
            } catch (DateTimeParseException ignored) {
                // Continue trying available formatters.
            }
        }

        throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), (endInclusive ? "endTime" : "startTime") + " 参数格式不合法");
    }

    private byte[] buildCsv(List<ResearchDataExportRowVo> rows) {
        StringBuilder builder = new StringBuilder();
        builder.append("研究ID,患者姓名(脱敏),性别,年龄,住院号(脱敏),病区,主要EMR诊断,心电特征总结,采集时间,伦理授权,导出状态\n");

        for (ResearchDataExportRowVo row : rows) {
            if (row == null) {
                continue;
            }

            List<String> columns = new ArrayList<>();
            columns.add(safeText(row.getResearchId()));
            columns.add(maskName(row.getPatientName()));
            columns.add(genderText(row.getGender()));
            columns.add(safeText(row.getAge()));
            columns.add(maskInpatientNo(row.getInpatientNo()));
            columns.add(safeText(row.getDeptName()));
            columns.add(safeText(row.getMainEmrDiagnosis()));
            columns.add(safeText(row.getEcgFeatureSummary()));
            columns.add(formatDateTime(row.getCollectionTime()));
            columns.add(approvedText(row.getIsDataApproved()));
            columns.add(row.getIsExported() != null && row.getIsExported() == 1 ? "已导出" : "首次导出");

            builder.append(joinCsv(columns)).append("\n");
        }

        byte[] body = builder.toString().getBytes(StandardCharsets.UTF_8);
        byte[] bom = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        byte[] result = new byte[bom.length + body.length];
        System.arraycopy(bom, 0, result, 0, bom.length);
        System.arraycopy(body, 0, result, bom.length, body.length);
        return result;
    }

    private String joinCsv(List<String> columns) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < columns.size(); i++) {
            if (i > 0) {
                builder.append(',');
            }
            builder.append(escapeCsv(columns.get(i)));
        }
        return builder.toString();
    }

    private String escapeCsv(String value) {
        String text = value == null ? "" : value;
        boolean needsQuote = text.contains(",") || text.contains("\"") || text.contains("\n") || text.contains("\r");
        if (text.contains("\"")) {
            text = text.replace("\"", "\"\"");
        }
        if (needsQuote) {
            return "\"" + text + "\"";
        }
        return text;
    }

    private String maskName(String patientName) {
        String value = trimToNull(patientName);
        if (value == null) {
            return "-";
        }
        if (value.length() == 1) {
            return "*";
        }
        if (value.length() == 2) {
            return value.substring(0, 1) + "*";
        }
        return value.substring(0, 1) + "*".repeat(value.length() - 2) + value.substring(value.length() - 1);
    }

    private String maskInpatientNo(String inpatientNo) {
        String value = trimToNull(inpatientNo);
        if (value == null) {
            return "-";
        }
        int length = value.length();
        if (length <= 2) {
            return "*".repeat(length);
        }
        if (length <= 6) {
            return value.substring(0, 1) + "***" + value.substring(length - 1);
        }
        return value.substring(0, 2) + "***" + value.substring(length - 2);
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "-";
        }
        return dateTime.format(CSV_DATE_TIME_FORMATTER);
    }

    private String safeText(Object value) {
        if (value == null) {
            return "-";
        }
        String text = String.valueOf(value);
        return StringUtils.hasText(text) ? text.trim() : "-";
    }

    private String genderText(Integer gender) {
        if (gender == null) {
            return "未知";
        }
        if (gender == 1) {
            return "男";
        }
        if (gender == 2) {
            return "女";
        }
        return "未知";
    }

    private String approvedText(Integer isDataApproved) {
        return isDataApproved != null && isDataApproved == 1 ? "已授权" : "未授权";
    }

    private String exportedText(Integer isExported) {
        return isExported != null && isExported == 1 ? "已导出" : "未导出";
    }

    private List<Long> extractResearchIds(List<ResearchDataExportRowVo> rows) {
        LinkedHashSet<Long> ids = new LinkedHashSet<>();
        for (ResearchDataExportRowVo row : rows) {
            if (row != null && row.getResearchId() != null && row.getResearchId() > 0) {
                ids.add(row.getResearchId());
            }
        }
        return new ArrayList<>(ids);
    }

    private String summarizeIds(List<Long> ids, int maxCount) {
        if (ids == null || ids.isEmpty()) {
            return "[]";
        }
        int limit = Math.max(maxCount, 1);
        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < ids.size() && i < limit; i++) {
            if (i > 0) {
                builder.append(',');
            }
            builder.append(ids.get(i));
        }
        if (ids.size() > limit) {
            builder.append(",...");
        }
        builder.append(']');
        return builder.toString();
    }

    private String buildFilterExportContent(ResearchDataPageQueryDto query, DateRange dateRange, int count) {
        return "科研数据脱敏导出(筛选全量) 条数=" + count
                + ", patientKeyword=" + defaultPlaceholder(query.getPatientKeyword())
                + ", emrKeyword=" + defaultPlaceholder(query.getEmrKeyword())
                + ", ecgKeyword=" + defaultPlaceholder(query.getEcgKeyword())
                + ", deptId=" + (query.getDeptId() == null ? "-" : query.getDeptId())
                + ", startTime=" + formatLogDateTime(dateRange.getStartTime())
                + ", endTime=" + formatLogDateTime(dateRange.getEndTime());
    }

    private String formatLogDateTime(LocalDateTime value) {
        if (value == null) {
            return "-";
        }
        return value.format(CSV_DATE_TIME_FORMATTER);
    }

    private String buildFileName(String suffix) {
        return "research-data-" + suffix + "-" + LocalDateTime.now().format(FILE_TIME_FORMATTER) + ".csv";
    }

    private Long nextOperationLogId() {
        Long maxLogId = systemUserMapper.selectMaxOperationLogIdInRangeForUpdate(OP_LOG_ID_MAX_ALLOWED);
        long base = maxLogId == null ? OP_LOG_ID_BASE : maxLogId;
        if (base >= OP_LOG_ID_MAX_ALLOWED) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR.getCode(), "操作日志ID已达到上限，请联系管理员");
        }
        return base + 1;
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
        String text = value.trim();
        if ("unknown".equalsIgnoreCase(text)) {
            return null;
        }
        return text;
    }

    private String defaultPlaceholder(String value) {
        return value == null ? "-" : value;
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
}
