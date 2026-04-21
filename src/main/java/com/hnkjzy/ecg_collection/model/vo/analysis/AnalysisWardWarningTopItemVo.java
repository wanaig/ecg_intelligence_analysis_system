package com.hnkjzy.ecg_collection.model.vo.analysis;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 病区预警 TOP 项。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AnalysisWardWarningTopItemVo extends BaseVo {

    private String wardName;
    private Long warningCount;
}
