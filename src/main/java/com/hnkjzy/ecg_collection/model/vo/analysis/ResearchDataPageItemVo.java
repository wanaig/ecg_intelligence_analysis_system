package com.hnkjzy.ecg_collection.model.vo.analysis;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * Research data page item.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ResearchDataPageItemVo extends BaseVo {

    private Long rowNum;
    private Long researchId;
    private String patientName;
    private Integer gender;
    private String genderText;
    private Integer age;
    private String inpatientNo;
    private Long deptId;
    private String deptName;
    private String mainEmrDiagnosis;
    private String ecgFeatureSummary;
    private LocalDateTime collectionTime;
    private Integer isDataApproved;
    private String isDataApprovedText;
    private Integer isExported;
    private String isExportedText;
}
