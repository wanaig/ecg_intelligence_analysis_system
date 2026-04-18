package com.hnkjzy.ecg_collection.service.impl.monitor;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnkjzy.ecg_collection.common.exception.BusinessException;
import com.hnkjzy.ecg_collection.common.result.ResultCode;
import com.hnkjzy.ecg_collection.mapper.monitor.MonitorDeviceMapper;
import com.hnkjzy.ecg_collection.model.dto.monitor.MonitorDeviceCreateDto;
import com.hnkjzy.ecg_collection.model.dto.monitor.MonitorDevicePageQueryDto;
import com.hnkjzy.ecg_collection.model.dto.monitor.MonitorDeviceUpdateDto;
import com.hnkjzy.ecg_collection.model.entity.monitor.EcgDeviceEntity;
import com.hnkjzy.ecg_collection.model.vo.common.DictOptionVo;
import com.hnkjzy.ecg_collection.model.vo.common.PageResultVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorDeviceBasicInfoVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorDeviceCurrentPatientVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorDeviceDeleteResultVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorDeviceDictVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorDeviceMaintainRecordVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorDevicePageItemVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorDeviceSaveResultVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorDeviceUsageDetailVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorDeviceUsageStatVo;
import com.hnkjzy.ecg_collection.service.impl.BaseServiceImpl;
import com.hnkjzy.ecg_collection.service.monitor.MonitorDeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 实时监护-设备管理服务实现。
 */
@Service
@RequiredArgsConstructor
public class MonitorDeviceServiceImpl extends BaseServiceImpl implements MonitorDeviceService {

    private static final long DEFAULT_PAGE_NUM = 1L;
    private static final long DEFAULT_PAGE_SIZE = 10L;
    private static final long MAX_PAGE_SIZE = 200L;

    private final MonitorDeviceMapper monitorDeviceMapper;

    @Override
    public PageResultVo<MonitorDevicePageItemVo> pageDevices(MonitorDevicePageQueryDto queryDto) {
        MonitorDevicePageQueryDto query = normalizePageQuery(queryDto);
        Integer deviceTypeCode = parseDeviceType(query.getDeviceType(), false, "deviceType");
        LocalDate purchaseStart = parseDate(query.getPurchaseDateStart(), "purchaseDateStart");
        LocalDate purchaseEnd = parseDate(query.getPurchaseDateEnd(), "purchaseDateEnd");

        if (purchaseStart != null && purchaseEnd != null && purchaseStart.isAfter(purchaseEnd)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "purchaseDateStart 不能晚于 purchaseDateEnd");
        }

        long pageNum = normalizePageNum(query.getPageNum());
        long pageSize = normalizePageSize(query.getPageSize());

        Page<MonitorDevicePageItemVo> page = new Page<>(pageNum, pageSize);
        IPage<MonitorDevicePageItemVo> pageData = monitorDeviceMapper.selectDevicePage(page, query, deviceTypeCode, purchaseStart, purchaseEnd);

        List<MonitorDevicePageItemVo> records = pageData.getRecords();
        if (records == null) {
            records = Collections.emptyList();
        }

