package com.hnkjzy.ecg_collection.model.vo.system;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户重置密码返回。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SystemUserResetPasswordResultVo extends BaseVo {

    private Long userId;
    private Boolean resetSuccess;
    private LocalDateTime resetTime;
}
