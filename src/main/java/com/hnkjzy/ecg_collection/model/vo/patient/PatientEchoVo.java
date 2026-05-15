package com.hnkjzy.ecg_collection.model.vo.patient;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 患者详情VO（数据回响/详情展示）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PatientEchoVo extends BaseVo {

    private Long patientId;

    private String patientName;

    private Integer gender;

    private String genderName;

    private Integer age;

    private String inpatientNo;

    private Long wardId;

    private String wardName;

    private Long bedId;

    private String bedNo;

    private Integer patientStatus;

    private String patientStatusName;

    private Integer riskLevel;

    private String riskLevelName;

    private Long deviceId;

    private String deviceName;

    private Integer ecgCount;

    private LocalDateTime latestEcgTime;
}
