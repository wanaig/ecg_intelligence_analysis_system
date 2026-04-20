package com.hnkjzy.ecg_collection.model.vo.monitor;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 质控详情-时间快照。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorQualityControlTimeSnapshotVo extends BaseVo {

    private LocalDateTime testTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
