package com.hnkjzy.ecg_collection.service.impl.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnkjzy.ecg_collection.common.exception.BusinessException;
import com.hnkjzy.ecg_collection.common.result.ResultCode;
import com.hnkjzy.ecg_collection.mapper.system.SystemDepartmentMapper;
import com.hnkjzy.ecg_collection.model.dto.system.SystemDepartmentCreateDto;
import com.hnkjzy.ecg_collection.model.dto.system.SystemDepartmentPageQueryDto;
import com.hnkjzy.ecg_collection.model.dto.system.SystemDepartmentStatusUpdateDto;
import com.hnkjzy.ecg_collection.model.dto.system.SystemDepartmentUpdateDto;
import com.hnkjzy.ecg_collection.model.entity.system.SysDepartmentEntity;
import com.hnkjzy.ecg_collection.model.vo.common.DictOptionVo;
import com.hnkjzy.ecg_collection.model.vo.common.PageResultVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemDepartmentDeleteResultVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemDepartmentDetailVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemDepartmentDictVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemDepartmentPageItemVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemDepartmentSaveResultVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemDepartmentStatusUpdateResultVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemDepartmentTreeItemVo;
import com.hnkjzy.ecg_collection.service.impl.BaseServiceImpl;
import com.hnkjzy.ecg_collection.service.system.SystemDepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class SystemDepartmentServiceImpl extends BaseServiceImpl implements SystemDepartmentService {

    private static final long DEFAULT_PAGE_NUM = 1L;
    private static final long DEFAULT_PAGE_SIZE = 10L;
    private static final long MAX_PAGE_SIZE = 200L;
    private static final long DEPARTMENT_ID_BASE = 1200L;
    private static final long DEPARTMENT_ID_MAX_ALLOWED = 9_999_999_999L;

    private static final Pattern PHONE_PATTERN = Pattern.compile("^1\\d{10}$");

    private final SystemDepartmentMapper systemDepartmentMapper;

    @Override
    public PageResultVo<SystemDepartmentPageItemVo> pageDepartments(SystemDepartmentPageQueryDto queryDto) {
        SystemDepartmentPageQueryDto query = normalizePageQuery(queryDto);
        if (query.getParentDeptId() != null && query.getParentDeptId() <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "parentDeptId 参数不合法");
        }

        Integer statusCode = parseStatusText(query.getStatus(), false, "status");
        long pageNum = normalizePageNum(query.getPageNum());
        long pageSize = normalizePageSize(query.getPageSize());

        Page<SystemDepartmentPageItemVo> page = new Page<>(pageNum, pageSize);
        IPage<SystemDepartmentPageItemVo> pageData = systemDepartmentMapper.selectDepartmentPage(page, query, statusCode);

        List<SystemDepartmentPageItemVo> records = pageData.getRecords();
        if (records == null) {
            records = Collections.emptyList();
        }
        for (SystemDepartmentPageItemVo item : records) {
            item.setDeptName(trimToNull(item.getDeptName()));
            item.setParentDeptName(trimToNull(item.getParentDeptName()));
            item.setDeptDirector(trimToNull(item.getDeptDirector()));
            item.setContactPhone(trimToNull(item.getContactPhone()));
            item.setDirector(trimToNull(item.getDirector()));
            item.setPhone(trimToNull(item.getPhone()));
            item.setLocation(trimToNull(item.getLocation()));
            item.setStatus(item.getStatus() == null ? 1 : item.getStatus());
            if (!StringUtils.hasText(item.getStatusText())) {
                item.setStatusText(statusToText(item.getStatus()));
            }
            item.setDeptLevel(item.getDeptLevel() == null ? 1 : item.getDeptLevel());
            item.setDeptType(item.getDeptType() == null ? item.getDeptLevel() : item.getDeptType());
            if (!StringUtils.hasText(item.getDeptLevelText())) {
                item.setDeptLevelText(deptLevelToText(item.getDeptLevel()));
            }
            item.setDeptTypeText(StringUtils.hasText(item.getDeptTypeText())
                    ? item.getDeptTypeText()
                    : item.getDeptLevelText());
            if (item.getDeptLevel() == null && item.getDeptType() != null) {
                item.setDeptLevel(item.getDeptType());
            }
            if (!StringUtils.hasText(item.getDeptLevelText())) {
                item.setDeptLevelText(item.getDeptTypeText());
            }
            if (!StringUtils.hasText(item.getDirector())) {
                item.setDirector(item.getDeptDirector());
            }
            if (!StringUtils.hasText(item.getPhone())) {
                item.setPhone(item.getContactPhone());
            }
            if (!StringUtils.hasText(item.getDeptDirector())) {
                item.setDeptDirector(item.getDirector());
            }
            if (!StringUtils.hasText(item.getContactPhone())) {
                item.setContactPhone(item.getPhone());
            }
        }

        return PageResultVo.<SystemDepartmentPageItemVo>builder()
                .pageNum(pageData.getCurrent())
                .pageSize(pageData.getSize())
                .total(pageData.getTotal())
                .pages(pageData.getPages())
                .records(records)
                .build();
    }

    @Override
    public List<SystemDepartmentTreeItemVo> getParentDepartmentTree(Long deptId) {
        if (deptId != null && deptId <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "deptId 参数不合法");
        }
        List<SystemDepartmentTreeItemVo> flatNodes = systemDepartmentMapper.selectDepartmentTreeAll();
        return buildDepartmentTree(flatNodes, deptId);
    }

    @Override
    public SystemDepartmentDictVo getDepartmentDicts() {
        List<SystemDepartmentTreeItemVo> roots = buildDepartmentTree(systemDepartmentMapper.selectDepartmentTreeList(), null);

        SystemDepartmentDictVo dictVo = new SystemDepartmentDictVo();
        dictVo.setParentDeptTree(roots);
        dictVo.setDeptLevelOptions(buildDeptLevelOptions());
        dictVo.setStatusOptions(buildStatusOptions());
        return dictVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SystemDepartmentSaveResultVo createDepartment(SystemDepartmentCreateDto createDto) {
        if (createDto == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "请求体不能为空");
        }

        String deptName = requireDepartmentName(createDto.getDeptName());
        Long parentDeptId = normalizeParentDeptId(createDto.getParentId());
        String parentDeptName = parentDeptId == null ? null : requireDepartmentNameById(parentDeptId);
        Integer deptLevel = parseDeptLevel(createDto.getDeptType(), "deptType");
        Integer status = createDto.getStatus() == null ? 1 : parseStatusCode(createDto.getStatus(), "status");

        String deptDirector = normalizeDirector(createDto.getDirector());
        String contactPhone = normalizeContactPhone(createDto.getPhone());
        String location = normalizeLocation(createDto.getLocation());

        checkDepartmentNameDuplicate(deptName, parentDeptId, null);

        Long deptId = nextDepartmentId();
        SysDepartmentEntity entity = new SysDepartmentEntity();
        entity.setDeptId(deptId);
        entity.setParentId(parentDeptId);
        entity.setParentName(parentDeptName);
        entity.setDeptName(deptName);
        entity.setDeptCode(buildDeptCode(deptId, location));
        entity.setDeptType(deptLevel);
        entity.setSort(nextSort(parentDeptId));
        entity.setStatus(status);
        entity.setIsDeleted(0);

        systemDepartmentMapper.insertDepartment(entity);

        if (StringUtils.hasText(deptDirector) || StringUtils.hasText(contactPhone)) {
            systemDepartmentMapper.updateDirectorUserByDeptId(deptId, deptDirector, contactPhone);
        }

        return normalizeSaveResult(systemDepartmentMapper.selectDepartmentSaveResult(deptId));
    }

    @Override
    public SystemDepartmentDetailVo getDepartmentDetail(Long deptId) {
        if (deptId == null || deptId <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "deptId 参数不合法");
        }

        SystemDepartmentDetailVo detailVo = systemDepartmentMapper.selectDepartmentDetail(deptId);
        if (detailVo == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "科室不存在");
        }
        return normalizeDetail(detailVo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SystemDepartmentSaveResultVo updateDepartment(SystemDepartmentUpdateDto updateDto) {
        if (updateDto == null || updateDto.getDeptId() == null || updateDto.getDeptId() <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "deptId 参数不合法");
        }

        SystemDepartmentDetailVo existing = getDepartmentDetail(updateDto.getDeptId());

        String finalDeptName = updateDto.getDeptName() == null
                ? existing.getDeptName()
                : requireDepartmentName(updateDto.getDeptName());

        Long finalParentDeptId = updateDto.getParentId() == null
                ? existing.getParentDeptId()
            : normalizeParentDeptId(updateDto.getParentId());

        if (finalParentDeptId != null && finalParentDeptId.equals(updateDto.getDeptId())) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "parentId 参数不合法");
        }
        validateParentCircular(updateDto.getDeptId(), finalParentDeptId);
        String finalParentDeptName = finalParentDeptId == null ? null : requireDepartmentNameById(finalParentDeptId);

        Integer existingDeptLevel = existing.getDeptLevel() == null ? 1 : existing.getDeptLevel();
        Integer finalDeptLevel = updateDto.getDeptType() == null
                ? existingDeptLevel
            : parseDeptLevel(updateDto.getDeptType(), "deptType");

        Integer existingStatus = existing.getStatus() == null ? 1 : existing.getStatus();
        Integer finalStatus = updateDto.getStatus() == null
                ? existingStatus
                : parseStatusCode(updateDto.getStatus(), "status");

        String location = updateDto.getLocation() == null
                ? existing.getLocation()
                : normalizeLocation(updateDto.getLocation());

        String deptDirector = updateDto.getDirector() == null ? null : normalizeDirector(updateDto.getDirector());
        String contactPhone = updateDto.getPhone() == null ? null : normalizeContactPhone(updateDto.getPhone());

        if (!finalDeptName.equals(existing.getDeptName())
                || !isSameLong(finalParentDeptId, existing.getParentDeptId())) {
            checkDepartmentNameDuplicate(finalDeptName, finalParentDeptId, updateDto.getDeptId());
        }

        String existingCode = systemDepartmentMapper.selectDepartmentCodeById(updateDto.getDeptId());
        String finalDeptCode = updateDto.getLocation() == null ? existingCode : buildDeptCode(updateDto.getDeptId(), location);
        if (!StringUtils.hasText(finalDeptCode)) {
            finalDeptCode = buildDeptCode(updateDto.getDeptId(), location);
        }

        SysDepartmentEntity entity = new SysDepartmentEntity();
        entity.setDeptId(updateDto.getDeptId());
        entity.setParentId(finalParentDeptId);
        entity.setParentName(finalParentDeptName);
        entity.setDeptName(finalDeptName);
        entity.setDeptCode(finalDeptCode);
        entity.setDeptType(finalDeptLevel);
        entity.setStatus(finalStatus);

        systemDepartmentMapper.updateDepartmentById(entity);

        if (StringUtils.hasText(deptDirector) || StringUtils.hasText(contactPhone)) {
            systemDepartmentMapper.updateDirectorUserByDeptId(updateDto.getDeptId(), deptDirector, contactPhone);
        }

        return normalizeSaveResult(systemDepartmentMapper.selectDepartmentSaveResult(updateDto.getDeptId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SystemDepartmentDeleteResultVo deleteDepartment(Long deptId) {
        getDepartmentDetail(deptId);

        long childCount = defaultLong(systemDepartmentMapper.countChildDepartments(deptId));
        if (childCount > 0) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR.getCode(), "存在下级子科室，禁止直接删除");
        }

        long boundBedCount = defaultLong(systemDepartmentMapper.countBedsByDepartmentId(deptId));
        long boundDeviceCount = defaultLong(systemDepartmentMapper.countDevicesByDepartmentId(deptId));

        if (boundBedCount > 0 || boundDeviceCount > 0) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR.getCode(), "科室已绑定床位或设备，禁止删除");
        }

        systemDepartmentMapper.logicalDeleteDepartment(deptId);

        SystemDepartmentDeleteResultVo resultVo = new SystemDepartmentDeleteResultVo();
        resultVo.setDeptId(deptId);
        resultVo.setDeleted(true);
        resultVo.setChildCount(childCount);
        resultVo.setBoundUserCount(0L);
        resultVo.setBoundBedCount(boundBedCount);
        resultVo.setBoundDeviceCount(boundDeviceCount);
        resultVo.setForcedDelete(false);
        return resultVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SystemDepartmentStatusUpdateResultVo updateDepartmentStatus(SystemDepartmentStatusUpdateDto statusUpdateDto) {
        if (statusUpdateDto == null || statusUpdateDto.getDeptId() == null || statusUpdateDto.getDeptId() <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "deptId 参数不合法");
        }

        Integer finalStatus = parseStatusCode(statusUpdateDto.getStatus(), "status");
        getDepartmentDetail(statusUpdateDto.getDeptId());

        systemDepartmentMapper.updateDepartmentStatusById(statusUpdateDto.getDeptId(), finalStatus);

        SystemDepartmentStatusUpdateResultVo resultVo = new SystemDepartmentStatusUpdateResultVo();
        resultVo.setDeptId(statusUpdateDto.getDeptId());
        resultVo.setStatus(finalStatus);
        resultVo.setStatusText(statusToText(finalStatus));
        return resultVo;
    }

    private SystemDepartmentPageQueryDto normalizePageQuery(SystemDepartmentPageQueryDto queryDto) {
        SystemDepartmentPageQueryDto query = queryDto == null ? new SystemDepartmentPageQueryDto() : queryDto;
        query.setKeyword(trimToNull(query.getKeyword()));
        query.setStatus(trimToNull(query.getStatus()));
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
        return Math.min(pageSize, MAX_PAGE_SIZE);
    }

    private String requireDepartmentName(String deptName) {
        String value = trimToNull(deptName);
        if (!StringUtils.hasText(value) || value.length() > 64) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "deptName 参数不合法");
        }
        return value;
    }

    private Long normalizeParentDeptId(Long parentDeptId) {
        if (parentDeptId == null) {
            return null;
        }
        if (parentDeptId <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "parentId 参数不合法");
        }
        return parentDeptId;
    }

    private String requireDepartmentNameById(Long deptId) {
        String deptName = systemDepartmentMapper.selectDepartmentNameById(deptId);
        if (!StringUtils.hasText(deptName)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "parentId 对应科室不存在");
        }
        return deptName;
    }

    private void checkDepartmentNameDuplicate(String deptName, Long parentDeptId, Long excludeDeptId) {
        Long duplicate = systemDepartmentMapper.countDepartmentName(deptName, parentDeptId, excludeDeptId);
        if (duplicate != null && duplicate > 0) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR.getCode(), "deptName 已存在");
        }
    }

    private Integer parseDeptLevel(Integer deptLevel, String fieldName) {
        if (deptLevel == null || (deptLevel != 1 && deptLevel != 2 && deptLevel != 3)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), fieldName + " 参数不合法");
        }
        return deptLevel;
    }

    private Integer parseStatusText(String status, boolean required, String fieldName) {
        if (!StringUtils.hasText(status)) {
            if (required) {
                throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), fieldName + " 参数不合法");
            }
            return null;
        }

        String value = status.trim();
        if ("1".equals(value) || "启用".equals(value) || "正常".equals(value) || "ENABLED".equalsIgnoreCase(value)) {
            return 1;
        }
        if ("0".equals(value) || "禁用".equals(value) || "DISABLED".equalsIgnoreCase(value)) {
            return 0;
        }
        throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), fieldName + " 参数不合法");
    }

    private Integer parseStatusCode(Integer status, String fieldName) {
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), fieldName + " 参数不合法");
        }
        return status;
    }

    private String normalizeDirector(String deptDirector) {
        String value = trimToNull(deptDirector);
        if (value == null) {
            return null;
        }
        if (value.length() > 32) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "director 参数不合法");
        }
        return value;
    }

    private String normalizeContactPhone(String contactPhone) {
        String value = trimToNull(contactPhone);
        if (value == null) {
            return null;
        }
        if (!PHONE_PATTERN.matcher(value).matches()) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "phone 参数不合法");
        }
        return value;
    }

    private String normalizeLocation(String location) {
        String value = trimToNull(location);
        if (value == null) {
            return null;
        }
        if (value.length() > 64) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "location 参数不合法");
        }
        return value;
    }

    private String buildDeptCode(Long deptId, String location) {
        String base = "DEPT_" + deptId;
        String locationValue = trimToNull(location);
        if (!StringUtils.hasText(locationValue)) {
            return base;
        }

        String cleaned = locationValue.replaceAll("\\s+", "").replaceAll("[^0-9A-Za-z\\u4e00-\\u9fa5_-]", "");
        if (!StringUtils.hasText(cleaned)) {
            return base;
        }

        int remaining = 32 - base.length() - 1;
        if (remaining <= 0) {
            return base;
        }
        if (cleaned.length() > remaining) {
            cleaned = cleaned.substring(0, remaining);
        }
        return base + "_" + cleaned;
    }

    private int nextSort(Long parentDeptId) {
        Long maxSort = systemDepartmentMapper.selectMaxSortByParent(parentDeptId);
        if (maxSort == null) {
            return 1;
        }
        return maxSort.intValue() + 1;
    }

    private Long nextDepartmentId() {
        Long maxDeptId = systemDepartmentMapper.selectMaxDepartmentIdInRangeForUpdate(DEPARTMENT_ID_MAX_ALLOWED);
        long base = maxDeptId == null ? DEPARTMENT_ID_BASE : maxDeptId;
        if (base >= DEPARTMENT_ID_MAX_ALLOWED) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR.getCode(), "科室ID已达到上限，请联系管理员");
        }
        return base + 1;
    }

    private List<DictOptionVo> buildDeptLevelOptions() {
        List<DictOptionVo> options = new ArrayList<>();
        options.add(DictOptionVo.builder().value("").label("全部级别").build());
        options.add(DictOptionVo.builder().value("1").label("科室").build());
        options.add(DictOptionVo.builder().value("2").label("病区").build());
        options.add(DictOptionVo.builder().value("3").label("居家站点").build());
        return options;
    }

    private List<DictOptionVo> buildStatusOptions() {
        List<DictOptionVo> options = new ArrayList<>();
        options.add(DictOptionVo.builder().value("").label("全部状态").build());
        options.add(DictOptionVo.builder().value("1").label("启用").build());
        options.add(DictOptionVo.builder().value("0").label("禁用").build());
        return options;
    }

    private String statusToText(Integer status) {
        if (status == null) {
            return "未知";
        }
        if (status == 1) {
            return "启用";
        }
        if (status == 0) {
            return "禁用";
        }
        return "未知";
    }

    private String deptLevelToText(Integer deptLevel) {
        if (deptLevel == null) {
            return "未知";
        }
        if (deptLevel == 1) {
            return "科室";
        }
        if (deptLevel == 2) {
            return "病区";
        }
        if (deptLevel == 3) {
            return "居家站点";
        }
        return "未知";
    }

    private SystemDepartmentDetailVo normalizeDetail(SystemDepartmentDetailVo detailVo) {
        detailVo.setDeptName(trimToNull(detailVo.getDeptName()));
        detailVo.setParentDeptName(trimToNull(detailVo.getParentDeptName()));
        detailVo.setDeptDirector(trimToNull(detailVo.getDeptDirector()));
        detailVo.setContactPhone(trimToNull(detailVo.getContactPhone()));
        detailVo.setDirector(trimToNull(detailVo.getDirector()));
        detailVo.setPhone(trimToNull(detailVo.getPhone()));
        detailVo.setLocation(trimToNull(detailVo.getLocation()));
        detailVo.setStatus(detailVo.getStatus() == null ? 1 : detailVo.getStatus());
        detailVo.setDeptLevel(detailVo.getDeptLevel() == null ? 1 : detailVo.getDeptLevel());
        detailVo.setDeptType(detailVo.getDeptType() == null ? detailVo.getDeptLevel() : detailVo.getDeptType());
        if (!StringUtils.hasText(detailVo.getStatusText())) {
            detailVo.setStatusText(statusToText(detailVo.getStatus()));
        }
        if (!StringUtils.hasText(detailVo.getDeptLevelText())) {
            detailVo.setDeptLevelText(deptLevelToText(detailVo.getDeptLevel()));
        }
        detailVo.setDeptTypeText(StringUtils.hasText(detailVo.getDeptTypeText())
                ? detailVo.getDeptTypeText()
                : detailVo.getDeptLevelText());
        if (!StringUtils.hasText(detailVo.getDirector())) {
            detailVo.setDirector(detailVo.getDeptDirector());
        }
        if (!StringUtils.hasText(detailVo.getPhone())) {
            detailVo.setPhone(detailVo.getContactPhone());
        }
        if (!StringUtils.hasText(detailVo.getDeptDirector())) {
            detailVo.setDeptDirector(detailVo.getDirector());
        }
        if (!StringUtils.hasText(detailVo.getContactPhone())) {
            detailVo.setContactPhone(detailVo.getPhone());
        }
        if (detailVo.getDeptLevel() == null && detailVo.getDeptType() != null) {
            detailVo.setDeptLevel(detailVo.getDeptType());
        }
        if (!StringUtils.hasText(detailVo.getDeptLevelText())) {
            detailVo.setDeptLevelText(detailVo.getDeptTypeText());
        }
        return detailVo;
    }

    private SystemDepartmentSaveResultVo normalizeSaveResult(SystemDepartmentSaveResultVo resultVo) {
        if (resultVo == null) {
            return null;
        }
        resultVo.setDeptName(trimToNull(resultVo.getDeptName()));
        resultVo.setParentDeptName(trimToNull(resultVo.getParentDeptName()));
        resultVo.setDeptDirector(trimToNull(resultVo.getDeptDirector()));
        resultVo.setContactPhone(trimToNull(resultVo.getContactPhone()));
        resultVo.setDirector(trimToNull(resultVo.getDirector()));
        resultVo.setPhone(trimToNull(resultVo.getPhone()));
        resultVo.setLocation(trimToNull(resultVo.getLocation()));
        resultVo.setStatus(resultVo.getStatus() == null ? 1 : resultVo.getStatus());
        resultVo.setDeptLevel(resultVo.getDeptLevel() == null ? 1 : resultVo.getDeptLevel());
        resultVo.setDeptType(resultVo.getDeptType() == null ? resultVo.getDeptLevel() : resultVo.getDeptType());
        if (!StringUtils.hasText(resultVo.getStatusText())) {
            resultVo.setStatusText(statusToText(resultVo.getStatus()));
        }
        if (!StringUtils.hasText(resultVo.getDeptLevelText())) {
            resultVo.setDeptLevelText(deptLevelToText(resultVo.getDeptLevel()));
        }
        resultVo.setDeptTypeText(StringUtils.hasText(resultVo.getDeptTypeText())
                ? resultVo.getDeptTypeText()
                : resultVo.getDeptLevelText());
        if (!StringUtils.hasText(resultVo.getDirector())) {
            resultVo.setDirector(resultVo.getDeptDirector());
        }
        if (!StringUtils.hasText(resultVo.getPhone())) {
            resultVo.setPhone(resultVo.getContactPhone());
        }
        if (!StringUtils.hasText(resultVo.getDeptDirector())) {
            resultVo.setDeptDirector(resultVo.getDirector());
        }
        if (!StringUtils.hasText(resultVo.getContactPhone())) {
            resultVo.setContactPhone(resultVo.getPhone());
        }
        if (resultVo.getDeptLevel() == null && resultVo.getDeptType() != null) {
            resultVo.setDeptLevel(resultVo.getDeptType());
        }
        if (!StringUtils.hasText(resultVo.getDeptLevelText())) {
            resultVo.setDeptLevelText(resultVo.getDeptTypeText());
        }
        return resultVo;
    }

    private List<SystemDepartmentTreeItemVo> buildDepartmentTree(List<SystemDepartmentTreeItemVo> flatNodes, Long excludeDeptId) {
        if (flatNodes == null) {
            flatNodes = Collections.emptyList();
        }

        Map<Long, SystemDepartmentTreeItemVo> nodeMap = new LinkedHashMap<>();
        for (SystemDepartmentTreeItemVo node : flatNodes) {
            node.setDeptName(trimToNull(node.getDeptName()));
            node.setChildren(new ArrayList<>());
            if (node.getDeptId() != null) {
                nodeMap.put(node.getDeptId(), node);
            }
        }

        List<SystemDepartmentTreeItemVo> roots = new ArrayList<>();
        for (SystemDepartmentTreeItemVo node : flatNodes) {
            Long parentDeptId = node.getParentDeptId();
            if (parentDeptId == null || !nodeMap.containsKey(parentDeptId)) {
                roots.add(node);
                continue;
            }
            nodeMap.get(parentDeptId).getChildren().add(node);
        }

        if (excludeDeptId == null) {
            return roots;
        }

        Set<Long> excludeIds = new HashSet<>();
        collectChildrenIds(nodeMap.get(excludeDeptId), excludeIds);
        return filterDepartmentTree(roots, excludeIds);
    }

    private void collectChildrenIds(SystemDepartmentTreeItemVo node, Set<Long> excludeIds) {
        if (node == null || node.getDeptId() == null || excludeIds.contains(node.getDeptId())) {
            return;
        }
        excludeIds.add(node.getDeptId());
        if (node.getChildren() == null || node.getChildren().isEmpty()) {
            return;
        }
        for (SystemDepartmentTreeItemVo child : node.getChildren()) {
            collectChildrenIds(child, excludeIds);
        }
    }

    private List<SystemDepartmentTreeItemVo> filterDepartmentTree(List<SystemDepartmentTreeItemVo> nodes, Set<Long> excludeIds) {
        if (nodes == null || nodes.isEmpty()) {
            return Collections.emptyList();
        }

        List<SystemDepartmentTreeItemVo> filtered = new ArrayList<>();
        for (SystemDepartmentTreeItemVo node : nodes) {
            if (node.getDeptId() != null && excludeIds.contains(node.getDeptId())) {
                continue;
            }

            SystemDepartmentTreeItemVo copy = new SystemDepartmentTreeItemVo();
            copy.setDeptId(node.getDeptId());
            copy.setParentDeptId(node.getParentDeptId());
            copy.setDeptName(node.getDeptName());
            copy.setChildren(filterDepartmentTree(node.getChildren(), excludeIds));
            filtered.add(copy);
        }
        return filtered;
    }

    private void validateParentCircular(Long deptId, Long parentDeptId) {
        if (deptId == null || parentDeptId == null) {
            return;
        }

        List<SystemDepartmentTreeItemVo> flatNodes = systemDepartmentMapper.selectDepartmentTreeAll();
        Map<Long, Long> parentMap = new HashMap<>();
        for (SystemDepartmentTreeItemVo node : flatNodes) {
            if (node.getDeptId() != null) {
                parentMap.put(node.getDeptId(), node.getParentDeptId());
            }
        }

        Set<Long> visited = new HashSet<>();
        Long current = parentDeptId;
        while (current != null && visited.add(current)) {
            if (current.equals(deptId)) {
                throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "parentId 参数不合法");
            }
            current = parentMap.get(current);
        }
    }

    private boolean isSameLong(Long left, Long right) {
        if (left == null && right == null) {
            return true;
        }
        if (left == null || right == null) {
            return false;
        }
        return left.longValue() == right.longValue();
    }

    private long defaultLong(Long value) {
        return value == null ? 0L : value;
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}
