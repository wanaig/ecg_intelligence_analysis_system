package com.hnkjzy.ecg_collection.model.dto.monitor;

import com.hnkjzy.ecg_collection.model.dto.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 解除重点监护请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorCancelKeyDto extends BaseDto {

    private Long patientId;
}
