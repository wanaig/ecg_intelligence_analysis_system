package com.hnkjzy.ecg_collection.model.vo.workbench;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ECG data status update result.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EcgDataStatusUpdateResultVo extends BaseVo {

    private Long ecgId;
    private String targetStatus;
    private Boolean updated;
}
