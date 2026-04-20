package com.hnkjzy.ecg_collection.service.impl.system;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnkjzy.ecg_collection.common.exception.BusinessException;
import com.hnkjzy.ecg_collection.common.result.ResultCode;
import com.hnkjzy.ecg_collection.mapper.system.SystemUserMapper;
import com.hnkjzy.ecg_collection.model.dto.system.SystemUserCreateDto;
import com.hnkjzy.ecg_collection.model.dto.system.SystemUserPageQueryDto;
import com.hnkjzy.ecg_collection.model.dto.system.SystemUserResetPasswordDto;
import com.hnkjzy.ecg_collection.model.dto.system.SystemUserUpdateDto;
import com.hnkjzy.ecg_collection.model.entity.system.SysOperationLogEntity;
import com.hnkjzy.ecg_collection.model.entity.system.SysUserEntity;
import com.hnkjzy.ecg_collection.model.vo.common.DictOptionVo;
import com.hnkjzy.ecg_collection.model.vo.common.PageResultVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemUserDeleteResultVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemUserDetailVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemUserDictVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemUserOperatorVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemUserPageItemVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemUserResetPasswordResultVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemUserSaveResultVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemTestUserOptionVo;
import com.hnkjzy.ecg_collection.service.impl.BaseServiceImpl;
import com.hnkjzy.ecg_collection.service.system.SystemUserService;
import com.hnkjzy.ecg_collection.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 系统管理-用户管理服务实现。
 */
@Service
@RequiredArgsConstructor
public class SystemUserServiceImpl extends BaseServiceImpl implements SystemUserService {

    private static final long DEFAULT_PAGE_NUM = 1L;
    private static final long DEFAULT_PAGE_SIZE = 10L;
    private static final long MAX_PAGE_SIZE = 200L;
    private static final long USER_ID_BASE = 1300L;
    private static final long USER_ID_MAX_ALLOWED = 9_999_999_999L;
    private static final long OP_LOG_ID_BASE = 1400L;
    private static final long OP_LOG_ID_MAX_ALLOWED = 9_999_999_999L;

    private static final String DEFAULT_PASSWORD = "123456";
    private static final String SUPER_ADMIN_USER_NAME = "admin";
    private static final Long SUPER_ADMIN_ROLE_ID = 1001L;

    private static final String MODULE_USER_MANAGE = "系统管理-用户管理";
    private static final String OPERATION_DELETE = "删除";

    private static final List<String> DELETE_PERMISSION_CODES = List.of("system:all", "system:user:delete", "user:delete");

    private static final Pattern USER_NAME_PATTERN = Pattern.compile("^[A-Za-z0-9_]{3,32}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1\\d{10}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final SystemUserMapper systemUserMapper;
    private final JwtUtil jwtUtil;

    @Override
    public PageResultVo<SystemUserPageItemVo> pageUsers(SystemUserPageQueryDto queryDto) {
        SystemUserPageQueryDto query = normalizePageQuery(queryDto);
        if (query.getRoleId() != null && query.getRoleId() <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "roleId 参数不合法");
        }
        if (query.getDeptId() != null && query.getDeptId() <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "deptId 参数不合法");
        }

        Integer statusCode = parseStatusText(query.getStatus(), false, "status");
        long pageNum = normalizePageNum(query.getPageNum());
        long pageSize = normalizePageSize(query.getPageSize());

        Page<SystemUserPageItemVo> page = new Page<>(pageNum, pageSize);
        IPage<SystemUserPageItemVo> pageData = systemUserMapper.selectUserPage(page, query, statusCode);

        List<SystemUserPageItemVo> records = pageData.getRecords();
        if (records == null) {
            records = Collections.emptyList();
        }
        for (SystemUserPageItemVo item : records) {
            item.setStatus(item.getStatus() == null ? 1 : item.getStatus());
            if (!StringUtils.hasText(item.getStatusText())) {
                item.setStatusText(statusToText(item.getStatus()));
            }
        }

