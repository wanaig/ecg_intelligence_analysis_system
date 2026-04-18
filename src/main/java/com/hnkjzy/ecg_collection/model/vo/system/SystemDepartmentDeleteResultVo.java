package com.hnkjzy.ecg_collection.model.vo.system;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SystemDepartmentDeleteResultVo extends BaseVo {

    private Long deptId;
    private Boolean deleted;
    private Long childCount;
    private Long boundUserCount;
    private Long boundDeviceCount;
    private Boolean forcedDelete;
}
