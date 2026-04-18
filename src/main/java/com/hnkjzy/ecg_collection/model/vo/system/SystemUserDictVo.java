package com.hnkjzy.ecg_collection.model.vo.system;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import com.hnkjzy.ecg_collection.model.vo.common.DictOptionVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 用户筛选字典。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SystemUserDictVo extends BaseVo {

    private List<DictOptionVo> roleOptions;
    private List<DictOptionVo> departmentOptions;
    private List<DictOptionVo> statusOptions;
}
