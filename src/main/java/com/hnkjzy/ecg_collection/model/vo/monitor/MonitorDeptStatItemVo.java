package com.hnkjzy.ecg_collection.model.vo.monitor;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 科室分布统计项。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorDeptStatItemVo extends BaseVo {

    private String deptName;
    private Long patientCount;
}
