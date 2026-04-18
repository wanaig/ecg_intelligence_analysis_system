package com.hnkjzy.ecg_collection.model.vo.patient;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 患者分页列表项。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PatientPageItemVo extends BaseVo {

    private Long patientId;
    private String patientName;
    private Integer gender;
    private String genderText;
    private Integer age;
    private String inpatientNo;
    private Long wardId;
    private String wardName;
    private Long bedId;
    private String bedNo;
    private Integer riskLevel;
    private String riskLevelText;
    private Integer patientStatus;
    private String patientStatusText;
    private String primaryDiagnosis;
    private Integer ecgCount;
    private LocalDateTime latestEcgTime;
}