        return PageResultVo.<SystemUserPageItemVo>builder()
                .pageNum(pageData.getCurrent())
                .pageSize(pageData.getSize())
                .total(pageData.getTotal())
                .pages(pageData.getPages())
                .records(records)
                .build();
    }

    @Override
    public SystemUserDictVo getUserDicts() {
        SystemUserDictVo dictVo = new SystemUserDictVo();

        List<DictOptionVo> roleOptions = systemUserMapper.selectRoleOptions();
        if (roleOptions == null) {
            roleOptions = new ArrayList<>();
        }
        roleOptions.add(0, DictOptionVo.builder().value("").label("全部角色").build());

        List<DictOptionVo> departmentOptions = systemUserMapper.selectDepartmentOptions();
        if (departmentOptions == null) {
            departmentOptions = new ArrayList<>();
        }
        departmentOptions.add(0, DictOptionVo.builder().value("").label("全部科室").build());

        List<DictOptionVo> statusOptions = new ArrayList<>();
        statusOptions.add(DictOptionVo.builder().value("").label("全部状态").build());
        statusOptions.add(DictOptionVo.builder().value("1").label("启用").build());
        statusOptions.add(DictOptionVo.builder().value("0").label("禁用").build());

        dictVo.setRoleOptions(roleOptions);
        dictVo.setDepartmentOptions(departmentOptions);
        dictVo.setStatusOptions(statusOptions);
        return dictVo;
    }

    @Override
    public List<SystemTestUserOptionVo> listTestUserOptions() {
        List<SystemTestUserOptionVo> options = systemUserMapper.selectTestUserOptions();
        return options == null ? Collections.emptyList() : options;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SystemUserSaveResultVo createUser(SystemUserCreateDto createDto) {
        if (createDto == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "请求体不能为空");
        }

        String userName = requireUserName(createDto.getUserName());
        checkUserNameDuplicate(userName, null);

        String realName = requireRealName(createDto.getRealName());
        Long roleId = requirePositiveId(createDto.getRoleId(), "roleId 参数不合法");
        String roleName = requireRoleName(roleId);

        Long deptId = createDto.getDeptId();
        if (deptId != null && deptId <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "deptId 参数不合法");
        }
        String deptName = deptId == null ? null : requireDeptName(deptId);

        String phone = normalizePhone(createDto.getPhone());
        String email = normalizeEmail(createDto.getEmail());
        Integer status = createDto.getStatus() == null ? 1 : parseStatusCode(createDto.getStatus(), "status");
        String encodedPassword = encodePassword(resolvePassword(createDto.getPassword(), true));
        Long userId = nextUserId();

        SysUserEntity entity = new SysUserEntity();
        entity.setUserId(userId);
        entity.setUserName(userName);
        entity.setRealName(realName);
        entity.setPassword(encodedPassword);
        entity.setRoleId(roleId);
        entity.setRoleName(roleName);
        entity.setDeptId(deptId);
        entity.setDeptName(deptName);
        entity.setPhone(phone);
        entity.setEmail(email);
        entity.setStatus(status);
        entity.setCreateUserId(null);
        entity.setIsDeleted(0);

        systemUserMapper.insertUser(entity);
        return systemUserMapper.selectUserSaveResult(entity.getUserId());
    }

    @Override
    public SystemUserDetailVo getUserDetail(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "userId 参数不合法");
        }

        SystemUserDetailVo detailVo = systemUserMapper.selectUserDetail(userId);
        if (detailVo == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "用户不存在");
        }
        if (!StringUtils.hasText(detailVo.getStatusText())) {
            detailVo.setStatusText(statusToText(detailVo.getStatus()));
        }
        return detailVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SystemUserSaveResultVo updateUser(SystemUserUpdateDto updateDto) {
        if (updateDto == null || updateDto.getUserId() == null || updateDto.getUserId() <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "userId 参数不合法");
        }

        SystemUserDetailVo existing = getUserDetail(updateDto.getUserId());

        String finalUserName = updateDto.getUserName() == null
                ? existing.getUserName()
                : requireUserName(updateDto.getUserName());
        if (!finalUserName.equals(existing.getUserName())) {
            checkUserNameDuplicate(finalUserName, updateDto.getUserId());
        }

        String finalRealName = updateDto.getRealName() == null
                ? existing.getRealName()
                : requireRealName(updateDto.getRealName());

        Long finalRoleId = updateDto.getRoleId() == null
                ? existing.getRoleId()
                : requirePositiveId(updateDto.getRoleId(), "roleId 参数不合法");
        String finalRoleName = requireRoleName(finalRoleId);

        Long finalDeptId;
        if (updateDto.getDeptId() == null) {
            finalDeptId = existing.getDeptId();
        } else {
            finalDeptId = requirePositiveId(updateDto.getDeptId(), "deptId 参数不合法");
        }
        String finalDeptName = finalDeptId == null ? null : requireDeptName(finalDeptId);

        String finalPhone = updateDto.getPhone() == null ? existing.getPhone() : normalizePhone(updateDto.getPhone());
        String finalEmail = updateDto.getEmail() == null ? existing.getEmail() : normalizeEmail(updateDto.getEmail());

        Integer existingStatus = existing.getStatus() == null ? 1 : parseStatusCode(existing.getStatus(), "status");
        Integer finalStatus = updateDto.getStatus() == null
                ? existingStatus
                : parseStatusCode(updateDto.getStatus(), "status");

        SysUserEntity entity = new SysUserEntity();
        entity.setUserId(updateDto.getUserId());
        entity.setUserName(finalUserName);
        entity.setRealName(finalRealName);
        entity.setRoleId(finalRoleId);
        entity.setRoleName(finalRoleName);
        entity.setDeptId(finalDeptId);
        entity.setDeptName(finalDeptName);
        entity.setPhone(finalPhone);
        entity.setEmail(finalEmail);
        entity.setStatus(finalStatus);

        systemUserMapper.updateUserById(entity);
        return systemUserMapper.selectUserSaveResult(updateDto.getUserId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SystemUserDeleteResultVo deleteUser(Long userId, HttpServletRequest request) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "userId 参数不合法");
        }

        SystemUserOperatorVo operator = resolveCurrentOperator(request);
        validateDeletePermission(operator);

        SystemUserDetailVo target = getUserDetail(userId);
        if (isSuperAdmin(target)) {
            throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "超管账号禁止删除");
        }
        if (operator.getUserId() != null && operator.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR.getCode(), "不允许删除当前登录用户");
        }

        systemUserMapper.logicalDeleteUser(userId);
        insertDeleteOperationLog(operator, target, request);

        SystemUserDeleteResultVo resultVo = new SystemUserDeleteResultVo();
        resultVo.setUserId(userId);
        resultVo.setDeleted(true);
        return resultVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SystemUserResetPasswordResultVo resetPassword(SystemUserResetPasswordDto resetPasswordDto) {
        if (resetPasswordDto == null || resetPasswordDto.getUserId() == null || resetPasswordDto.getUserId() <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "userId 参数不合法");
        }

        getUserDetail(resetPasswordDto.getUserId());

        String encodedPassword = encodePassword(resolvePassword(resetPasswordDto.getNewPassword(), true));
        systemUserMapper.updateUserPassword(resetPasswordDto.getUserId(), encodedPassword);

        SystemUserResetPasswordResultVo resultVo = new SystemUserResetPasswordResultVo();
        resultVo.setUserId(resetPasswordDto.getUserId());
        resultVo.setResetSuccess(true);
        resultVo.setResetTime(LocalDateTime.now());
        return resultVo;
    }

    private SystemUserPageQueryDto normalizePageQuery(SystemUserPageQueryDto queryDto) {
        SystemUserPageQueryDto query = queryDto == null ? new SystemUserPageQueryDto() : queryDto;
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

    private void checkUserNameDuplicate(String userName, Long excludeUserId) {
        Long duplicate = systemUserMapper.countUserName(userName, excludeUserId);
        if (duplicate != null && duplicate > 0) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR.getCode(), "userName 已存在");
        }
    }

    private String requireUserName(String userName) {
        if (!StringUtils.hasText(userName)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "userName 参数不合法");
        }
        String value = userName.trim();
        if (!USER_NAME_PATTERN.matcher(value).matches()) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "userName 参数不合法");
        }
        return value;
    }

    private String requireRealName(String realName) {
        if (!StringUtils.hasText(realName)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "realName 参数不合法");
        }
        String value = realName.trim();
        if (value.length() > 32) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "realName 参数不合法");
        }
        return value;
    }

    private Long requirePositiveId(Long id, String fieldMessage) {
        if (id == null || id <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), fieldMessage);
        }
        return id;
    }

    private String requireRoleName(Long roleId) {
        String roleName = systemUserMapper.selectRoleNameById(roleId);
        if (!StringUtils.hasText(roleName)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "roleId 对应角色不存在");
        }
        return roleName;
    }

    private String requireDeptName(Long deptId) {
        String deptName = systemUserMapper.selectDeptNameById(deptId);
        if (!StringUtils.hasText(deptName)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "deptId 对应科室不存在");
        }
        return deptName;
    }

    private String normalizePhone(String phone) {
        String value = trimToNull(phone);
        if (value == null) {
            return null;
        }
        if (!PHONE_PATTERN.matcher(value).matches()) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "phone 参数不合法");
        }
        return value;
    }

    private String normalizeEmail(String email) {
        String value = trimToNull(email);
        if (value == null) {
            return null;
        }
        if (value.length() > 64 || !EMAIL_PATTERN.matcher(value).matches()) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "email 参数不合法");
        }
        return value;
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

    private String resolvePassword(String password, boolean allowDefault) {
        String value = trimToNull(password);
        if (value == null) {
            if (allowDefault) {
                return DEFAULT_PASSWORD;
            }
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "password 参数不合法");
        }
        if (value.length() < 6 || value.length() > 32) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "password 长度需在 6-32 之间");
        }
        return value;
    }

    private String encodePassword(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
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
            String value = trimToNull(subject);
            if (!StringUtils.hasText(value)) {
                throw new BusinessException(ResultCode.UNAUTHORIZED);
            }
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

    private void validateDeletePermission(SystemUserOperatorVo operator) {
        if (operator.getRoleId() == null) {
            throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "无权限删除用户");
        }
        Long count = systemUserMapper.countRolePermission(operator.getRoleId(), DELETE_PERMISSION_CODES);
        if (count == null || count <= 0) {
            throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "无权限删除用户");
        }
    }

    private boolean isSuperAdmin(SystemUserDetailVo target) {
        if (target == null) {
            return false;
        }
        if (StringUtils.hasText(target.getUserName()) && SUPER_ADMIN_USER_NAME.equalsIgnoreCase(target.getUserName().trim())) {
            return true;
        }
        if (target.getRoleId() != null && SUPER_ADMIN_ROLE_ID.equals(target.getRoleId())) {
            return true;
        }
        return StringUtils.hasText(target.getRoleName()) && "系统管理员".equals(target.getRoleName().trim());
    }

    private void insertDeleteOperationLog(SystemUserOperatorVo operator, SystemUserDetailVo target, HttpServletRequest request) {
        SysOperationLogEntity logEntity = new SysOperationLogEntity();
        logEntity.setLogId(nextOperationLogId());
        logEntity.setUserId(operator.getUserId());
        logEntity.setRealName(operator.getRealName());
        logEntity.setModule(MODULE_USER_MANAGE);
        logEntity.setOperationType(OPERATION_DELETE);
        logEntity.setOperationContent(buildDeleteOperationContent(target));
        logEntity.setRequestIp(resolveRequestIp(request));
        logEntity.setOperationTime(LocalDateTime.now());
        logEntity.setIsDeleted(0);
        systemUserMapper.insertOperationLog(logEntity);
    }

    private String buildDeleteOperationContent(SystemUserDetailVo target) {
        return "删除用户 userId=" + target.getUserId()
                + ", userName=" + nullToEmpty(target.getUserName())
                + ", realName=" + nullToEmpty(target.getRealName());
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

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private Long nextUserId() {
        Long maxUserId = systemUserMapper.selectMaxUserIdInRangeForUpdate(USER_ID_MAX_ALLOWED);
        long base = maxUserId == null ? USER_ID_BASE : maxUserId;
        if (base >= USER_ID_MAX_ALLOWED) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR.getCode(), "用户ID已达到上限，请联系管理员");
        }
        return base + 1;
    }

    private Long nextOperationLogId() {
        Long maxLogId = systemUserMapper.selectMaxOperationLogIdInRangeForUpdate(OP_LOG_ID_MAX_ALLOWED);
        long base = maxLogId == null ? OP_LOG_ID_BASE : maxLogId;
        if (base >= OP_LOG_ID_MAX_ALLOWED) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR.getCode(), "操作日志ID已达到上限，请联系管理员");
        }
        return base + 1;
    }
}
