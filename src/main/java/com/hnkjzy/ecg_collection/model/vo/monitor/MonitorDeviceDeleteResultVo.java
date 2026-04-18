package com.hnkjzy.ecg_collection.model.vo.monitor;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 设备删除返回。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorDeviceDeleteResultVo extends BaseVo {

    private Long deviceId;
    private Boolean deleted;
}
