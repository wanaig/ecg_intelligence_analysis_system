package com.hnkjzy.ecg_collection.model.vo.patient;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import com.hnkjzy.ecg_collection.model.vo.common.DictOptionVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 患者筛选字典。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PatientDictVo extends BaseVo {

    private List<DictOptionVo> wardOptions;
    private List<DictOptionVo> riskLevelOptions;
    private List<DictOptionVo> patientStatusOptions;
}
