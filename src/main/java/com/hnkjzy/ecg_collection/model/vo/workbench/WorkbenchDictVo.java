package com.hnkjzy.ecg_collection.model.vo.workbench;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import com.hnkjzy.ecg_collection.model.vo.common.DictOptionVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Workbench filter dict response.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WorkbenchDictVo extends BaseVo {

    private List<DictOptionVo> timeTypeOptions;
    private List<DictOptionVo> alertLevelOptions;
    private List<DictOptionVo> alertStatusOptions;
    private List<DictOptionVo> deviceTypeOptions;
}
