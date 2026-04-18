package com.hnkjzy.ecg_collection.model.dto.system;

import com.hnkjzy.ecg_collection.model.dto.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 新增用户请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SystemUserCreateDto extends BaseDto {

    private String userName;
    private String realName;
    private String password;
    private Long roleId;
    private Long deptId;
    private String phone;
    private String email;
    private Integer status;
}
