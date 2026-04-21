package com.hnkjzy.ecg_collection.model.vo.analysis;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AI diagnosis engine running status.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AiDiagnosisEngineStatusVo extends BaseVo {

    private String engineStatus;
    private String engineStatusText;
    private String engineVersion;
    private Long runningInstanceCount;
    private Long queueBacklogCount;
    private Long todayAnalysisCount;
    private BigDecimal avgAnalysisSeconds;
    private LocalDateTime lastHeartbeatTime;
    private String statusMessage;
}
