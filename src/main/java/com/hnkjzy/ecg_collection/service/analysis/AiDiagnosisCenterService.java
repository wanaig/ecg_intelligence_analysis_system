package com.hnkjzy.ecg_collection.service.analysis;

import com.hnkjzy.ecg_collection.model.dto.analysis.AiDiagnosisAuditSubmitDto;
import com.hnkjzy.ecg_collection.model.dto.analysis.AiDiagnosisPageQueryDto;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisAuditSubmitResultVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisDetailVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisOverviewVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisPageResultVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisStatusDictVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisWaveformVo;
import com.hnkjzy.ecg_collection.service.BaseService;
import jakarta.servlet.http.HttpServletRequest;

/**
 * AI diagnosis center service.
 */
public interface AiDiagnosisCenterService extends BaseService {

    AiDiagnosisOverviewVo getOverview();

    AiDiagnosisPageResultVo pageDiagnoses(AiDiagnosisPageQueryDto queryDto);

    AiDiagnosisStatusDictVo getStatusDicts();

    AiDiagnosisDetailVo getDiagnosisDetail(String diagnosisId);

    AiDiagnosisWaveformVo getWaveform(Long ecgId);

    AiDiagnosisAuditSubmitResultVo submitAudit(AiDiagnosisAuditSubmitDto submitDto, HttpServletRequest request);
}
