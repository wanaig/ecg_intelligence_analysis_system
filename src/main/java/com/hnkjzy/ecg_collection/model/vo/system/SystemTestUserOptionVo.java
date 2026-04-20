package com.hnkjzy.ecg_collection.model.vo.system;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 测试人员下拉选项。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SystemTestUserOptionVo extends BaseVo {

    private Long userId;
    private String userName;
    private String realName;
}
