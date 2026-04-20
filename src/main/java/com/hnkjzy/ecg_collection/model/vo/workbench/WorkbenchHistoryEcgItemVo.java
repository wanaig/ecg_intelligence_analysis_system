package com.hnkjzy.ecg_collection.model.vo.workbench;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * History ECG record in alert detail.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WorkbenchHistoryEcgItemVo extends BaseVo {

    private Long ecgId;
    private String ecgNo;
    private LocalDateTime collectTime;
    private String deviceName;
    private String status;
    private String aiConclusion;
}
