package com.hnkjzy.ecg_collection.model.dto.monitor;

import com.hnkjzy.ecg_collection.model.dto.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 加入重点监护请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorAddKeyDto extends BaseDto {

    private Long patientId;
    private String monitorType;
}
