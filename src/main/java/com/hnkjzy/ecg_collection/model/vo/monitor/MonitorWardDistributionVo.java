package com.hnkjzy.ecg_collection.model.vo.monitor;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 科室分布响应。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorWardDistributionVo extends BaseVo {

    private List<MonitorWardDistributionItemVo> dataList;
}
