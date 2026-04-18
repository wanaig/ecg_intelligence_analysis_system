package com.hnkjzy.ecg_collection.model.dto.system;

import com.hnkjzy.ecg_collection.model.dto.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 重置密码请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SystemUserResetPasswordDto extends BaseDto {

    private Long userId;
    private String newPassword;
}
