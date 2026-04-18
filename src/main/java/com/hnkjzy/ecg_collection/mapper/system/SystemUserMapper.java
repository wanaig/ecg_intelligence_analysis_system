package com.hnkjzy.ecg_collection.mapper.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnkjzy.ecg_collection.mapper.BaseMapperX;
import com.hnkjzy.ecg_collection.model.dto.system.SystemUserPageQueryDto;
import com.hnkjzy.ecg_collection.model.entity.system.SysOperationLogEntity;
import com.hnkjzy.ecg_collection.model.entity.system.SysUserEntity;
import com.hnkjzy.ecg_collection.model.vo.common.DictOptionVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemUserDetailVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemUserOperatorVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemUserPageItemVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemUserSaveResultVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统管理-用户管理 Mapper。
 */
public interface SystemUserMapper extends BaseMapperX<SysUserEntity> {

    IPage<SystemUserPageItemVo> selectUserPage(Page<SystemUserPageItemVo> page,
                                               @Param("req") SystemUserPageQueryDto req,
                                               @Param("statusCode") Integer statusCode);

    List<DictOptionVo> selectRoleOptions();

    List<DictOptionVo> selectDepartmentOptions();

    Long countUserName(@Param("userName") String userName, @Param("excludeUserId") Long excludeUserId);

    String selectRoleNameById(@Param("roleId") Long roleId);

    String selectDeptNameById(@Param("deptId") Long deptId);

    int insertUser(SysUserEntity entity);

    int updateUserById(SysUserEntity entity);

    SystemUserSaveResultVo selectUserSaveResult(@Param("userId") Long userId);

    SystemUserDetailVo selectUserDetail(@Param("userId") Long userId);

    int logicalDeleteUser(@Param("userId") Long userId);

    SystemUserOperatorVo selectOperatorById(@Param("userId") Long userId);

    SystemUserOperatorVo selectOperatorByUserName(@Param("userName") String userName);

    Long countRolePermission(@Param("roleId") Long roleId, @Param("permissionCodes") List<String> permissionCodes);

    int updateUserPassword(@Param("userId") Long userId, @Param("encodedPassword") String encodedPassword);

    int insertOperationLog(SysOperationLogEntity entity);
}
