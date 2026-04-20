package com.hnkjzy.ecg_collection.model.vo.workbench;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * Latest ECG page item.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WorkbenchLatestEcgItemVo extends BaseVo {

    private Long ecgId;
    private LocalDateTime collectTime;
    private String patientInfo;
    private String ward;
    private String deviceNo;
    private String status;
    private String aiConclusion;
}
