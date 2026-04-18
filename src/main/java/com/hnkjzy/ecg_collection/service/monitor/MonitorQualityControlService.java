package com.hnkjzy.ecg_collection.service.monitor;

import com.hnkjzy.ecg_collection.model.dto.monitor.MonitorQualityControlCreateDto;
import com.hnkjzy.ecg_collection.model.dto.monitor.MonitorQualityControlPageQueryDto;
import com.hnkjzy.ecg_collection.model.dto.monitor.MonitorQualityControlUpdateDto;
import com.hnkjzy.ecg_collection.model.vo.common.PageResultVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorQualityControlDetailVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorQualityControlDictVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorQualityControlPageItemVo;
import com.hnkjzy.ecg_collection.service.BaseService;

/**
 * 实时监护-质控管理服务。
 */
public interface MonitorQualityControlService extends BaseService {

    PageResultVo<MonitorQualityControlPageItemVo> pageQualityControls(MonitorQualityControlPageQueryDto queryDto);

    MonitorQualityControlDictVo getQualityControlDicts();

    MonitorQualityControlDetailVo createQualityControl(MonitorQualityControlCreateDto createDto);

    MonitorQualityControlDetailVo updateQualityControl(MonitorQualityControlUpdateDto updateDto);

    MonitorQualityControlDetailVo getQualityControlDetail(Long qcId);
}
