package com.hnkjzy.ecg_collection.model.vo.workbench;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import com.hnkjzy.ecg_collection.model.vo.common.DictOptionVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * ECG data list module dicts.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EcgDataDictVo extends BaseVo {

    private List<DictOptionVo> wardOptions;
    private List<DictOptionVo> deviceOptions;
    private List<DictOptionVo> statusOptions;
}
