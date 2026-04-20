package com.hnkjzy.ecg_collection.service.monitor;

import com.hnkjzy.ecg_collection.model.dto.monitor.MonitorQualityControlCreateDto;
import com.hnkjzy.ecg_collection.model.dto.monitor.MonitorQualityControlPageQueryDto;
import com.hnkjzy.ecg_collection.model.dto.monitor.MonitorQualityControlUpdateDto;
import com.hnkjzy.ecg_collection.model.vo.common.DictOptionVo;
import com.hnkjzy.ecg_collection.model.vo.common.PageResultVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorQualityControlDeleteResultVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorQualityControlDetailVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorQualityControlDictVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorQualityControlPageItemVo;
import com.hnkjzy.ecg_collection.service.BaseService;

import java.util.List;

/**
 * 实时监护-质控管理服务。
 */
public interface MonitorQualityControlService extends BaseService {

    PageResultVo<MonitorQualityControlPageItemVo> pageQualityControls(MonitorQualityControlPageQueryDto queryDto);

    List<DictOptionVo> getNormalDeviceOptions();

    MonitorQualityControlDictVo getQualityControlDicts();

    MonitorQualityControlDetailVo createQualityControl(MonitorQualityControlCreateDto createDto);

    MonitorQualityControlDetailVo updateQualityControl(MonitorQualityControlUpdateDto updateDto);

    MonitorQualityControlDeleteResultVo deleteQualityControl(Long qcId);

    MonitorQualityControlDetailVo getQualityControlDetail(Long qcId);
}
