package com.hnkjzy.ecg_collection.model.vo.monitor;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 实时监护大屏列表响应。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorScreenListVo extends BaseVo {

    private List<MonitorPatientCardVo> list;
}
