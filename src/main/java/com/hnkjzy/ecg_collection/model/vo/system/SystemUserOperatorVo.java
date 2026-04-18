package com.hnkjzy.ecg_collection.model.vo.system;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 当前操作人信息。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SystemUserOperatorVo extends BaseVo {

    private Long userId;
    private String userName;
    private String realName;
    private Long roleId;
    private Integer status;
}
