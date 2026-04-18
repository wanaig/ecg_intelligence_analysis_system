package com.hnkjzy.ecg_collection.model.vo.analysis;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import com.hnkjzy.ecg_collection.model.vo.common.DictOptionVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 数据分析筛选字典。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AnalysisDictVo extends BaseVo {

    private List<DictOptionVo> wardOptions;
    private List<DictOptionVo> timeTypeOptions;
}
