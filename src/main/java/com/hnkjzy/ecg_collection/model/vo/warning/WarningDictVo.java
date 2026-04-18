package com.hnkjzy.ecg_collection.model.vo.warning;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import com.hnkjzy.ecg_collection.model.vo.common.DictOptionVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 预警筛选字典。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WarningDictVo extends BaseVo {

    private List<DictOptionVo> alertLevelOptions;
    private List<DictOptionVo> alertStatusOptions;
}
