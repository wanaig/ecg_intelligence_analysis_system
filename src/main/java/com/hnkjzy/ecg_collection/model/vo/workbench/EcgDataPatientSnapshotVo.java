package com.hnkjzy.ecg_collection.model.vo.workbench;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Patient snapshot for ECG upload.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EcgDataPatientSnapshotVo extends BaseVo {

    private Long patientId;
    private String patientName;
    private String gender;
    private Integer age;
    private String inpatientNo;
    private Long wardId;
    private String wardName;
    private String bedNo;
    private String primaryDiagnosis;
}
