package com.hnkjzy.ecg_collection.model.vo.patient;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 患者详情。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PatientDetailVo extends BaseVo {

    private PatientBasicInfoVo basicInfo;
    private List<PatientEcgRecordVo> ecgRecords;
    private List<PatientWarningVo> warningHistory;
    private List<PatientDiagnosisVo> diagnosisInfo;
}
