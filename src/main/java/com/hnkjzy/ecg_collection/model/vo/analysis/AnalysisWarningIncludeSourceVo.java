package com.hnkjzy.ecg_collection.model.vo.analysis;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 预警纳入时的源数据快照。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AnalysisWarningIncludeSourceVo extends BaseVo {

    private Long warningId;
    private Long patientId;
    private Long recordId;
    private Long aiDiagnosisId;
    private Long deptId;
    private String deptName;
    private String patientName;
    private Integer gender;
    private Integer age;
    private String inpatientNo;
    private String bedNo;
    private Long deviceId;
    private String deviceName;
    private Integer warningLevel;
    private String primaryDiagnosis;
    private String warningDesc;
    private String aiConclusion;
    private LocalDateTime collectionTime;
    private LocalDateTime warningTime;
}
