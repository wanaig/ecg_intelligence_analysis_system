package com.hnkjzy.ecg_collection.model.vo.monitor;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * AI 诊断准确率。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorAiAccuracyVo extends BaseVo {

    private BigDecimal accuracyRate;
}
