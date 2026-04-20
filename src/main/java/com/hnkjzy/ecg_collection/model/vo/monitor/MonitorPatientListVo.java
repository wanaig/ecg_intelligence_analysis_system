package com.hnkjzy.ecg_collection.model.vo.monitor;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 监护患者列表响应。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorPatientListVo extends BaseVo {

    private List<MonitorPatientListItemVo> list;
}
