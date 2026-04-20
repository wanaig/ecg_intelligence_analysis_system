package com.hnkjzy.ecg_collection.model.vo.monitor;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 重点监护状态变更结果。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorKeyMonitorResultVo extends BaseVo {

    private Long patientId;
    private Boolean keyMonitor;
    private String monitorStatus;
    private String monitorType;
}
