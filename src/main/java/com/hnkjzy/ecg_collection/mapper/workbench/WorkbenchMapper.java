package com.hnkjzy.ecg_collection.mapper.workbench;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnkjzy.ecg_collection.model.vo.workbench.WorkbenchAlertDetailVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.WorkbenchEcgDetailVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.WorkbenchHistoryEcgItemVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.WorkbenchLatestEcgItemVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.WorkbenchOverviewStatVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.WorkbenchPendingAlertItemVo;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Workbench mapper.
 */
public interface WorkbenchMapper {

    WorkbenchOverviewStatVo selectOverview(@Param("startTime") LocalDateTime startTime,
                                           @Param("endTime") LocalDateTime endTime);

    IPage<WorkbenchPendingAlertItemVo> selectPendingAlertPage(Page<WorkbenchPendingAlertItemVo> page,
                                                              @Param("startTime") LocalDateTime startTime,
                                                              @Param("endTime") LocalDateTime endTime);

    IPage<WorkbenchLatestEcgItemVo> selectLatestEcgPage(Page<WorkbenchLatestEcgItemVo> page,
                                                        @Param("startTime") LocalDateTime startTime,
                                                        @Param("endTime") LocalDateTime endTime);

    WorkbenchAlertDetailVo selectAlertDetail(@Param("alertId") Long alertId);

    List<WorkbenchHistoryEcgItemVo> selectAlertHistoryEcg(@Param("patientId") Long patientId);

    WorkbenchEcgDetailVo selectEcgDetail(@Param("ecgId") Long ecgId);
}
