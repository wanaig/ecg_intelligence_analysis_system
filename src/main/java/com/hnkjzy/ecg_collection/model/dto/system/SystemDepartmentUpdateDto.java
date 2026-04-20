package com.hnkjzy.ecg_collection.model.dto.system;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.hnkjzy.ecg_collection.model.dto.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SystemDepartmentUpdateDto extends BaseDto {

    private Long deptId;
    private String deptName;

    @JsonAlias({"parentDeptId"})
    private Long parentId;

    @JsonAlias({"deptLevel"})
    private Integer deptType;

    @JsonAlias({"deptDirector"})
    private String director;

    @JsonAlias({"contactPhone"})
    private String phone;

    private String location;
    private Integer status;
}
