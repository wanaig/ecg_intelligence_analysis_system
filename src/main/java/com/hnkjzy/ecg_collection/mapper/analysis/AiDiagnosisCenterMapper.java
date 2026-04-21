package com.hnkjzy.ecg_collection.mapper.analysis;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnkjzy.ecg_collection.model.dto.analysis.AiDiagnosisDashboardPageQueryDto;
import com.hnkjzy.ecg_collection.model.dto.analysis.AiDiagnosisPageQueryDto;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisAbnormalTypeRatioVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisAuditBaseVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisAuditDetailVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisAuditWarningVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisCoreMetricsSnapshotVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisDailyTrendCountVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisDashboardPageItemVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisDetailVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisEngineStatusVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisLiteDetailVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisOverviewVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisPageItemVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisWaveformVo;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * AI diagnosis center mapper.
 */
public interface AiDiagnosisCenterMapper {

    AiDiagnosisEngineStatusVo selectEngineStatus(@Param("startOfDay") LocalDateTime startOfDay,
                                                 @Param("nowTime") LocalDateTime nowTime);

    AiDiagnosisCoreMetricsSnapshotVo selectCoreMetricsSnapshot(@Param("startTime") LocalDateTime startTime,
                                                               @Param("endTime") LocalDateTime endTime);

    List<AiDiagnosisDailyTrendCountVo> selectTrendCounts(@Param("startDate") LocalDate startDate,
                                                         @Param("endDate") LocalDate endDate);

    List<AiDiagnosisAbnormalTypeRatioVo> selectAbnormalTypeRatios(@Param("startTime") LocalDateTime startTime,
                                                                   @Param("endTime") LocalDateTime endTime);

    IPage<AiDiagnosisDashboardPageItemVo> selectDashboardPage(Page<AiDiagnosisDashboardPageItemVo> page,
                                                              @Param("req") AiDiagnosisDashboardPageQueryDto req,
                                                              @Param("startTime") LocalDateTime startTime,
                                                              @Param("endTime") LocalDateTime endTime);

    AiDiagnosisLiteDetailVo selectLiteDetail(@Param("diagnosisId") String diagnosisId);

    AiDiagnosisAuditDetailVo selectAuditDetail(@Param("diagnosisId") String diagnosisId);

    List<AiDiagnosisAuditWarningVo> selectAuditWarnings(@Param("aiDiagnosisId") Long aiDiagnosisId,
                                                        @Param("recordId") Long recordId);

    AiDiagnosisOverviewVo selectOverview();

    IPage<AiDiagnosisPageItemVo> selectDiagnosisPage(Page<AiDiagnosisPageItemVo> page,
                                                     @Param("req") AiDiagnosisPageQueryDto req,
                                                     @Param("statusCode") Integer statusCode);

    AiDiagnosisDetailVo selectDiagnosisDetail(@Param("diagnosisId") String diagnosisId);

    AiDiagnosisWaveformVo selectWaveformMeta(@Param("ecgId") Long ecgId);

    AiDiagnosisAuditBaseVo selectAuditBase(@Param("diagnosisId") String diagnosisId);

    Long selectMaxReportIdInRangeForUpdate(@Param("maxAllowedId") Long maxAllowedId);

    int insertAuditReport(@Param("reportId") Long reportId,
                          @Param("reportNo") String reportNo,
                          @Param("ecgId") Long ecgId,
                          @Param("aiDiagnosisId") Long aiDiagnosisId,
                          @Param("patientId") Long patientId,
                          @Param("patientName") String patientName,
                          @Param("gender") String gender,
                          @Param("age") Integer age,
                          @Param("hospitalNo") String hospitalNo,
                          @Param("collectionTime") LocalDateTime collectionTime,
                          @Param("aiConclusion") String aiConclusion,
                          @Param("doctorConclusion") String doctorConclusion,
                          @Param("doctorId") Long doctorId,
                          @Param("doctorName") String doctorName,
                          @Param("auditTime") LocalDateTime auditTime,
                          @Param("auditOpinion") String auditOpinion,
                          @Param("reportStatus") Integer reportStatus);

    int insertAuditReportV2(@Param("reportId") Long reportId,
                            @Param("reportNo") String reportNo,
                            @Param("ecgId") Long ecgId,
                            @Param("aiDiagnosisId") Long aiDiagnosisId,
                            @Param("patientId") Long patientId,
                            @Param("patientName") String patientName,
                            @Param("gender") String gender,
                            @Param("age") Integer age,
                            @Param("hospitalNo") String hospitalNo,
                            @Param("collectionTime") LocalDateTime collectionTime,
                            @Param("aiConclusion") String aiConclusion,
                            @Param("doctorConclusion") String doctorConclusion,
                            @Param("doctorSuggestion") String doctorSuggestion,
                            @Param("doctorId") Long doctorId,
                            @Param("doctorName") String doctorName,
                            @Param("reportCreateTime") LocalDateTime reportCreateTime,
                            @Param("auditTime") LocalDateTime auditTime,
                            @Param("auditOpinion") String auditOpinion,
                            @Param("reportStatus") Integer reportStatus);

    int updateReportAudit(@Param("reportId") Long reportId,
                          @Param("doctorConclusion") String doctorConclusion,
                          @Param("auditDoctorId") Long auditDoctorId,
                          @Param("auditDoctorName") String auditDoctorName,
                          @Param("auditTime") LocalDateTime auditTime,
                          @Param("auditOpinion") String auditOpinion,
                          @Param("reportStatus") Integer reportStatus);

    int updateReportAuditV2(@Param("reportId") Long reportId,
                            @Param("doctorConclusion") String doctorConclusion,
                            @Param("doctorSuggestion") String doctorSuggestion,
                            @Param("auditDoctorId") Long auditDoctorId,
                            @Param("auditDoctorName") String auditDoctorName,
                            @Param("auditTime") LocalDateTime auditTime,
                            @Param("auditOpinion") String auditOpinion,
                            @Param("reportStatus") Integer reportStatus);

    int updateCollectionReportStatus(@Param("ecgId") Long ecgId,
                                     @Param("reportStatus") Integer reportStatus,
                                     @Param("displayStatus") Integer displayStatus);

    int updateAiDiagnosisAuditSync(@Param("aiDiagnosisId") Long aiDiagnosisId,
                                   @Param("isAbnormal") Integer isAbnormal,
                                   @Param("abnormalLevel") Integer abnormalLevel,
                                   @Param("abnormalType") String abnormalType,
                                   @Param("abnormalCount") Integer abnormalCount);

    int updateWarningHandleStatusByDiagnosis(@Param("aiDiagnosisId") Long aiDiagnosisId,
                                             @Param("recordId") Long recordId,
                                             @Param("handleStatus") Integer handleStatus,
                                             @Param("handleUserId") Long handleUserId,
                                             @Param("handleUserName") String handleUserName,
                                             @Param("handleTime") LocalDateTime handleTime,
                                             @Param("handleOpinion") String handleOpinion);

    Long countWarningsByDiagnosis(@Param("aiDiagnosisId") Long aiDiagnosisId,
                                  @Param("recordId") Long recordId);
}
