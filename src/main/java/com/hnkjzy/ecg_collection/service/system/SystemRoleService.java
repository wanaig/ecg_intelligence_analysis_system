package com.hnkjzy.ecg_collection.service.system;

import com.hnkjzy.ecg_collection.model.dto.system.SystemRoleCreateDto;
import com.hnkjzy.ecg_collection.model.dto.system.SystemRolePageQueryDto;
import com.hnkjzy.ecg_collection.model.dto.system.SystemRolePermissionSaveDto;
import com.hnkjzy.ecg_collection.model.dto.system.SystemRoleUpdateDto;
import com.hnkjzy.ecg_collection.model.vo.common.PageResultVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemRoleDeleteResultVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemRoleDetailVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemRolePageItemVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemRolePermissionDetailVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemRolePermissionSaveResultVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemRoleSaveResultVo;
import com.hnkjzy.ecg_collection.service.BaseService;

/**
 * 绯荤粺绠＄悊-瑙掕壊绠＄悊鏈嶅姟銆?
 */
public interface SystemRoleService extends BaseService {

    PageResultVo<SystemRolePageItemVo> pageRoles(SystemRolePageQueryDto queryDto);

    SystemRoleSaveResultVo createRole(SystemRoleCreateDto createDto);

    SystemRoleDetailVo getRoleDetail(Long roleId);

    SystemRoleSaveResultVo updateRole(SystemRoleUpdateDto updateDto);

    SystemRoleDeleteResultVo deleteRole(Long roleId, Boolean force);

    SystemRolePermissionDetailVo getRolePermissions(Long roleId);

    SystemRolePermissionSaveResultVo saveRolePermissions(SystemRolePermissionSaveDto saveDto);
}

