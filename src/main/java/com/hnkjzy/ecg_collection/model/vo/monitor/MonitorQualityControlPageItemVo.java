package com.hnkjzy.ecg_collection.model.vo.monitor;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 质控记录分页项。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorQualityControlPageItemVo extends BaseVo {

    private Long qcId;
    private String qcNo;
    private Long deviceId;
    private String deviceName;
    private Long deptId;
    private String deptName;
    private LocalDateTime testTime;
    private String testType;
    private Long testUserId;
    private String testUserName;
    private String deviceStatus;
    private String testResult;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<String> actionPermissions;
}
