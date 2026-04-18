package com.hnkjzy.ecg_collection.model.vo.system;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class SystemDepartmentTreeItemVo extends BaseVo {

    private Long deptId;
    private Long parentDeptId;
    private String deptName;
    private List<SystemDepartmentTreeItemVo> children;
}
