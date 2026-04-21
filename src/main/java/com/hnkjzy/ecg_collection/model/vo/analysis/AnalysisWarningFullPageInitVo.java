package com.hnkjzy.ecg_collection.model.vo.analysis;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import com.hnkjzy.ecg_collection.model.vo.common.DictOptionVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Full warning page init payload.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AnalysisWarningFullPageInitVo extends BaseVo {

    private Long highRiskCount;
    private Long pendingHandleCount;
    private List<DictOptionVo> wardOptions;
    private List<DictOptionVo> alertLevelOptions;
    private List<DictOptionVo> alertStatusOptions;
    private AnalysisDashboardPageResultVo<AnalysisWarningFullPageItemVo> pageData;
}
