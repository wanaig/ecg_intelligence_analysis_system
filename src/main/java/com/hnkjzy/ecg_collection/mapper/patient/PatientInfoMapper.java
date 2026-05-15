package com.hnkjzy.ecg_collection.mapper.patient;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnkjzy.ecg_collection.mapper.BaseMapperX;
import com.hnkjzy.ecg_collection.model.dto.patient.PatientPageQueryDto;
import com.hnkjzy.ecg_collection.model.entity.patient.PatientInfoEntity;
import com.hnkjzy.ecg_collection.model.vo.common.DictOptionVo;
import com.hnkjzy.ecg_collection.model.vo.patient.PatientBasicInfoVo;
import com.hnkjzy.ecg_collection.model.vo.patient.PatientDashboardStatVo;
import com.hnkjzy.ecg_collection.model.vo.patient.PatientDetailEchoVo;
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

    List<DictOptionVo> selectWardOptions();

    /**
     * 查询患者详情回响（详情弹窗 + 修改弹窗初始化）。
     */
    PatientDetailEchoVo selectPatientDetailEcho(@Param("patientId") Long patientId);

    /**
     * 同步更新 ecg_collection_record 中的患者快照信息。
     */
    int updateCollectionRecordPatient(@Param("patientId") Long patientId,
                                      @Param("patientName") String patientName,
                                      @Param("gender") String gender,
                                      @Param("age") Integer age,
                                      @Param("inpatientNo") String inpatientNo);

    /**
     * 同步更新 ecg_ai_diagnosis 中的患者快照信息。
     */
    int updateAiDiagnosisPatient(@Param("patientId") Long patientId,
                                 @Param("patientName") String patientName,
                                 @Param("gender") String gender,
                                 @Param("age") Integer age,
                                 @Param("inpatientNo") String inpatientNo);

    /**
     * 同步更新 ecg_diagnosis_report 中的患者快照信息。
     */
    int updateDiagnosisReportPatient(@Param("patientId") Long patientId,
                                     @Param("patientName") String patientName,
                                     @Param("gender") String gender,
                                     @Param("age") Integer age,
                                     @Param("inpatientNo") String inpatientNo);

    /**
     * 同步更新 ecg_abnormal_warning 中的患者快照信息。
     */
    int updateAbnormalWarningPatient(@Param("patientId") Long patientId,
                                     @Param("patientName") String patientName,
                                     @Param("gender") String gender,
                                     @Param("age") Integer age,
                                     @Param("inpatientNo") String inpatientNo);

    /**
     * 同步更新 ecg_real_time_monitor 中的患者姓名。
     */
    int updateRealTimeMonitorPatient(@Param("patientId") Long patientId,
                                     @Param("patientName") String patientName);

    /**
     * 同步更新 patient_follow_up_record 中的患者姓名。
     */
    int updateFollowUpRecordPatient(@Param("patientId") Long patientId,
                                    @Param("patientName") String patientName);

    /**
     * 同步更新 ecg_research_data 中的患者快照信息。
     */
    int updateResearchDataPatient(@Param("patientId") Long patientId,
                                  @Param("patientName") String patientName,
                                  @Param("gender") Integer gender,
                                  @Param("age") Integer age,
                                  @Param("inpatientNo") String inpatientNo);
}
