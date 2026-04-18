package com.hnkjzy.ecg_collection.model.dto.system;

import com.hnkjzy.ecg_collection.model.dto.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 缂栬緫瑙掕壊璇锋眰銆?
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SystemRoleUpdateDto extends BaseDto {

    private Long roleId;
    private String roleName;
    private String description;
    private Integer status;
}

