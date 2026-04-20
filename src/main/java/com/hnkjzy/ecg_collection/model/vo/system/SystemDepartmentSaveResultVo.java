package com.hnkjzy.ecg_collection.model.vo.system;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class SystemDepartmentSaveResultVo extends BaseVo {

    private Long deptId;
    private String deptName;
    private Long parentDeptId;
    private String parentDeptName;

    private Integer deptType;
    private String deptTypeText;
    private String director;
    private String phone;

    private Integer deptLevel;
    private String deptLevelText;
    private String deptDirector;
    private String contactPhone;
    private String location;
    private Integer status;
    private String statusText;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
