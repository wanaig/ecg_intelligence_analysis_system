package com.hnkjzy.ecg_collection.model.vo.system;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户保存返回。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SystemUserSaveResultVo extends BaseVo {

    private Long userId;
    private String userName;
    private String realName;
    private Long roleId;
    private String roleName;
    private Long deptId;
    private String deptName;
    private String phone;
    private String email;
    private Integer status;
    private String statusText;
}
