package com.hnkjzy.ecg_collection.model.vo.monitor;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 设备使用统计。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorDeviceUsageStatVo extends BaseVo {

    private Long todayMeasureCount;
    private Long weekMeasureCount;
    private Long monthMeasureCount;
    private BigDecimal onlineRate;
    private BigDecimal errorRate;
}
