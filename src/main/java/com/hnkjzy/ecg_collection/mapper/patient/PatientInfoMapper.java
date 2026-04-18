package com.hnkjzy.ecg_collection.mapper.patient;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnkjzy.ecg_collection.mapper.BaseMapperX;
import com.hnkjzy.ecg_collection.model.dto.patient.PatientPageQueryDto;
import com.hnkjzy.ecg_collection.model.entity.patient.PatientInfoEntity;
import com.hnkjzy.ecg_collection.model.vo.patient.PatientBasicInfoVo;
import com.hnkjzy.ecg_collection.model.vo.patient.PatientDashboardStatVo;
import com.hnkjzy.ecg_collection.model.vo.patient.PatientDiagnosisVo;
import com.hnkjzy.ecg_collection.model.vo.patient.PatientEcgRecordVo;
import com.hnkjzy.ecg_collection.model.vo.patient.PatientPageItemVo;
import com.hnkjzy.ecg_collection.model.vo.patient.PatientWarningVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 患者信息 Mapper。
 */
public interface PatientInfoMapper extends BaseMapperX<PatientInfoEntity> {

    PatientDashboardStatVo selectDashboardStat();

    IPage<PatientPageItemVo> selectPatientPage(Page<PatientPageItemVo> page,
                                               @Param("req") PatientPageQueryDto req,
                                               @Param("riskLevelCode") Integer riskLevelCode,
                                               @Param("patientStatusCode") Integer patientStatusCode);

    PatientBasicInfoVo selectPatientBasicInfo(@Param("patientId") Long patientId);

    List<PatientEcgRecordVo> selectPatientEcgRecords(@Param("patientId") Long patientId);

    List<PatientWarningVo> selectPatientWarningHistory(@Param("patientId") Long patientId);

    List<PatientDiagnosisVo> selectPatientDiagnosisInfo(@Param("patientId") Long patientId);
}
