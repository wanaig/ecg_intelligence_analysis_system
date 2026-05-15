package com.hnkjzy.ecg_collection.service.patient;

import com.hnkjzy.ecg_collection.model.dto.patient.PatientPageQueryDto;
import com.hnkjzy.ecg_collection.model.dto.patient.PatientUpdateDto;
import com.hnkjzy.ecg_collection.model.vo.common.PageResultVo;
import com.hnkjzy.ecg_collection.model.vo.patient.PatientDashboardStatVo;
import com.hnkjzy.ecg_collection.model.vo.patient.PatientDetailEchoVo;
import com.hnkjzy.ecg_collection.model.vo.patient.PatientDetailVo;
import com.hnkjzy.ecg_collection.model.vo.patient.PatientDictVo;
import com.hnkjzy.ecg_collection.model.vo.patient.PatientPageItemVo;
import com.hnkjzy.ecg_collection.service.BaseService;

/**
 * 患者查询服务。
 */
public interface PatientService extends BaseService {

    PatientDashboardStatVo getPatientStats();

    PageResultVo<PatientPageItemVo> pagePatientList(PatientPageQueryDto queryDto);

    PatientDetailVo getPatientDetail(Long patientId);

    PatientDictVo getPatientDicts();

    /**
     * 修改患者信息（含冗余表同步）。
     *
     * @param patientId 患者ID
     * @param updateDto 修改参数
     */
    void updatePatient(Long patientId, PatientUpdateDto updateDto);

    /**
     * 获取患者详情回响（详情弹窗 + 修改弹窗初始化）。
     *
     * @param patientId 患者ID
     * @return 患者详情回响数据
     */
    PatientDetailEchoVo getPatientDetailEcho(Long patientId);
}
