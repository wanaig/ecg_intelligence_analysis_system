package com.hnkjzy.ecg_collection.model.dto.system;

import com.hnkjzy.ecg_collection.model.dto.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 淇濆瓨瑙掕壊鏉冮檺璇锋眰銆?
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SystemRolePermissionSaveDto extends BaseDto {

    private Long roleId;
    private List<Long> permissionIds;
}

