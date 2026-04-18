package com.hnkjzy.ecg_collection.model.vo.monitor;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 科室分布项。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorWardDistributionItemVo extends BaseVo {

    private String wardName;
    private Long patientCount;
}
