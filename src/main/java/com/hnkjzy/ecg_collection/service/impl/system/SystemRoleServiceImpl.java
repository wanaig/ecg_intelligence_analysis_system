package com.hnkjzy.ecg_collection.service.impl.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnkjzy.ecg_collection.common.exception.BusinessException;
import com.hnkjzy.ecg_collection.common.result.ResultCode;
import com.hnkjzy.ecg_collection.mapper.system.SystemRoleMapper;
import com.hnkjzy.ecg_collection.model.dto.system.SystemRoleCreateDto;
import com.hnkjzy.ecg_collection.model.dto.system.SystemRolePageQueryDto;
import com.hnkjzy.ecg_collection.model.dto.system.SystemRolePermissionSaveDto;
import com.hnkjzy.ecg_collection.model.dto.system.SystemRoleUpdateDto;
import com.hnkjzy.ecg_collection.model.entity.system.SysRoleEntity;
import com.hnkjzy.ecg_collection.model.entity.system.SysRolePermissionEntity;
import com.hnkjzy.ecg_collection.model.vo.common.PageResultVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemRoleDeleteResultVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemRoleDetailVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemRolePageItemVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemRolePermissionDetailVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemRolePermissionItemVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemRolePermissionSaveResultVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemRoleSaveResultVo;
import com.hnkjzy.ecg_collection.service.impl.BaseServiceImpl;
import com.hnkjzy.ecg_collection.service.system.SystemRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * System role service implementation.
 */
@Service
@RequiredArgsConstructor
public class SystemRoleServiceImpl extends BaseServiceImpl implements SystemRoleService {

    private static final long DEFAULT_PAGE_NUM = 1L;
    private static final long DEFAULT_PAGE_SIZE = 10L;
    private static final long MAX_PAGE_SIZE = 200L;

    private static final Long SUPER_ADMIN_ROLE_ID = 1001L;
    private static final String SUPER_ADMIN_ROLE_NAME_CN = "\u7cfb\u7edf\u7ba1\u7406\u5458";

    private final SystemRoleMapper systemRoleMapper;

    @Override
    public PageResultVo<SystemRolePageItemVo> pageRoles(SystemRolePageQueryDto queryDto) {
        SystemRolePageQueryDto query = normalizePageQuery(queryDto);
        long pageNum = normalizePageNum(query.getPageNum());
        long pageSize = normalizePageSize(query.getPageSize());

        Page<SystemRolePageItemVo> page = new Page<>(pageNum, pageSize);
        IPage<SystemRolePageItemVo> pageData = systemRoleMapper.selectRolePage(page, query);

        List<SystemRolePageItemVo> records = pageData.getRecords();
        if (records == null) {
            records = Collections.emptyList();
        }
        for (SystemRolePageItemVo item : records) {
            item.setUserCount(defaultInt(item.getUserCount()));
            item.setStatus(item.getStatus() == null ? 1 : item.getStatus());
            item.setStatusText(statusToText(item.getStatus()));
        }

        return PageResultVo.<SystemRolePageItemVo>builder()
                .pageNum(pageData.getCurrent())
                .pageSize(pageData.getSize())
                .total(pageData.getTotal())
                .pages(pageData.getPages())
                .records(records)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SystemRoleSaveResultVo createRole(SystemRoleCreateDto createDto) {
        if (createDto == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "request body is required");
        }

        String roleName = requireRoleName(createDto.getRoleName());
        checkRoleNameDuplicate(roleName, null);

        String description = normalizeDescription(createDto.getDescription());
        Integer status = createDto.getStatus() == null ? 1 : parseStatusCode(createDto.getStatus(), "status");

        SysRoleEntity entity = new SysRoleEntity();
        entity.setRoleId(IdWorker.getId());
        entity.setRoleName(roleName);
        entity.setDescription(description);
        entity.setUserCount(0);
        entity.setStatus(status);
        entity.setIsDeleted(0);

        systemRoleMapper.insertRole(entity);
        return normalizeRoleSaveResult(systemRoleMapper.selectRoleSaveResult(entity.getRoleId()));
    }

    @Override
    public SystemRoleDetailVo getRoleDetail(Long roleId) {
        if (roleId == null || roleId <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "roleId is invalid");
        }

