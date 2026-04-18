package com.hnkjzy.ecg_collection.model.vo.system;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 瑙掕壊鏉冮檺椤广€?
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SystemRolePermissionItemVo extends BaseVo {

    private Long permissionId;
    private String permissionCode;
    private String permissionName;
    private Boolean checked;
}

