package com.hnkjzy.ecg_collection.model.vo.monitor;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 连续监护记录。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorContinuousRecordVo extends BaseVo {

    private Long recordId;
    private String ecgNo;
    private LocalDateTime collectionStartTime;
    private LocalDateTime collectionEndTime;
    private String deviceName;
    private Integer aiAnalysisStatus;
    private String aiAnalysisStatusText;
    private Integer reportStatus;
    private String reportStatusText;
    private Integer displayStatus;
    private String displayStatusText;
    private String aiConclusionShort;
}
