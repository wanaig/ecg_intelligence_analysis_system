package com.hnkjzy.ecg_collection.model.vo.system;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 科室状态切换结果。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SystemDepartmentStatusUpdateResultVo extends BaseVo {

    private Long deptId;
    private Integer status;
    private String statusText;
}
