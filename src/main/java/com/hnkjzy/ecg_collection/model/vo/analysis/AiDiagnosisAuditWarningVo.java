package com.hnkjzy.ecg_collection.model.vo.analysis;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * AI diagnosis warning snapshot for audit detail.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AiDiagnosisAuditWarningVo extends BaseVo {

    private Long warningId;
    private String warningType;
    private Integer warningLevel;
    private String warningLevelText;
    private Integer handleStatus;
    private String handleStatusText;
    private LocalDateTime warningTime;
    private String warningDesc;
    private String handleOpinion;
}
