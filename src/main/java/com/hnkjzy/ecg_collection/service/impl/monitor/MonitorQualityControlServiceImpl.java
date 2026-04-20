package com.hnkjzy.ecg_collection.service.impl.monitor;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnkjzy.ecg_collection.common.exception.BusinessException;
import com.hnkjzy.ecg_collection.common.result.ResultCode;
import com.hnkjzy.ecg_collection.mapper.monitor.MonitorQualityControlMapper;
import com.hnkjzy.ecg_collection.model.dto.monitor.MonitorQualityControlCreateDto;
import com.hnkjzy.ecg_collection.model.dto.monitor.MonitorQualityControlPageQueryDto;
import com.hnkjzy.ecg_collection.model.dto.monitor.MonitorQualityControlUpdateDto;
import com.hnkjzy.ecg_collection.model.entity.monitor.EcgDeviceQualityControlEntity;
import com.hnkjzy.ecg_collection.model.vo.common.DictOptionVo;
import com.hnkjzy.ecg_collection.model.vo.common.PageResultVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorQualityControlDeleteResultVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorQualityControlDetailVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorQualityControlDeviceInfoVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorQualityControlDictVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorQualityControlIndicatorVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorQualityControlPageItemVo;
import com.hnkjzy.ecg_collection.service.impl.BaseServiceImpl;
import com.hnkjzy.ecg_collection.service.monitor.MonitorQualityControlService;
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
 * 实时监护-质控管理服务实现。
 */
@Service
@RequiredArgsConstructor
public class MonitorQualityControlServiceImpl extends BaseServiceImpl implements MonitorQualityControlService {

    private static final long DEFAULT_PAGE_NUM = 1L;
    private static final long DEFAULT_PAGE_SIZE = 10L;
    private static final long MAX_PAGE_SIZE = 200L;
    private static final long QC_ID_BASE = 2800L;
    private static final long QC_ID_MAX_ALLOWED = 9_999_999_999L;

    private static final DateTimeFormatter STANDARD_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final List<DateTimeFormatter> DATETIME_FORMATTERS = List.of(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
            DateTimeFormatter.ISO_LOCAL_DATE_TIME
    );

    private final MonitorQualityControlMapper monitorQualityControlMapper;

    @Override
    public PageResultVo<MonitorQualityControlPageItemVo> pageQualityControls(MonitorQualityControlPageQueryDto queryDto) {
        MonitorQualityControlPageQueryDto query = normalizePageQuery(queryDto);
        if (query.getDeviceId() != null && query.getDeviceId() <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "deviceId 参数不合法");
        }

        String statusText = parseTestResult(query.getStatus(), false, "status");
        String testTypeText = parseTestType(query.getTestType(), false);
        LocalDateTime startTime = parseDateTime(query.getStartTime(), "startTime", false);
        LocalDateTime endTime = parseDateTime(query.getEndTime(), "endTime", true);

