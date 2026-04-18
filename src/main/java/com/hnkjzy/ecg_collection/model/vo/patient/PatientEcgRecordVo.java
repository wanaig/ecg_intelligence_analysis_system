package com.hnkjzy.ecg_collection.model.vo.patient;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 患者心电记录。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PatientEcgRecordVo extends BaseVo {

    private Long recordId;
    private String ecgNo;
    private String deviceName;
    private LocalDateTime collectionStartTime;
    private LocalDateTime collectionEndTime;
    private Integer aiAnalysisStatus;
    private String aiAnalysisStatusText;
    private Integer reportStatus;
    private String reportStatusText;
    private Integer displayStatus;
    private String displayStatusText;
    private String aiConclusionShort;
}
