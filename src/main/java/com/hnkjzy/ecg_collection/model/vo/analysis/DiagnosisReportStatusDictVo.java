package com.hnkjzy.ecg_collection.model.vo.analysis;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import com.hnkjzy.ecg_collection.model.vo.common.DictOptionVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Diagnosis report status dictionary response.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DiagnosisReportStatusDictVo extends BaseVo {

    private List<DictOptionVo> statusList;
}
