package com.hnkjzy.ecg_collection.model.dto.patient;

import com.hnkjzy.ecg_collection.model.dto.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 患者信息修改请求（匹配前端字段名）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PatientUpdateDto extends BaseDto {

    /**
     * 患者姓名
     */
    private String name;

    /**
     * 性别枚举：MALE-男 FEMALE-女
     */
    private String gender;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 病区名称
     */
    private String ward;

    /**
     * 床位号
     */
    private String bedNo;

    /**
     * 设备编号
     */
    private String deviceNo;

    /**
     * 住院号/居家编号
     */
    private String inpatientNo;

    /**
     * 风险等级枚举：LOW-低危 MEDIUM-中危 MEDIUM_HIGH-中高危 HIGH-高危
     */
    private String riskLevel;

    /**
     * 患者状态枚举：IN_HOSPITAL-住院中 DISCHARGED-出院 HOME_FOLLOW-居家随访
     */
    private String status;

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
}
