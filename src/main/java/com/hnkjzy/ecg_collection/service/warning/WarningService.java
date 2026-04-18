package com.hnkjzy.ecg_collection.service.warning;

import com.hnkjzy.ecg_collection.model.dto.warning.WarningHandleDto;
import com.hnkjzy.ecg_collection.model.dto.warning.WarningPageQueryDto;
import com.hnkjzy.ecg_collection.model.vo.common.PageResultVo;
import com.hnkjzy.ecg_collection.model.vo.warning.WarningDetailVo;
import com.hnkjzy.ecg_collection.model.vo.warning.WarningDictVo;
import com.hnkjzy.ecg_collection.model.vo.warning.WarningGlobalStatVo;
import com.hnkjzy.ecg_collection.model.vo.warning.WarningHandleResultVo;
import com.hnkjzy.ecg_collection.model.vo.warning.WarningPageItemVo;
import com.hnkjzy.ecg_collection.service.BaseService;

/**
 * 预警模块服务。
 */
public interface WarningService extends BaseService {

    WarningGlobalStatVo getGlobalStat();

    PageResultVo<WarningPageItemVo> pageWarningList(WarningPageQueryDto queryDto);

    WarningDetailVo getWarningDetail(Long alertId);

    WarningHandleResultVo handleWarning(WarningHandleDto handleDto);

    WarningDictVo getWarningDicts();
}
