package com.hnkjzy.ecg_collection.model.vo.monitor;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 监护详情中的预警历史。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorWarningHistoryVo extends BaseVo {

    private Long alertId;
    private LocalDateTime warningTime;
    private String warningType;
    private Integer alertLevel;
    private String alertLevelText;
    private Integer alertStatus;
    private String alertStatusText;
    private LocalDateTime handleTime;
    private String handleUserName;
    private String handleRemark;
}
