package com.hnkjzy.ecg_collection.model.vo.system;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 瑙掕壊璇︽儏銆?
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SystemRoleDetailVo extends BaseVo {

    private Long roleId;
    private String roleName;
    private String description;
    private Integer userCount;
    private Integer status;
    private String statusText;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