        if (startTime != null && endTime != null && startTime.isAfter(endTime)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "startTime 不能晚于 endTime");
        }

        long pageNum = normalizePageNum(query.getPageNum());
        long pageSize = normalizePageSize(query.getPageSize());

        Page<MonitorQualityControlPageItemVo> page = new Page<>(pageNum, pageSize);
        IPage<MonitorQualityControlPageItemVo> pageData = monitorQualityControlMapper.selectQualityControlPage(page, query, statusText, testTypeText, startTime, endTime);

        List<MonitorQualityControlPageItemVo> records = pageData.getRecords();
        if (records == null) {
            records = Collections.emptyList();
        }

        return PageResultVo.<MonitorQualityControlPageItemVo>builder()
                .pageNum(pageData.getCurrent())
                .pageSize(pageData.getSize())
                .total(pageData.getTotal())
                .pages(pageData.getPages())
                .records(records)
                .build();
    }

    @Override
    public MonitorQualityControlDictVo getQualityControlDicts() {
        MonitorQualityControlDictVo dictVo = new MonitorQualityControlDictVo();

        List<DictOptionVo> deviceOptions = monitorQualityControlMapper.selectDeviceOptions();
        if (deviceOptions == null) {
            deviceOptions = new ArrayList<>();
        }
        deviceOptions.add(0, buildOption("", "全部设备"));

        List<DictOptionVo> testStatusOptions = new ArrayList<>();
        testStatusOptions.add(buildOption("", "全部状态"));
        testStatusOptions.add(buildOption("1", "通过"));
        testStatusOptions.add(buildOption("2", "未通过"));

        List<DictOptionVo> testTypeOptions = new ArrayList<>();
        testTypeOptions.add(buildOption("", "全部类型"));
        testTypeOptions.add(buildOption("1", "日检"));
        testTypeOptions.add(buildOption("2", "周检"));
        testTypeOptions.add(buildOption("3", "月检"));
        testTypeOptions.add(buildOption("4", "远程检测"));

        dictVo.setDeviceOptions(deviceOptions);
        dictVo.setTestStatusOptions(testStatusOptions);
        dictVo.setTestTypeOptions(testTypeOptions);
        return dictVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MonitorQualityControlDetailVo createQualityControl(MonitorQualityControlCreateDto createDto) {
        if (createDto == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "请求体不能为空");
        }

        Long deviceId = requirePositiveId(createDto.getDeviceId(), "deviceId 参数不合法");
        MonitorQualityControlDeviceInfoVo deviceInfo = requireDeviceInfo(deviceId);
        Long testUserId = createDto.getTestUserId() == null ? null : requirePositiveId(createDto.getTestUserId(), "testUserId 参数不合法");
        Long qcId = nextQcId();

        EcgDeviceQualityControlEntity entity = new EcgDeviceQualityControlEntity();
        entity.setQcId(qcId);
        entity.setDeviceId(deviceInfo.getDeviceId());
        entity.setDeviceName(deviceInfo.getDeviceName());
        entity.setDeptId(deviceInfo.getDeptId());
        entity.setDeptName(deviceInfo.getDeptName());
        entity.setTestTime(resolveTestTime(createDto.getTestTime()));
        entity.setTestType(parseTestType(createDto.getTestType(), true));
        entity.setTestUserId(testUserId);
        entity.setTestUserName(resolveTesterName(testUserId, createDto.getTestUserName(), true));
        entity.setDeviceStatus(parseDeviceStatus(createDto.getDeviceStatus(), true));
        entity.setTestResult(parseTestResult(createDto.getTestResult(), true, "testResult"));
        entity.setRemark(normalizeRemark(createDto.getRemark()));
        entity.setIsDeleted(0);

        monitorQualityControlMapper.insertQualityControl(entity);
        return getQualityControlDetail(entity.getQcId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MonitorQualityControlDetailVo updateQualityControl(MonitorQualityControlUpdateDto updateDto) {
        if (updateDto == null || updateDto.getQcId() == null || updateDto.getQcId() <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "qcId 参数不合法");
        }

        MonitorQualityControlDetailVo existing = monitorQualityControlMapper.selectQualityControlDetail(updateDto.getQcId());
        if (existing == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "质控记录不存在");
        }

        Long finalDeviceId = updateDto.getDeviceId() == null
                ? existing.getDeviceId()
                : requirePositiveId(updateDto.getDeviceId(), "deviceId 参数不合法");
        MonitorQualityControlDeviceInfoVo deviceInfo = requireDeviceInfo(finalDeviceId);

        boolean testTypeProvided = StringUtils.hasText(updateDto.getTestType());
        String finalTestType = testTypeProvided ? parseTestType(updateDto.getTestType(), true) : existing.getTestType();

        boolean deviceStatusProvided = StringUtils.hasText(updateDto.getDeviceStatus());
        String finalDeviceStatus = deviceStatusProvided ? parseDeviceStatus(updateDto.getDeviceStatus(), true) : existing.getDeviceStatus();

        boolean testResultProvided = StringUtils.hasText(updateDto.getTestResult());
        String finalTestResult = testResultProvided
                ? parseTestResult(updateDto.getTestResult(), true, "testResult")
                : existing.getTestResult();

        LocalDateTime finalTestTime = StringUtils.hasText(updateDto.getTestTime())
                ? parseDateTime(updateDto.getTestTime(), "testTime", false)
                : existing.getTestTime();

        Long finalUserId = updateDto.getTestUserId() == null
                ? existing.getTestUserId()
                : requirePositiveId(updateDto.getTestUserId(), "testUserId 参数不合法");

        String finalUserName = resolveUpdateTesterName(finalUserId,
                updateDto.getTestUserId() != null,
                updateDto.getTestUserName(),
                existing.getTestUserName());

        String finalRemark = updateDto.getRemark() == null
                ? existing.getRemark()
                : normalizeRemark(updateDto.getRemark());

        EcgDeviceQualityControlEntity entity = new EcgDeviceQualityControlEntity();
        entity.setQcId(updateDto.getQcId());
        entity.setDeviceId(deviceInfo.getDeviceId());
        entity.setDeviceName(deviceInfo.getDeviceName());
        entity.setDeptId(deviceInfo.getDeptId());
        entity.setDeptName(deviceInfo.getDeptName());
        entity.setTestTime(finalTestTime);
        entity.setTestType(finalTestType);
        entity.setTestUserId(finalUserId);
        entity.setTestUserName(finalUserName);
        entity.setDeviceStatus(finalDeviceStatus);
        entity.setTestResult(finalTestResult);
        entity.setRemark(finalRemark);

        monitorQualityControlMapper.updateQualityControlById(entity);
        return getQualityControlDetail(updateDto.getQcId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MonitorQualityControlDeleteResultVo deleteQualityControl(Long qcId) {
        if (qcId == null || qcId <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "qcId 参数不合法");
        }

        MonitorQualityControlDetailVo existing = monitorQualityControlMapper.selectQualityControlDetail(qcId);
        if (existing == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "质控记录不存在");
        }

        monitorQualityControlMapper.logicalDeleteQualityControl(qcId);

        MonitorQualityControlDeleteResultVo resultVo = new MonitorQualityControlDeleteResultVo();
        resultVo.setQcId(qcId);
        resultVo.setDeleted(true);
        return resultVo;
    }

    @Override
    public MonitorQualityControlDetailVo getQualityControlDetail(Long qcId) {
        if (qcId == null || qcId <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "qcId 参数不合法");
        }

        MonitorQualityControlDetailVo detailVo = monitorQualityControlMapper.selectQualityControlDetail(qcId);
        if (detailVo == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "质控记录不存在");
        }

        detailVo.setIndicatorDetails(buildIndicatorDetails(detailVo));
        return detailVo;
    }

    private DictOptionVo buildOption(String value, String label) {
        return DictOptionVo.builder().value(value).label(label).build();
    }

    private MonitorQualityControlPageQueryDto normalizePageQuery(MonitorQualityControlPageQueryDto queryDto) {
        MonitorQualityControlPageQueryDto query = queryDto == null ? new MonitorQualityControlPageQueryDto() : queryDto;
        query.setStatus(trimToNull(query.getStatus()));
        query.setTestType(trimToNull(query.getTestType()));
        query.setStartTime(trimToNull(query.getStartTime()));
        query.setEndTime(trimToNull(query.getEndTime()));
        return query;
    }

    private String parseTestType(String testType, boolean required) {
        if (!StringUtils.hasText(testType)) {
            if (required) {
                throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "testType 参数不合法");
            }
            return null;
        }

        String value = testType.trim();
        if (!required && ("全部类型".equals(value) || "all".equalsIgnoreCase(value))) {
            return null;
        }
        switch (value) {
            case "1":
            case "日检":
                return "日检";
            case "2":
            case "周检":
                return "周检";
            case "3":
            case "月检":
                return "月检";
            case "4":
            case "远程检测":
                return "远程检测";
            default:
                throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "testType 参数不合法");
        }
    }

    private String parseDeviceStatus(String deviceStatus, boolean required) {
        if (!StringUtils.hasText(deviceStatus)) {
            if (required) {
                throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "deviceStatus 参数不合法");
            }
            return null;
        }

        String value = deviceStatus.trim();
        switch (value) {
            case "1":
            case "正常":
                return "正常";
            case "2":
            case "异常":
                return "异常";
            default:
                throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "deviceStatus 参数不合法");
        }
    }

    private String parseTestResult(String testResult, boolean required, String fieldName) {
        if (!StringUtils.hasText(testResult)) {
            if (required) {
                throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), fieldName + " 参数不合法");
            }
            return null;
        }

        String value = testResult.trim();
        if (!required && "全部状态".equals(value)) {
            return null;
        }
        switch (value) {
            case "1":
            case "通过":
            case "PASS":
            case "pass":
                return "通过";
            case "2":
            case "未通过":
            case "FAIL":
            case "fail":
                return "未通过";
            default:
                throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), fieldName + " 参数不合法");
        }
    }

    private LocalDateTime resolveTestTime(String testTime) {
        LocalDateTime value = parseDateTime(testTime, "testTime", false);
        return value == null ? LocalDateTime.now() : value;
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

    private String resolveTesterName(Long testUserId, String testUserName, boolean required) {
        if (testUserId != null) {
            String dbUserName = monitorQualityControlMapper.selectUserNameById(testUserId);
            if (!StringUtils.hasText(dbUserName)) {
                throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "testUserId 参数不合法");
            }
            return dbUserName;
        }

        String finalUserName = trimToNull(testUserName);
        if (required && !StringUtils.hasText(finalUserName)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "testUserName 参数不合法");
        }
        return finalUserName;
    }

    private String resolveUpdateTesterName(Long finalUserId, boolean userIdUpdated, String inputName, String existingName) {
        if (userIdUpdated) {
            return resolveTesterName(finalUserId, inputName, true);
        }

        String providedName = trimToNull(inputName);
        if (StringUtils.hasText(providedName)) {
            return providedName;
        }

        if (StringUtils.hasText(existingName)) {
            return existingName;
        }

        if (finalUserId != null) {
            return resolveTesterName(finalUserId, null, true);
        }

        throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "testUserName 参数不合法");
    }

    private String normalizeRemark(String remark) {
        String finalRemark = trimToNull(remark);
        if (finalRemark != null && finalRemark.length() > 256) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "remark 长度不能超过 256");
        }
        return finalRemark;
    }

    private MonitorQualityControlDeviceInfoVo requireDeviceInfo(Long deviceId) {
        MonitorQualityControlDeviceInfoVo deviceInfo = monitorQualityControlMapper.selectDeviceInfoById(deviceId);
        if (deviceInfo == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "deviceId 参数不合法");
        }
        return deviceInfo;
    }

    private Long requirePositiveId(Long value, String message) {
        if (value == null || value <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), message);
        }
        return value;
    }

    private List<MonitorQualityControlIndicatorVo> buildIndicatorDetails(MonitorQualityControlDetailVo detailVo) {
        List<MonitorQualityControlIndicatorVo> indicators = new ArrayList<>();
        indicators.add(buildIndicator("device_status", "设备状态", detailVo.getDeviceStatus(), "正常".equals(detailVo.getDeviceStatus()) ? "达标" : "异常"));
        indicators.add(buildIndicator("test_result", "测试结果", detailVo.getTestResult(), detailVo.getTestResult()));
        indicators.add(buildIndicator("test_type", "测试类型", detailVo.getTestType(), "已执行"));

        if (detailVo.getTestTime() != null) {
            indicators.add(buildIndicator("test_time", "测试时间", detailVo.getTestTime().format(STANDARD_DATETIME_FORMATTER), "已记录"));
        }
        return indicators;
    }

    private MonitorQualityControlIndicatorVo buildIndicator(String code, String name, String value, String result) {
        MonitorQualityControlIndicatorVo indicator = new MonitorQualityControlIndicatorVo();
        indicator.setIndicatorCode(code);
        indicator.setIndicatorName(name);
        indicator.setIndicatorValue(value);
        indicator.setResult(result);
        return indicator;
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

    private Long nextQcId() {
        Long maxQcId = monitorQualityControlMapper.selectMaxQcIdInRangeForUpdate(QC_ID_MAX_ALLOWED);
        long base = maxQcId == null ? QC_ID_BASE : maxQcId;
        if (base >= QC_ID_MAX_ALLOWED) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR.getCode(), "质控记录ID已达到上限，请联系管理员");
        }
        return base + 1;
    }
}
