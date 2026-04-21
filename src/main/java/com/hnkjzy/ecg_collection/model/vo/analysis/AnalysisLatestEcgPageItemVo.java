package com.hnkjzy.ecg_collection.model.vo.analysis;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 最新心电记录分页项。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AnalysisLatestEcgPageItemVo extends BaseVo {

    private Long recordId;
    private String ecgNo;
    private Long patientId;
    private String patientName;
    private String gender;
    private Integer age;
    private String inpatientNo;
    private Long deptId;
    private String deptName;
    private String bedNo;
    private Long deviceId;
    private String deviceName;
    private Integer leadCount;
    private Integer samplingRate;
    private Integer collectionDuration;
    private Integer collectionType;
    private String collectionTypeText;
    private LocalDateTime collectStartTime;
    private LocalDateTime collectEndTime;
    private Integer aiAnalysisStatus;
    private Integer reportStatus;
    private Integer displayStatus;
    private String statusText;
    private String aiConclusionShort;
    private String aiConclusion;
}
