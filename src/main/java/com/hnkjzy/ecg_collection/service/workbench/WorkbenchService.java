package com.hnkjzy.ecg_collection.service.workbench;

import com.hnkjzy.ecg_collection.model.dto.workbench.WorkbenchPageQueryDto;
import com.hnkjzy.ecg_collection.model.dto.workbench.WorkbenchTimeQueryDto;
import com.hnkjzy.ecg_collection.model.vo.workbench.WorkbenchAlertDetailVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.WorkbenchDictVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.WorkbenchEcgDetailVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.WorkbenchLatestEcgItemVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.WorkbenchOverviewStatVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.WorkbenchPageListVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.WorkbenchPendingAlertItemVo;
import com.hnkjzy.ecg_collection.service.BaseService;

/**
 * Workbench service.
 */
public interface WorkbenchService extends BaseService {

    WorkbenchOverviewStatVo getOverview(WorkbenchTimeQueryDto queryDto);

    WorkbenchPageListVo<WorkbenchPendingAlertItemVo> pagePendingAlerts(WorkbenchPageQueryDto queryDto);

    WorkbenchPageListVo<WorkbenchLatestEcgItemVo> pageLatestEcg(WorkbenchPageQueryDto queryDto);

    WorkbenchAlertDetailVo getAlertDetail(Long alertId);

    WorkbenchEcgDetailVo getEcgDetail(Long ecgId);

    WorkbenchDictVo getDicts();
}
