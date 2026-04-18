package com.hnkjzy.ecg_collection.model.vo.monitor;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 心电趋势点。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorEcgTrendPointVo extends BaseVo {

    private LocalDateTime trendTime;
    private Integer heartRate;
    private Integer abnormalLevel;
    private String abnormalLevelText;
}
