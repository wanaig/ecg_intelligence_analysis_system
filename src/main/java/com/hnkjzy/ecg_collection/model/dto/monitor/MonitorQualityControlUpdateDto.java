package com.hnkjzy.ecg_collection.model.dto.monitor;

import com.hnkjzy.ecg_collection.model.dto.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 编辑质控记录请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorQualityControlUpdateDto extends BaseDto {

    private Long qcId;
    private Long deviceId;
    private String testType;
    private Long testUserId;
    private String testUserName;
    private String testResult;
    private String deviceStatus;
    private String testTime;
    private String remark;
}
