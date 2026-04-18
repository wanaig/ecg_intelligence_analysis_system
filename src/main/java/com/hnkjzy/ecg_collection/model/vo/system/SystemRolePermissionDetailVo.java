package com.hnkjzy.ecg_collection.model.vo.system;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 瑙掕壊鏉冮檺璇︽儏銆?
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SystemRolePermissionDetailVo extends BaseVo {

    private Long roleId;
    private String roleName;
    private List<SystemRolePermissionItemVo> permissionOptions;
    private List<Long> checkedPermissionIds;
}

