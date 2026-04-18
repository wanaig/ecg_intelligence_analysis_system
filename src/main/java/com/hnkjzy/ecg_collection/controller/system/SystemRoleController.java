package com.hnkjzy.ecg_collection.controller.system;

import com.hnkjzy.ecg_collection.common.result.ApiResponse;
import com.hnkjzy.ecg_collection.controller.BaseController;
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
import com.hnkjzy.ecg_collection.service.system.SystemRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 绯荤粺绠＄悊-瑙掕壊绠＄悊鎺ュ彛銆?
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/system/role")
public class SystemRoleController extends BaseController {

    private final SystemRoleService systemRoleService;

    @PostMapping("/page")
    public ApiResponse<PageResultVo<SystemRolePageItemVo>> page(@RequestBody(required = false) SystemRolePageQueryDto queryDto) {
        return ApiResponse.success(systemRoleService.pageRoles(queryDto));
    }

    @PostMapping
    public ApiResponse<SystemRoleSaveResultVo> create(@RequestBody SystemRoleCreateDto createDto) {
        return ApiResponse.success(systemRoleService.createRole(createDto));
    }

    @GetMapping("/detail")
    public ApiResponse<SystemRoleDetailVo> detail(@RequestParam("roleId") Long roleId) {
        return ApiResponse.success(systemRoleService.getRoleDetail(roleId));
    }

    @PutMapping
    public ApiResponse<SystemRoleSaveResultVo> update(@RequestBody SystemRoleUpdateDto updateDto) {
        return ApiResponse.success(systemRoleService.updateRole(updateDto));
    }

    @DeleteMapping
    public ApiResponse<SystemRoleDeleteResultVo> delete(@RequestParam("roleId") Long roleId,
                                                         @RequestParam(value = "force", required = false) Boolean force) {
        return ApiResponse.success(systemRoleService.deleteRole(roleId, force));
    }

    @GetMapping("/permissions")
    public ApiResponse<SystemRolePermissionDetailVo> permissions(@RequestParam("roleId") Long roleId) {
        return ApiResponse.success(systemRoleService.getRolePermissions(roleId));
    }

    @PostMapping("/permissions")
    public ApiResponse<SystemRolePermissionSaveResultVo> savePermissions(@RequestBody SystemRolePermissionSaveDto saveDto) {
        return ApiResponse.success(systemRoleService.saveRolePermissions(saveDto));
    }
}

