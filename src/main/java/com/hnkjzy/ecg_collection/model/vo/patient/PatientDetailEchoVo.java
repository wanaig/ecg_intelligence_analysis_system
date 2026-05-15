package com.hnkjzy.ecg_collection.model.vo.patient;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 患者详情回响VO（患者信息 + 心电统计聚合）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PatientDetailEchoVo extends BaseVo {

    /**
     * 患者ID
     */
    private Long id;

    /**
     * 患者姓名
     */
    private String name;

    /**
     * 性别枚举（MALE/FEMALE）
     */
    private String gender;

    /**
     * 性别文本（男/女）
     */
    private String genderText;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 住院号/居家编号
     */
    private String inpatientNo;

    /**
     * 病区名称
     */
    private String ward;

    /**
     * 床位号
     */
    private String bedNo;

    /**
     * 患者状态枚举（IN_HOSPITAL/DISCHARGED/HOME_FOLLOW）
     */
    private String status;

    /**
     * 患者状态文本（在院/出院/居家随访）
     */
    private String statusText;

    /**
     * 风险等级枚举（LOW/MEDIUM/MEDIUM_HIGH/HIGH）
     */
    private String riskLevel;

    /**
     * 风险等级文本（低危/中危/中高危/高危）
     */
    private String riskLevelText;

    /**
     * 设备编号
     */
    private String deviceNo;

    /**
     * 入院时间
     */
    private LocalDateTime admissionTime;

    /**
     * 出院时间
     */
    private LocalDateTime dischargeTime;

    /**
     * 主要诊断
     */
    private String diagnosis;

    /**
     * 心电采集次数
     */
    private Integer ecgCount;

    /**
     * 最新采集时间
     */
    private LocalDateTime lastEcgTime;
}
