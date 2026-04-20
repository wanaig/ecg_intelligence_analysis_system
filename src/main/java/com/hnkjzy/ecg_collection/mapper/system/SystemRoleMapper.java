package com.hnkjzy.ecg_collection.mapper.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnkjzy.ecg_collection.mapper.BaseMapperX;
import com.hnkjzy.ecg_collection.model.dto.system.SystemRolePageQueryDto;
import com.hnkjzy.ecg_collection.model.entity.system.SysRoleEntity;
import com.hnkjzy.ecg_collection.model.entity.system.SysRolePermissionEntity;
import com.hnkjzy.ecg_collection.model.vo.system.SystemRoleDetailVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemRolePageItemVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemRolePermissionItemVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemRoleSaveResultVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 绯荤粺绠＄悊-瑙掕壊绠＄悊 Mapper銆?
 */
public interface SystemRoleMapper extends BaseMapperX<SysRoleEntity> {

    IPage<SystemRolePageItemVo> selectRolePage(Page<SystemRolePageItemVo> page,
                                               @Param("req") SystemRolePageQueryDto req);

    Long countRoleName(@Param("roleName") String roleName, @Param("excludeRoleId") Long excludeRoleId);

    Long selectMaxRoleIdInRangeForUpdate(@Param("maxAllowedId") Long maxAllowedId);

    Long selectMaxRolePermissionIdInRangeForUpdate(@Param("maxAllowedId") Long maxAllowedId);

    int insertRole(SysRoleEntity entity);

    int updateRoleById(SysRoleEntity entity);

    SystemRoleDetailVo selectRoleDetail(@Param("roleId") Long roleId);

    SystemRoleSaveResultVo selectRoleSaveResult(@Param("roleId") Long roleId);

    Long countUsersByRoleId(@Param("roleId") Long roleId);

    int logicalDeleteRole(@Param("roleId") Long roleId);

    int logicalDeleteRolePermissions(@Param("roleId") Long roleId);

    List<SystemRolePermissionItemVo> selectPermissionTemplates();

    List<SystemRolePermissionItemVo> selectPermissionTemplatesByIds(@Param("permissionIds") List<Long> permissionIds);

    List<String> selectRolePermissionCodes(@Param("roleId") Long roleId);

    int insertRolePermission(SysRolePermissionEntity entity);
}

