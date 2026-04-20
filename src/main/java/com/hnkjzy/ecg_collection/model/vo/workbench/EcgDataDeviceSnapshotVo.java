package com.hnkjzy.ecg_collection.model.vo.workbench;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Device snapshot for ECG upload.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EcgDataDeviceSnapshotVo extends BaseVo {

    private Long deviceId;
    private String deviceCode;
    private String deviceName;
    private Integer deviceType;
}
