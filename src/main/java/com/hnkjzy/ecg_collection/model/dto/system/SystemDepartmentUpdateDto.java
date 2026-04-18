package com.hnkjzy.ecg_collection.model.dto.system;

import com.hnkjzy.ecg_collection.model.dto.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SystemDepartmentUpdateDto extends BaseDto {

    private Long deptId;
    private String deptName;
    private Long parentDeptId;
    private Integer deptLevel;
    private String deptDirector;
    private String contactPhone;
    private String location;
    private Integer status;
}
