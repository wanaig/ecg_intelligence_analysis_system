package com.hnkjzy.ecg_collection.model.vo.monitor;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 设备维保记录项。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorDeviceMaintainRecordVo extends BaseVo {

    private Long maintainId;
    private String maintainType;
    private String maintainUserName;
    private LocalDate maintainTime;
    private String maintainContent;
    private String maintainResult;
    private LocalDate nextMaintainTime;
}
