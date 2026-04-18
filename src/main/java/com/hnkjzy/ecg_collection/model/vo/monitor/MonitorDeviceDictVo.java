package com.hnkjzy.ecg_collection.model.vo.monitor;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import com.hnkjzy.ecg_collection.model.vo.common.DictOptionVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 设备筛选字典。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorDeviceDictVo extends BaseVo {

    private List<DictOptionVo> deviceTypeOptions;
    private List<DictOptionVo> wardOptions;
}
