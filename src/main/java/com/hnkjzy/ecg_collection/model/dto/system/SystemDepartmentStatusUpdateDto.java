package com.hnkjzy.ecg_collection.model.dto.system;

import com.hnkjzy.ecg_collection.model.dto.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 科室状态切换请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SystemDepartmentStatusUpdateDto extends BaseDto {

    private Long deptId;
    private Integer status;
}