        SystemRoleDetailVo detailVo = systemRoleMapper.selectRoleDetail(roleId);
        if (detailVo == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "role not found");
        }

        detailVo.setUserCount(defaultInt(detailVo.getUserCount()));
        detailVo.setStatus(detailVo.getStatus() == null ? 1 : detailVo.getStatus());
        detailVo.setStatusText(statusToText(detailVo.getStatus()));
        detailVo.setRoleName(trimToNull(detailVo.getRoleName()));
        return detailVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SystemRoleSaveResultVo updateRole(SystemRoleUpdateDto updateDto) {
        if (updateDto == null || updateDto.getRoleId() == null || updateDto.getRoleId() <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "roleId is invalid");
        }

        SystemRoleDetailVo existing = getRoleDetail(updateDto.getRoleId());

        String finalRoleName = updateDto.getRoleName() == null
                ? existing.getRoleName()
                : requireRoleName(updateDto.getRoleName());
        if (!finalRoleName.equals(existing.getRoleName())) {
            checkRoleNameDuplicate(finalRoleName, updateDto.getRoleId());
        }

        String finalDescription = updateDto.getDescription() == null
                ? existing.getDescription()
                : normalizeDescription(updateDto.getDescription());

        Integer finalStatus = updateDto.getStatus() == null
                ? (existing.getStatus() == null ? 1 : existing.getStatus())
                : parseStatusCode(updateDto.getStatus(), "status");

        SysRoleEntity entity = new SysRoleEntity();
        entity.setRoleId(updateDto.getRoleId());
        entity.setRoleName(finalRoleName);
        entity.setDescription(finalDescription);
        entity.setStatus(finalStatus);

        systemRoleMapper.updateRoleById(entity);
        return normalizeRoleSaveResult(systemRoleMapper.selectRoleSaveResult(updateDto.getRoleId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SystemRoleDeleteResultVo deleteRole(Long roleId, Boolean force) {
        SystemRoleDetailVo target = getRoleDetail(roleId);
        if (isSuperAdminRole(target)) {
            throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "\u7cfb\u7edf\u5185\u7f6e\u8d85\u7ba1\u89d2\u8272\u7981\u6b62\u5220\u9664");
        }

        long associatedUserCount = defaultLong(systemRoleMapper.countUsersByRoleId(roleId));
        boolean forcedDelete = Boolean.TRUE.equals(force);
        if (associatedUserCount > 0 && !forcedDelete) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR.getCode(),
                    "\u89d2\u8272\u5df2\u5173\u8054\u7528\u6237\uff0c\u8bf7\u4e8c\u6b21\u786e\u8ba4\u540e\u5220\u9664\uff08\u4f20 force=true\uff09");
        }

        systemRoleMapper.logicalDeleteRolePermissions(roleId);
        systemRoleMapper.logicalDeleteRole(roleId);

        SystemRoleDeleteResultVo resultVo = new SystemRoleDeleteResultVo();
        resultVo.setRoleId(roleId);
        resultVo.setDeleted(true);
        resultVo.setAssociatedUserCount(associatedUserCount);
        resultVo.setForcedDelete(forcedDelete && associatedUserCount > 0);
        return resultVo;
    }

    @Override
    public SystemRolePermissionDetailVo getRolePermissions(Long roleId) {
        SystemRoleDetailVo roleDetail = getRoleDetail(roleId);

        List<SystemRolePermissionItemVo> templates = systemRoleMapper.selectPermissionTemplates();
        if (templates == null) {
            templates = new ArrayList<>();
        }

        List<String> rolePermissionCodes = systemRoleMapper.selectRolePermissionCodes(roleId);
        Set<String> checkedCodeSet = rolePermissionCodes == null ? Collections.emptySet() : new HashSet<>(rolePermissionCodes);

        List<Long> checkedIds = new ArrayList<>();
        for (SystemRolePermissionItemVo item : templates) {
            boolean checked = StringUtils.hasText(item.getPermissionCode())
                    && checkedCodeSet.contains(item.getPermissionCode());
            item.setChecked(checked);
            if (checked && item.getPermissionId() != null) {
                checkedIds.add(item.getPermissionId());
            }
        }

        SystemRolePermissionDetailVo detailVo = new SystemRolePermissionDetailVo();
        detailVo.setRoleId(roleId);
        detailVo.setRoleName(roleDetail.getRoleName());
        detailVo.setPermissionOptions(templates);
        detailVo.setCheckedPermissionIds(checkedIds);
        return detailVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SystemRolePermissionSaveResultVo saveRolePermissions(SystemRolePermissionSaveDto saveDto) {
        if (saveDto == null || saveDto.getRoleId() == null || saveDto.getRoleId() <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "roleId is invalid");
        }

        getRoleDetail(saveDto.getRoleId());
        List<Long> permissionIds = normalizePermissionIds(saveDto.getPermissionIds());

        LinkedHashMap<String, String> permissionCodeNameMap = new LinkedHashMap<>();
        if (!permissionIds.isEmpty()) {
            List<SystemRolePermissionItemVo> selectedPermissions =
                    systemRoleMapper.selectPermissionTemplatesByIds(permissionIds);
            if (selectedPermissions == null) {
                selectedPermissions = Collections.emptyList();
            }

            Map<Long, SystemRolePermissionItemVo> selectedMap = new HashMap<>();
            for (SystemRolePermissionItemVo item : selectedPermissions) {
                if (item.getPermissionId() != null) {
                    selectedMap.put(item.getPermissionId(), item);
                }
            }

            for (Long permissionId : permissionIds) {
                if (!selectedMap.containsKey(permissionId)) {
                    throw new BusinessException(ResultCode.BAD_REQUEST.getCode(),
                            "permissionIds contains invalid id");
                }
            }

            for (Long permissionId : permissionIds) {
                SystemRolePermissionItemVo item = selectedMap.get(permissionId);
                String permissionCode = trimToNull(item.getPermissionCode());
                if (!StringUtils.hasText(permissionCode)) {
                    continue;
                }
                String permissionName = trimToNull(item.getPermissionName());
                permissionCodeNameMap.putIfAbsent(permissionCode,
                        StringUtils.hasText(permissionName) ? permissionName : permissionCode);
            }
        }

        systemRoleMapper.logicalDeleteRolePermissions(saveDto.getRoleId());

        for (Map.Entry<String, String> entry : permissionCodeNameMap.entrySet()) {
            SysRolePermissionEntity entity = new SysRolePermissionEntity();
            entity.setId(IdWorker.getId());
            entity.setRoleId(saveDto.getRoleId());
            entity.setPermissionCode(entry.getKey());
            entity.setPermissionName(entry.getValue());
            entity.setIsDeleted(0);
            systemRoleMapper.insertRolePermission(entity);
        }

        SystemRolePermissionSaveResultVo resultVo = new SystemRolePermissionSaveResultVo();
        resultVo.setRoleId(saveDto.getRoleId());
        resultVo.setPermissionCount(permissionCodeNameMap.size());
        resultVo.setSaveSuccess(true);
        resultVo.setSaveTime(LocalDateTime.now());
        return resultVo;
    }

    private SystemRolePageQueryDto normalizePageQuery(SystemRolePageQueryDto queryDto) {
        SystemRolePageQueryDto query = queryDto == null ? new SystemRolePageQueryDto() : queryDto;
        query.setKeyword(trimToNull(query.getKeyword()));
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

    private void checkRoleNameDuplicate(String roleName, Long excludeRoleId) {
        Long duplicate = systemRoleMapper.countRoleName(roleName, excludeRoleId);
        if (duplicate != null && duplicate > 0) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR.getCode(), "roleName already exists");
        }
    }

    private String requireRoleName(String roleName) {
        String value = trimToNull(roleName);
        if (!StringUtils.hasText(value) || value.length() > 32) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "roleName is invalid");
        }
        return value;
    }

    private String normalizeDescription(String description) {
        String value = trimToNull(description);
        if (value != null && value.length() > 256) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "description is invalid");
        }
        return value;
    }

    private Integer parseStatusCode(Integer status, String fieldName) {
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), fieldName + " is invalid");
        }
        return status;
    }

    private String statusToText(Integer status) {
        if (status == null) {
            return "\u672a\u77e5";
        }
        if (status == 1) {
            return "\u542f\u7528";
        }
        if (status == 0) {
            return "\u7981\u7528";
        }
        return "\u672a\u77e5";
    }

    private SystemRoleSaveResultVo normalizeRoleSaveResult(SystemRoleSaveResultVo resultVo) {
        if (resultVo == null) {
            return null;
        }
        resultVo.setUserCount(defaultInt(resultVo.getUserCount()));
        resultVo.setStatus(resultVo.getStatus() == null ? 1 : resultVo.getStatus());
        resultVo.setStatusText(statusToText(resultVo.getStatus()));
        resultVo.setRoleName(trimToNull(resultVo.getRoleName()));
        return resultVo;
    }

    private boolean isSuperAdminRole(SystemRoleDetailVo roleDetail) {
        if (roleDetail == null) {
            return false;
        }
        if (roleDetail.getRoleId() != null && SUPER_ADMIN_ROLE_ID.equals(roleDetail.getRoleId())) {
            return true;
        }
        if (!StringUtils.hasText(roleDetail.getRoleName())) {
            return false;
        }

        String normalizedRoleName = roleDetail.getRoleName().trim();
        return "system-admin".equalsIgnoreCase(normalizedRoleName)
                || SUPER_ADMIN_ROLE_NAME_CN.equals(normalizedRoleName);
    }

    private List<Long> normalizePermissionIds(List<Long> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return Collections.emptyList();
        }

        LinkedHashSet<Long> idSet = new LinkedHashSet<>();
        for (Long permissionId : permissionIds) {
            if (permissionId == null || permissionId <= 0) {
                throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "permissionIds is invalid");
            }
            idSet.add(permissionId);
        }
        return new ArrayList<>(idSet);
    }

    private Integer defaultInt(Integer value) {
        return value == null ? 0 : value;
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
