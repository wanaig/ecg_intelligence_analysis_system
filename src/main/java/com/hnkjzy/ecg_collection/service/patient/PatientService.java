package com.hnkjzy.ecg_collection.service.patient;

import com.hnkjzy.ecg_collection.model.dto.patient.PatientPageQueryDto;
import com.hnkjzy.ecg_collection.model.vo.common.PageResultVo;
import com.hnkjzy.ecg_collection.model.vo.patient.PatientDashboardStatVo;
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
}
