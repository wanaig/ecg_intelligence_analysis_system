package com.hnkjzy.ecg_collection.service.analysis;

import com.hnkjzy.ecg_collection.model.dto.analysis.AiDiagnosisAuditSubmitDto;
import com.hnkjzy.ecg_collection.model.dto.analysis.AiDiagnosisAuditSubmitV2Dto;
import com.hnkjzy.ecg_collection.model.dto.analysis.AiDiagnosisDashboardPageQueryDto;
import com.hnkjzy.ecg_collection.model.dto.analysis.AiDiagnosisDashboardTimeRangeQueryDto;
import com.hnkjzy.ecg_collection.model.dto.analysis.AiDiagnosisPageQueryDto;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisAbnormalTypeRatioVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisAuditDetailVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisAuditSubmitResultVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisAuditSubmitV2ResultVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisCoreMetricsVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisDashboardPageItemVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisDetailVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisEngineStatusVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisLiteDetailVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisOverviewVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisPageResultVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisStatusDictVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisTrendVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisWaveformVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AnalysisDashboardPageResultVo;
import com.hnkjzy.ecg_collection.service.BaseService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * AI diagnosis center service.
 */
public interface AiDiagnosisCenterService extends BaseService {

    AiDiagnosisEngineStatusVo getEngineStatus();

    AiDiagnosisCoreMetricsVo getDashboardCoreMetrics(AiDiagnosisDashboardTimeRangeQueryDto queryDto);

    AiDiagnosisTrendVo getWarningTrend(String timeRange);

    List<AiDiagnosisAbnormalTypeRatioVo> getAbnormalTypeRatios(AiDiagnosisDashboardTimeRangeQueryDto queryDto);

    AnalysisDashboardPageResultVo<AiDiagnosisDashboardPageItemVo> pageDashboardDiagnoses(AiDiagnosisDashboardPageQueryDto queryDto);

    AiDiagnosisLiteDetailVo getLiteDetail(String diagnosisId);

    AiDiagnosisAuditDetailVo getAuditDetail(String diagnosisId);

    AiDiagnosisAuditSubmitV2ResultVo submitAuditV2(AiDiagnosisAuditSubmitV2Dto submitDto, HttpServletRequest request);

    AiDiagnosisOverviewVo getOverview();

    AiDiagnosisPageResultVo pageDiagnoses(AiDiagnosisPageQueryDto queryDto);

    AiDiagnosisStatusDictVo getStatusDicts();

    AiDiagnosisDetailVo getDiagnosisDetail(String diagnosisId);

    AiDiagnosisWaveformVo getWaveform(Long ecgId);

    AiDiagnosisAuditSubmitResultVo submitAudit(AiDiagnosisAuditSubmitDto submitDto, HttpServletRequest request);
}
