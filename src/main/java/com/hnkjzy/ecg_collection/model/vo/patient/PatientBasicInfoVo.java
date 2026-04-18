package com.hnkjzy.ecg_collection.model.vo.patient;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 患者基础信息。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PatientBasicInfoVo extends BaseVo {

    private Long patientId;
    private String patientName;
    private Integer gender;
    private String genderText;
    private LocalDate birthDate;
    private Integer age;
    private String idCard;
    private String inpatientNo;
    private Long wardId;
    private String wardName;
    private Long bedId;
    private String bedNo;
    private Long deviceId;
    private String phone;
    private LocalDateTime admissionTime;
    private LocalDateTime dischargeTime;
    private String primaryDiagnosis;
    private Integer riskLevel;
    private String riskLevelText;
    private Integer patientType;
    private Integer patientStatus;
    private String patientStatusText;
    private Integer followUpStatus;
    private Integer ecgCount;
    private LocalDateTime latestEcgTime;
}
