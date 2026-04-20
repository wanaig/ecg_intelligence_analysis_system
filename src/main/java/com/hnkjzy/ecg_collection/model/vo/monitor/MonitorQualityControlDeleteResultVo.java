package com.hnkjzy.ecg_collection.model.vo.monitor;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 质控记录删除返回。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorQualityControlDeleteResultVo extends BaseVo {

    private Long qcId;
    private Boolean deleted;
}
