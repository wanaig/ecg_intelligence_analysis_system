package com.hnkjzy.ecg_collection.model.dto.warning;

import com.hnkjzy.ecg_collection.model.dto.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 处理预警请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WarningHandleDto extends BaseDto {

    private Long alertId;
    private String handleRemark;
}
