package com.hnkjzy.ecg_collection.model.entity.patient;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 患者信息实体。
 */
@Data
@TableName("patient_info")
public class PatientInfoEntity {

    @TableId("patient_id")
    private Long patientId;

    @TableField("patient_name")
    private String patientName;

    @TableField("gender")
    private Integer gender;

    @TableField("birth_date")
    private LocalDate birthDate;

    @TableField("age")
    private Integer age;

    @TableField("id_card")
    private String idCard;

    @TableField("inpatient_no")
    private String inpatientNo;

    @TableField("ward_id")
    private Long wardId;

    @TableField("bed_id")
    private Long bedId;

    @TableField("device_id")
    private Long deviceId;

    @TableField("phone")
    private String phone;

    @TableField("admission_time")
    private LocalDateTime admissionTime;

    @TableField("discharge_time")
    private LocalDateTime dischargeTime;

    @TableField("primary_diagnosis")
    private String primaryDiagnosis;

    @TableField("risk_level")
    private Integer riskLevel;

    @TableField("patient_type")
    private Integer patientType;

    @TableField("patient_status")
    private Integer patientStatus;

    @TableField("follow_up_status")
    private Integer followUpStatus;

    @TableField("ecg_count")
    private Integer ecgCount;

    @TableField("latest_ecg_time")
    private LocalDateTime latestEcgTime;

    @TableField("is_deleted")
    private Integer isDeleted;
}