        return PageResultVo.<MonitorDevicePageItemVo>builder()
                .pageNum(pageData.getCurrent())
                .pageSize(pageData.getSize())
                .total(pageData.getTotal())
                .pages(pageData.getPages())
                .records(records)
                .build();
    }

    @Override
    public MonitorDeviceDictVo getDeviceDicts() {
        MonitorDeviceDictVo dictVo = new MonitorDeviceDictVo();

        List<DictOptionVo> typeOptions = new ArrayList<>();
        typeOptions.add(DictOptionVo.builder().value("").label("全部类型").build());
        typeOptions.add(DictOptionVo.builder().value("1").label("静态心电设备").build());
        typeOptions.add(DictOptionVo.builder().value("2").label("动态心电设备").build());
        typeOptions.add(DictOptionVo.builder().value("3").label("居家监护设备").build());
        typeOptions.add(DictOptionVo.builder().value("4").label("床边监护仪").build());

        List<DictOptionVo> wardOptions = monitorDeviceMapper.selectWardOptions();
        if (wardOptions == null) {
            wardOptions = new ArrayList<>();
        }
        wardOptions.add(0, DictOptionVo.builder().value("").label("全部病区").build());

        dictVo.setDeviceTypeOptions(typeOptions);
        dictVo.setWardOptions(wardOptions);
        return dictVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MonitorDeviceSaveResultVo createDevice(MonitorDeviceCreateDto createDto) {
        if (createDto == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "请求体不能为空");
        }

        EcgDeviceEntity entity = new EcgDeviceEntity();
        entity.setDeviceId(IdWorker.getId());
        entity.setDeviceCode(resolveDeviceCode(createDto.getDeviceCode(), null, true));
        entity.setDeviceName(requireText(createDto.getDeviceName(), "deviceName 参数不合法"));
        entity.setDeviceType(parseDeviceType(createDto.getDeviceType(), true, "deviceType"));
        entity.setDeviceModel(trimToNull(createDto.getDeviceModel()));
        entity.setManufacturer(trimToNull(createDto.getManufacturer()));
        entity.setSupplier(trimToNull(createDto.getSupplier()));
        entity.setInstallDate(parseDate(createDto.getInstallDate(), "installDate"));
        entity.setBindDeptId(createDto.getBindDeptId());
        entity.setBindDeptName(resolveWardName(createDto.getBindDeptId(), createDto.getBindDeptName()));
        entity.setLastMaintainTime(parseDate(createDto.getLastMaintainTime(), "lastMaintainTime"));
        entity.setNextMaintainTime(parseDate(createDto.getNextMaintainTime(), "nextMaintainTime"));
        entity.setDeviceStatus(parseDeviceStatus(createDto.getDeviceStatus(), false));
        entity.setIsDeleted(0);

        validateMaintainDate(entity.getLastMaintainTime(), entity.getNextMaintainTime());
        monitorDeviceMapper.insertDevice(entity);
        return monitorDeviceMapper.selectDeviceSaveResult(entity.getDeviceId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MonitorDeviceSaveResultVo updateDevice(MonitorDeviceUpdateDto updateDto) {
        if (updateDto == null || updateDto.getDeviceId() == null || updateDto.getDeviceId() <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "deviceId 参数不合法");
        }

        MonitorDeviceSaveResultVo existing = monitorDeviceMapper.selectDeviceSaveResult(updateDto.getDeviceId());
        if (existing == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "设备不存在");
        }

        EcgDeviceEntity entity = new EcgDeviceEntity();
        entity.setDeviceId(updateDto.getDeviceId());
        String finalDeviceCode = trimToNull(updateDto.getDeviceCode());
        if (!StringUtils.hasText(finalDeviceCode)) {
            finalDeviceCode = existing.getDeviceCode();
        }
        entity.setDeviceCode(resolveDeviceCode(finalDeviceCode, updateDto.getDeviceId(), false));
        entity.setDeviceName(requireText(updateDto.getDeviceName(), "deviceName 参数不合法"));
        entity.setDeviceType(parseDeviceType(updateDto.getDeviceType(), true, "deviceType"));
        entity.setDeviceModel(trimToNull(updateDto.getDeviceModel()));
        entity.setManufacturer(trimToNull(updateDto.getManufacturer()));
        entity.setSupplier(trimToNull(updateDto.getSupplier()));
        entity.setInstallDate(parseDate(updateDto.getInstallDate(), "installDate"));
        entity.setBindDeptId(updateDto.getBindDeptId());
        entity.setBindDeptName(resolveWardName(updateDto.getBindDeptId(), updateDto.getBindDeptName()));
        entity.setLastMaintainTime(parseDate(updateDto.getLastMaintainTime(), "lastMaintainTime"));
        entity.setNextMaintainTime(parseDate(updateDto.getNextMaintainTime(), "nextMaintainTime"));
        Integer finalDeviceStatus = updateDto.getDeviceStatus() == null ? existing.getDeviceStatus() : updateDto.getDeviceStatus();
        entity.setDeviceStatus(parseDeviceStatus(finalDeviceStatus, true));

        validateMaintainDate(entity.getLastMaintainTime(), entity.getNextMaintainTime());
        monitorDeviceMapper.updateDeviceById(entity);
        return monitorDeviceMapper.selectDeviceSaveResult(updateDto.getDeviceId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MonitorDeviceDeleteResultVo deleteDevice(Long deviceId) {
        if (deviceId == null || deviceId <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "deviceId 参数不合法");
        }

        MonitorDeviceSaveResultVo existing = monitorDeviceMapper.selectDeviceSaveResult(deviceId);
        if (existing == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "设备不存在");
        }

        Integer activeBinding = monitorDeviceMapper.countActiveMonitorBinding(deviceId);
        if (activeBinding != null && activeBinding > 0) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR.getCode(), "设备已绑定监护中患者，禁止删除");
        }

        monitorDeviceMapper.logicalDeleteDevice(deviceId);
        MonitorDeviceDeleteResultVo resultVo = new MonitorDeviceDeleteResultVo();
        resultVo.setDeviceId(deviceId);
        resultVo.setDeleted(true);
        return resultVo;
    }

    @Override
    public MonitorDeviceUsageDetailVo getDeviceUsageDetail(Long deviceId) {
        if (deviceId == null || deviceId <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "deviceId 参数不合法");
        }

        MonitorDeviceBasicInfoVo basicInfo = monitorDeviceMapper.selectDeviceBasicInfo(deviceId);
        if (basicInfo == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "设备不存在");
        }

        MonitorDeviceUsageStatVo usageStat = monitorDeviceMapper.selectDeviceUsageStat(deviceId);
        if (usageStat == null) {
            usageStat = new MonitorDeviceUsageStatVo();
        }
        usageStat.setTodayMeasureCount(defaultLong(usageStat.getTodayMeasureCount()));
        usageStat.setWeekMeasureCount(defaultLong(usageStat.getWeekMeasureCount()));
        usageStat.setMonthMeasureCount(defaultLong(usageStat.getMonthMeasureCount()));
        usageStat.setOnlineRate(defaultDecimal(usageStat.getOnlineRate()));
        usageStat.setErrorRate(defaultDecimal(usageStat.getErrorRate()));

        List<String> deptTags = monitorDeviceMapper.selectDeviceDeptTags(deviceId);
        List<MonitorDeviceCurrentPatientVo> currentPatients = monitorDeviceMapper.selectCurrentPatients(deviceId);

        MonitorDeviceUsageDetailVo detailVo = new MonitorDeviceUsageDetailVo();
        detailVo.setBasicInfo(basicInfo);
        detailVo.setUsageStat(usageStat);
        detailVo.setDeptTags(deptTags == null ? Collections.emptyList() : deptTags);
        detailVo.setCurrentPatients(currentPatients == null ? Collections.emptyList() : currentPatients);
        return detailVo;
    }

    @Override
    public PageResultVo<MonitorDeviceMaintainRecordVo> pageMaintainRecords(Long deviceId, Long pageNum, Long pageSize) {
        if (deviceId == null || deviceId <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "deviceId 参数不合法");
        }

        MonitorDeviceSaveResultVo existing = monitorDeviceMapper.selectDeviceSaveResult(deviceId);
        if (existing == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "设备不存在");
        }

        long finalPageNum = normalizePageNum(pageNum);
        long finalPageSize = normalizePageSize(pageSize);

        Page<MonitorDeviceMaintainRecordVo> page = new Page<>(finalPageNum, finalPageSize);
        IPage<MonitorDeviceMaintainRecordVo> pageData = monitorDeviceMapper.selectMaintainRecordPage(page, deviceId);

        List<MonitorDeviceMaintainRecordVo> records = pageData.getRecords();
        if (records == null) {
            records = Collections.emptyList();
        }

        return PageResultVo.<MonitorDeviceMaintainRecordVo>builder()
                .pageNum(pageData.getCurrent())
                .pageSize(pageData.getSize())
                .total(pageData.getTotal())
                .pages(pageData.getPages())
                .records(records)
                .build();
    }

    private MonitorDevicePageQueryDto normalizePageQuery(MonitorDevicePageQueryDto queryDto) {
        MonitorDevicePageQueryDto query = queryDto == null ? new MonitorDevicePageQueryDto() : queryDto;
        query.setDeviceName(trimToNull(query.getDeviceName()));
        query.setDeviceType(trimToNull(query.getDeviceType()));
        query.setWard(trimToNull(query.getWard()));
        query.setPurchaseDateStart(trimToNull(query.getPurchaseDateStart()));
        query.setPurchaseDateEnd(trimToNull(query.getPurchaseDateEnd()));
        return query;
    }

    private Integer parseDeviceType(String deviceType, boolean required, String fieldName) {
        if (!StringUtils.hasText(deviceType)) {
            if (required) {
                throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), fieldName + " 参数不合法");
            }
            return null;
        }

        String value = deviceType.trim();
        if ("全部类型".equals(value)) {
            return null;
        }
        switch (value) {
            case "1":
            case "静态心电设备":
                return 1;
            case "2":
            case "动态心电设备":
                return 2;
            case "3":
            case "居家监护设备":
                return 3;
            case "4":
            case "床边监护仪":
                return 4;
            default:
                throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), fieldName + " 参数不合法");
        }
    }

    private Integer parseDeviceType(Integer deviceType, boolean required, String fieldName) {
        if (deviceType == null) {
            if (required) {
                throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), fieldName + " 参数不合法");
            }
            return null;
        }
        if (deviceType < 1 || deviceType > 4) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), fieldName + " 参数不合法");
        }
        return deviceType;
    }

    private Integer parseDeviceStatus(Integer deviceStatus, boolean required) {
        if (deviceStatus == null) {
            if (required) {
                throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "deviceStatus 参数不合法");
            }
            return 1;
        }
        if (deviceStatus < 1 || deviceStatus > 4) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "deviceStatus 参数不合法");
        }
        return deviceStatus;
    }

    private LocalDate parseDate(String value, String fieldName) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return LocalDate.parse(value.trim(), DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException ex) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), fieldName + " 参数格式错误");
        }
    }

    private String resolveDeviceCode(String inputCode, Long excludeDeviceId, boolean autoGenerateWhenBlank) {
        String code = trimToNull(inputCode);
        if (code == null && autoGenerateWhenBlank) {
            code = "DEV-AUTO-" + IdWorker.getId();
        }
        if (code == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "deviceCode 参数不合法");
        }
        Long count = monitorDeviceMapper.countDeviceCode(code, excludeDeviceId);
        if (count != null && count > 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "deviceCode 已存在");
        }
        return code;
    }

    private String resolveWardName(Long wardId, String wardName) {
        String fallbackName = trimToNull(wardName);
        if (wardId == null) {
            return fallbackName;
        }
        String dbWardName = monitorDeviceMapper.selectWardNameById(wardId);
        if (!StringUtils.hasText(dbWardName)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "bindDeptId 参数不合法");
        }
        return dbWardName;
    }

    private String requireText(String value, String errorMessage) {
        String text = trimToNull(value);
        if (!StringUtils.hasText(text)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), errorMessage);
        }
        return text;
    }

    private void validateMaintainDate(LocalDate lastMaintainTime, LocalDate nextMaintainTime) {
        if (lastMaintainTime != null && nextMaintainTime != null && lastMaintainTime.isAfter(nextMaintainTime)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "lastMaintainTime 不能晚于 nextMaintainTime");
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
}
