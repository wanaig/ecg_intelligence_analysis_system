package com.hnkjzy.ecg_collection.mapper.analysis;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnkjzy.ecg_collection.model.dto.analysis.DiagnosisReportPageQueryDto;
import com.hnkjzy.ecg_collection.model.vo.analysis.DiagnosisReportDetailVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.DiagnosisReportPageItemVo;
import org.apache.ibatis.annotations.Param;

/**
 * Diagnosis report management mapper.
 */
public interface DiagnosisReportManagerMapper {

    IPage<DiagnosisReportPageItemVo> selectReportPage(Page<DiagnosisReportPageItemVo> page,
                                                      @Param("req") DiagnosisReportPageQueryDto req,
                                                      @Param("statusCode") Integer statusCode);

    DiagnosisReportDetailVo selectReportDetail(@Param("reportId") Long reportId);
}
