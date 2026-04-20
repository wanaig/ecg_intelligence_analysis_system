package com.hnkjzy.ecg_collection.controller.system;

import com.hnkjzy.ecg_collection.common.result.ApiResponse;
import com.hnkjzy.ecg_collection.controller.BaseController;
import com.hnkjzy.ecg_collection.model.dto.system.SystemUserCreateDto;
import com.hnkjzy.ecg_collection.model.dto.system.SystemUserPageQueryDto;
import com.hnkjzy.ecg_collection.model.dto.system.SystemUserResetPasswordDto;
import com.hnkjzy.ecg_collection.model.dto.system.SystemUserUpdateDto;
import com.hnkjzy.ecg_collection.model.vo.common.PageResultVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemUserDeleteResultVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemUserDetailVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemUserDictVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemUserPageItemVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemUserResetPasswordResultVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemUserSaveResultVo;
import com.hnkjzy.ecg_collection.service.system.SystemUserService;
import jakarta.servlet.http.HttpServletRequest;
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
 * 系统管理-用户管理接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/system/user", "/system/user"})
public class SystemUserController extends BaseController {

    private final SystemUserService systemUserService;

    @PostMapping("/page")
    public ApiResponse<PageResultVo<SystemUserPageItemVo>> page(@RequestBody(required = false) SystemUserPageQueryDto queryDto) {
        return ApiResponse.success(systemUserService.pageUsers(queryDto));
    }

    @GetMapping("/dicts")
    public ApiResponse<SystemUserDictVo> dicts() {
        return ApiResponse.success(systemUserService.getUserDicts());
    }

    @PostMapping
    public ApiResponse<SystemUserSaveResultVo> create(@RequestBody SystemUserCreateDto createDto) {
        return ApiResponse.success(systemUserService.createUser(createDto));
    }

    @GetMapping("/detail")
    public ApiResponse<SystemUserDetailVo> detail(@RequestParam("userId") Long userId) {
        return ApiResponse.success(systemUserService.getUserDetail(userId));
    }

    @PutMapping
    public ApiResponse<SystemUserSaveResultVo> update(@RequestBody SystemUserUpdateDto updateDto) {
        return ApiResponse.success(systemUserService.updateUser(updateDto));
    }

    @DeleteMapping
    public ApiResponse<SystemUserDeleteResultVo> delete(@RequestParam("userId") Long userId, HttpServletRequest request) {
        return ApiResponse.success(systemUserService.deleteUser(userId, request));
    }

    @PostMapping("/reset-password")
    public ApiResponse<SystemUserResetPasswordResultVo> resetPassword(@RequestBody SystemUserResetPasswordDto resetPasswordDto) {
        return ApiResponse.success(systemUserService.resetPassword(resetPasswordDto));
    }
}
