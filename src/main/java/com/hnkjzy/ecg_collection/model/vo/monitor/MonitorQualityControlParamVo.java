package com.hnkjzy.ecg_collection.model.vo.monitor;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 质控详情-质控参数。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorQualityControlParamVo extends BaseVo {

    private String testType;
    private String deviceStatus;
    private String testResult;
}
