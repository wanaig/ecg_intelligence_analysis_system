package com.hnkjzy.ecg_collection.model.dto.system;

import com.hnkjzy.ecg_collection.model.dto.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SystemDepartmentPageQueryDto extends BaseDto {

    private String keyword;
    private Long parentDeptId;
    private String status;
    private Long pageNum;
    private Long pageSize;
}
