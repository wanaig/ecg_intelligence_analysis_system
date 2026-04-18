package com.hnkjzy.ecg_collection.model.vo.monitor;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import com.hnkjzy.ecg_collection.model.vo.common.DictOptionVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 质控筛选字典。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorQualityControlDictVo extends BaseVo {

    private List<DictOptionVo> deviceOptions;
    private List<DictOptionVo> testStatusOptions;
    private List<DictOptionVo> testTypeOptions;
}
