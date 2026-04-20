package com.hnkjzy.ecg_collection.model.vo.monitor;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 科室分布统计响应。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorDeptStatVo extends BaseVo {

    private List<MonitorDeptStatItemVo> list;
}
