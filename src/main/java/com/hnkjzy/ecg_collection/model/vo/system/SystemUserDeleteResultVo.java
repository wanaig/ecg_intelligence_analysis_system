package com.hnkjzy.ecg_collection.model.vo.system;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户删除返回。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SystemUserDeleteResultVo extends BaseVo {

    private Long userId;
    private Boolean deleted;
}
