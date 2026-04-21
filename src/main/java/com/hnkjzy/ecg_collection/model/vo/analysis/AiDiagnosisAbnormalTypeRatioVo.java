package com.hnkjzy.ecg_collection.model.vo.analysis;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * AI diagnosis abnormal type ratio item.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AiDiagnosisAbnormalTypeRatioVo extends BaseVo {

    private String abnormalType;
    private Long count;
    private BigDecimal ratio;
}
