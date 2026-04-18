package com.hnkjzy.ecg_collection.model.vo.system;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 淇濆瓨瑙掕壊鏉冮檺杩斿洖銆?
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SystemRolePermissionSaveResultVo extends BaseVo {

    private Long roleId;
    private Integer permissionCount;
    private Boolean saveSuccess;
    private LocalDateTime saveTime;
}

