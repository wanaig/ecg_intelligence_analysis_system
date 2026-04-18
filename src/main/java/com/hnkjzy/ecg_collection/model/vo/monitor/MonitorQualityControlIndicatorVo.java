package com.hnkjzy.ecg_collection.model.vo.monitor;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 质控测试指标明细。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorQualityControlIndicatorVo extends BaseVo {

    private String indicatorCode;
    private String indicatorName;
    private String indicatorValue;
    private String result;
}
