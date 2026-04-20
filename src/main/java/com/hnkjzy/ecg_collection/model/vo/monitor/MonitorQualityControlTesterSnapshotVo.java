package com.hnkjzy.ecg_collection.model.vo.monitor;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 质控详情-测试人员快照。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorQualityControlTesterSnapshotVo extends BaseVo {

    private Long testUserId;
    private String testUserName;
}
