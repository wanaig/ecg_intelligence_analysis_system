package com.hnkjzy.ecg_collection.model.vo.analysis;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AI diagnosis center page item.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AiDiagnosisPageItemVo extends BaseVo {

    private String diagnosisId;
    private Long ecgId;
    private String ecgNo;
    private String patientInfo;
    private String hospitalNo;
    private String deptName;
    private String aiVersion;
    private String aiConclusion;
    private Integer abnormalCount;
    private BigDecimal confidence;
    private LocalDateTime diagnosisTime;
    private String status;
}
