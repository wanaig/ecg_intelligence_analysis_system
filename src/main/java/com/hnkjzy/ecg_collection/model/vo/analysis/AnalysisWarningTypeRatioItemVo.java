package com.hnkjzy.ecg_collection.model.vo.analysis;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 预警类型占比项。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AnalysisWarningTypeRatioItemVo extends BaseVo {

    private String warningType;
    private Long count;
    private BigDecimal ratio;
}
