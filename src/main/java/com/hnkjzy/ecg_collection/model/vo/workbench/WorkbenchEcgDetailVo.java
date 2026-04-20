package com.hnkjzy.ecg_collection.model.vo.workbench;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Workbench ECG detail.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WorkbenchEcgDetailVo extends BaseVo {

    private Long ecgId;
    private String ecgNo;

    private Long patientId;
    private String patientName;
    private String gender;
    private Integer age;
    private String inpatientNo;
    private Long wardId;
    private String wardName;
    private Long bedId;
    private String bedNo;
    private String primaryDiagnosis;

    private Long deviceId;
    private String deviceName;
    private Integer leadCount;
    private Integer samplingRate;
    private Integer collectionDuration;
    private String collectionType;
    private LocalDateTime collectStartTime;
    private LocalDateTime collectEndTime;

    private Integer aiAnalysisStatus;
    private Integer reportStatus;
    private Integer displayStatus;
    private String status;

    private String aiConclusion;
    private String aiConclusionShort;
    private String abnormalType;
    private BigDecimal aiConfidence;

    private String rawDataFileUrl;
    private String uploadSourceFileUrl;
    private List<WorkbenchWaveformPointVo> waveformPreview;
}
