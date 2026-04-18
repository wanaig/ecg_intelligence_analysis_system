package com.hnkjzy.ecg_collection.model.dto.system;

import com.hnkjzy.ecg_collection.model.dto.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 鏂板瑙掕壊璇锋眰銆?
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SystemRoleCreateDto extends BaseDto {

    private String roleName;
    private String description;
    private Integer status;
}

