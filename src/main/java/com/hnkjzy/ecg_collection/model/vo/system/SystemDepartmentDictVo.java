package com.hnkjzy.ecg_collection.model.vo.system;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import com.hnkjzy.ecg_collection.model.vo.common.DictOptionVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class SystemDepartmentDictVo extends BaseVo {

    private List<SystemDepartmentTreeItemVo> parentDeptTree;
    private List<DictOptionVo> deptLevelOptions;
    private List<DictOptionVo> statusOptions;
}
