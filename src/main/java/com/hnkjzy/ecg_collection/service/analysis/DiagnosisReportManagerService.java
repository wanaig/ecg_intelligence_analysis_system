package com.hnkjzy.ecg_collection.service.analysis;

import com.hnkjzy.ecg_collection.model.dto.analysis.DiagnosisReportPageQueryDto;
import com.hnkjzy.ecg_collection.model.vo.analysis.DiagnosisReportDetailVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.DiagnosisReportPageResultVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.DiagnosisReportPdfFileVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.DiagnosisReportStatusDictVo;
import com.hnkjzy.ecg_collection.service.BaseService;

/**
 * Diagnosis report management service.
 */
public interface DiagnosisReportManagerService extends BaseService {

    DiagnosisReportPageResultVo pageReports(DiagnosisReportPageQueryDto queryDto);

    DiagnosisReportStatusDictVo getStatusDicts();

    DiagnosisReportDetailVo getReportDetail(Long reportId);

    DiagnosisReportPdfFileVo downloadReportPdf(Long reportId);
}
