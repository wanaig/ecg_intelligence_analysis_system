package com.hnkjzy.ecg_collection.model.vo.patient;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 患者页顶部统计。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PatientDashboardStatVo extends BaseVo {

    private Long totalPatient;
    private Long inHospital;
    private Long homeFollow;
    private Long highRisk;
}
