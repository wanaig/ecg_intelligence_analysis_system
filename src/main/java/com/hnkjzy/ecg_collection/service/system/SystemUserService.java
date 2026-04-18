package com.hnkjzy.ecg_collection.service.system;

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
import com.hnkjzy.ecg_collection.service.BaseService;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 系统管理-用户管理服务。
 */
public interface SystemUserService extends BaseService {

    PageResultVo<SystemUserPageItemVo> pageUsers(SystemUserPageQueryDto queryDto);

    SystemUserDictVo getUserDicts();

    SystemUserSaveResultVo createUser(SystemUserCreateDto createDto);

    SystemUserDetailVo getUserDetail(Long userId);

    SystemUserSaveResultVo updateUser(SystemUserUpdateDto updateDto);

    SystemUserDeleteResultVo deleteUser(Long userId, HttpServletRequest request);

    SystemUserResetPasswordResultVo resetPassword(SystemUserResetPasswordDto resetPasswordDto);
}
