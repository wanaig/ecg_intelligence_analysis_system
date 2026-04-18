package com.hnkjzy.ecg_collection.model.vo.system;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 瑙掕壊鍒犻櫎杩斿洖銆?
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SystemRoleDeleteResultVo extends BaseVo {

    private Long roleId;
    private Boolean deleted;
    private Long associatedUserCount;
    private Boolean forcedDelete;
}

