package com.hnkjzy.ecg_collection.model.dto.monitor;

import com.hnkjzy.ecg_collection.model.dto.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 新增质控记录请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorQualityControlCreateDto extends BaseDto {

    private Long deviceId;
    private String testType;
    private Long testUserId;
    private String testUserName;
    private String testResult;
    private String deviceStatus;
    private String testTime;
    private String remark;
}
