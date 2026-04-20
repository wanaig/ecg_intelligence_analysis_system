package com.hnkjzy.ecg_collection.mapper.analysis;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnkjzy.ecg_collection.model.dto.analysis.AiDiagnosisPageQueryDto;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisAuditBaseVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisDetailVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisOverviewVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisPageItemVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisWaveformVo;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * AI diagnosis center mapper.
 */
public interface AiDiagnosisCenterMapper {

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

    int updateReportAudit(@Param("reportId") Long reportId,
                          @Param("doctorConclusion") String doctorConclusion,
                          @Param("auditDoctorId") Long auditDoctorId,
                          @Param("auditDoctorName") String auditDoctorName,
                          @Param("auditTime") LocalDateTime auditTime,
                          @Param("auditOpinion") String auditOpinion,
                          @Param("reportStatus") Integer reportStatus);

    int updateCollectionReportStatus(@Param("ecgId") Long ecgId,
                                     @Param("reportStatus") Integer reportStatus,
                                     @Param("displayStatus") Integer displayStatus);
}
