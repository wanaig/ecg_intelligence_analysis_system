package com.hnkjzy.ecg_collection.mapper.analysis;

import com.hnkjzy.ecg_collection.mapper.BaseMapperX;
import com.hnkjzy.ecg_collection.model.entity.analysis.EcgStatisticsResultEntity;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisCoreMetricsVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisDeviceUsageStatVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisReportStatusStatVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisWardMeasureStatVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisWarningLevelStatVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisWarningTypeStatVo;
import com.hnkjzy.ecg_collection.model.vo.common.DictOptionVo;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 数据分析 Mapper。
 */
public interface AnalysisMapper extends BaseMapperX<EcgStatisticsResultEntity> {

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
