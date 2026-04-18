package com.hnkjzy.ecg_collection.model.vo.analysis;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 设备使用维度统计项。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AnalysisDeviceUsageStatVo extends BaseVo {

    private String dimension;
    private Long count;
}
