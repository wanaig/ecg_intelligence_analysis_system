package com.hnkjzy.ecg_collection.mapper.analysis;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnkjzy.ecg_collection.mapper.BaseMapperX;
import com.hnkjzy.ecg_collection.model.dto.analysis.AnalysisWarningFullPageQueryDto;
import com.hnkjzy.ecg_collection.model.entity.analysis.EcgStatisticsResultEntity;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisCoreMetricsVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisDashboardCoreMetricsVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisDeviceUsageStatVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisLatestEcgPageItemVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisWarningDetailVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisWarningFullPageItemVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisPendingWarningPageItemVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisReportStatusStatVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisWardMeasureStatVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisWardWarningTopItemVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisWarningDailyCountVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisWarningLevelDistributionVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisWarningLevelStatVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisWarningTypeRatioItemVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisWarningTypeStatVo;
import com.hnkjzy.ecg_collection.model.vo.common.DictOptionVo;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 数据分析 Mapper。
 */
public interface AnalysisMapper extends BaseMapperX<EcgStatisticsResultEntity> {

    AnalysisDashboardCoreMetricsVo selectDashboardCoreMetrics(@Param("startTime") LocalDateTime startTime,
                                                              @Param("endTime") LocalDateTime endTime);

    AnalysisWarningLevelDistributionVo selectWarningLevelDistribution(@Param("startTime") LocalDateTime startTime,
                                                                      @Param("endTime") LocalDateTime endTime);

    List<AnalysisWarningTypeRatioItemVo> selectWarningTypeCounts(@Param("startTime") LocalDateTime startTime,
                                                                 @Param("endTime") LocalDateTime endTime);

    List<AnalysisWardWarningTopItemVo> selectWardWarningTop(@Param("startTime") LocalDateTime startTime,
                                                            @Param("endTime") LocalDateTime endTime);

    List<AnalysisWarningDailyCountVo> selectWarningDailyCounts(@Param("startTime") LocalDateTime startTime,
                                                               @Param("endTime") LocalDateTime endTime);

    IPage<AnalysisPendingWarningPageItemVo> selectPendingWarningPage(Page<AnalysisPendingWarningPageItemVo> page,
                                                                     @Param("startTime") LocalDateTime startTime,
                                                                     @Param("endTime") LocalDateTime endTime);

    IPage<AnalysisLatestEcgPageItemVo> selectLatestEcgPage(Page<AnalysisLatestEcgPageItemVo> page,
                                                           @Param("startTime") LocalDateTime startTime,
                                                           @Param("endTime") LocalDateTime endTime);

    AnalysisWarningDetailVo selectDashboardWarningDetail(@Param("alertId") Long alertId);

    IPage<AnalysisWarningFullPageItemVo> selectDashboardFullWarningPage(Page<AnalysisWarningFullPageItemVo> page,
                                                                        @Param("req") AnalysisWarningFullPageQueryDto req,
                                                                        @Param("alertLevelCode") Integer alertLevelCode,
                                                                        @Param("alertStatusCode") Integer alertStatusCode,
                                                                        @Param("startTime") LocalDateTime startTime,
                                                                        @Param("endTime") LocalDateTime endTime);

    Long countDashboardHighRiskWarnings(@Param("startTime") LocalDateTime startTime,
                                        @Param("endTime") LocalDateTime endTime);

    Long countDashboardPendingHandleWarnings(@Param("startTime") LocalDateTime startTime,
                                             @Param("endTime") LocalDateTime endTime);

    AnalysisCoreMetricsVo selectCoreMetrics(@Param("startTime") LocalDateTime startTime,
                                            @Param("endTime") LocalDateTime endTime,
                                            @Param("wardId") Long wardId);

    List<AnalysisWardMeasureStatVo> selectWardMeasureStats();

    List<AnalysisWarningLevelStatVo> selectWarningLevelStats();

    List<AnalysisWarningTypeStatVo> selectWarningTypeStats();

    List<AnalysisReportStatusStatVo> selectReportStatusStats();

    List<AnalysisDeviceUsageStatVo> selectDeviceUsageStats();

    List<DictOptionVo> selectWardOptions();
}
