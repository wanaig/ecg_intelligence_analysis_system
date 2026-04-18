package com.hnkjzy.ecg_collection.model.dto.system;

import com.hnkjzy.ecg_collection.model.dto.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 编辑用户请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SystemUserUpdateDto extends BaseDto {

    private Long userId;
    private String userName;
    private String realName;
    private Long roleId;
    private Long deptId;
    private String phone;
    private String email;
    private Integer status;
}
