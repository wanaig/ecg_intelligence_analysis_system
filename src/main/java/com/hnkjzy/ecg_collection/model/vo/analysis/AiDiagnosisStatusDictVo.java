package com.hnkjzy.ecg_collection.model.vo.analysis;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import com.hnkjzy.ecg_collection.model.vo.common.DictOptionVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * AI diagnosis status dict response.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AiDiagnosisStatusDictVo extends BaseVo {

    private List<DictOptionVo> statusList;
}
