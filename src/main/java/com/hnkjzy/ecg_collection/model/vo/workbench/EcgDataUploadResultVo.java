package com.hnkjzy.ecg_collection.model.vo.workbench;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * ECG data upload result.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EcgDataUploadResultVo extends BaseVo {

    private Long ecgId;
    private String ecgNo;
    private String status;
    private Boolean aiQueueTriggered;
    private LocalDateTime uploadTime;
}
