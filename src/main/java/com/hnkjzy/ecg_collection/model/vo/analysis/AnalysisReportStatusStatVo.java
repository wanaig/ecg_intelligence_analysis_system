package com.hnkjzy.ecg_collection.model.vo.analysis;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 报告状态统计项。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AnalysisReportStatusStatVo extends BaseVo {

    private String status;
    private Long count;
}
