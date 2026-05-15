package com.hnkjzy.ecg_collection.service.impl.patient;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnkjzy.ecg_collection.common.exception.BusinessException;
import com.hnkjzy.ecg_collection.common.result.ResultCode;
import com.hnkjzy.ecg_collection.mapper.patient.PatientInfoMapper;
import com.hnkjzy.ecg_collection.model.dto.patient.PatientPageQueryDto;
import com.hnkjzy.ecg_collection.model.dto.patient.PatientUpdateDto;
import com.hnkjzy.ecg_collection.model.entity.patient.PatientInfoEntity;
import com.hnkjzy.ecg_collection.model.vo.common.DictOptionVo;
import com.hnkjzy.ecg_collection.model.vo.common.PageResultVo;
import com.hnkjzy.ecg_collection.model.vo.patient.PatientBasicInfoVo;
import com.hnkjzy.ecg_collection.model.vo.patient.PatientDashboardStatVo;
import com.hnkjzy.ecg_collection.model.vo.patient.PatientDetailEchoVo;
import com.hnkjzy.ecg_collection.model.vo.patient.PatientDetailVo;
import com.hnkjzy.ecg_collection.model.vo.patient.PatientDiagnosisVo;
import com.hnkjzy.ecg_collection.model.vo.patient.PatientDictVo;
import com.hnkjzy.ecg_collection.model.vo.patient.PatientEcgRecordVo;
import com.hnkjzy.ecg_collection.model.vo.patient.PatientPageItemVo;
import com.hnkjzy.ecg_collection.model.vo.patient.PatientWarningVo;
import com.hnkjzy.ecg_collection.service.impl.BaseServiceImpl;
import com.hnkjzy.ecg_collection.service.patient.PatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

/**
 * 患者查询服务实现。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PatientServiceImpl extends BaseServiceImpl implements PatientService {

    private static final long DEFAULT_PAGE_NUM = 1L;
    private static final long DEFAULT_PAGE_SIZE = 10L;
    private static final long MAX_PAGE_SIZE = 200L;

    private final PatientInfoMapper patientInfoMapper;

    @Override
    public PatientDashboardStatVo getPatientStats() {
        PatientDashboardStatVo statVo = patientInfoMapper.selectDashboardStat();
        if (statVo == null) {
            statVo = new PatientDashboardStatVo();
            statVo.setTotalPatient(0L);
            statVo.setInHospital(0L);
            statVo.setHomeFollow(0L);
            statVo.setHighRisk(0L);
            return statVo;
        }
        statVo.setTotalPatient(defaultLong(statVo.getTotalPatient()));
        statVo.setInHospital(defaultLong(statVo.getInHospital()));
        statVo.setHomeFollow(defaultLong(statVo.getHomeFollow()));
        statVo.setHighRisk(defaultLong(statVo.getHighRisk()));
        return statVo;
    }

    @Override
    public PageResultVo<PatientPageItemVo> pagePatientList(PatientPageQueryDto queryDto) {
        PatientPageQueryDto query = normalizeQuery(queryDto);
        Integer riskLevelCode = parseRiskLevel(query.getRiskLevel());
        Integer patientStatusCode = parsePatientStatus(query.getPatientStatus());

        long pageNum = normalizePageNum(query.getPageNum());
        long pageSize = normalizePageSize(query.getPageSize());

        Page<PatientPageItemVo> page = new Page<>(pageNum, pageSize);
        IPage<PatientPageItemVo> pageData = patientInfoMapper.selectPatientPage(page, query, riskLevelCode, patientStatusCode);

        List<PatientPageItemVo> records = pageData.getRecords();
        if (records == null) {
            records = Collections.emptyList();
        }

        return PageResultVo.<PatientPageItemVo>builder()
                .pageNum(pageData.getCurrent())
                .pageSize(pageData.getSize())
                .total(pageData.getTotal())
                .pages(pageData.getPages())
                .records(records)
                .build();
    }

    @Override
    public PatientDetailVo getPatientDetail(Long patientId) {
        if (patientId == null || patientId <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "patientId 参数不合法");
        }

        PatientBasicInfoVo basicInfo = patientInfoMapper.selectPatientBasicInfo(patientId);
        if (basicInfo == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "患者不存在");
        }

        List<PatientEcgRecordVo> ecgRecords = patientInfoMapper.selectPatientEcgRecords(patientId);
        List<PatientWarningVo> warningHistory = patientInfoMapper.selectPatientWarningHistory(patientId);
        List<PatientDiagnosisVo> diagnosisInfo = patientInfoMapper.selectPatientDiagnosisInfo(patientId);

        PatientDetailVo detailVo = new PatientDetailVo();
        detailVo.setBasicInfo(basicInfo);
        detailVo.setEcgRecords(ecgRecords == null ? Collections.emptyList() : ecgRecords);
        detailVo.setWarningHistory(warningHistory == null ? Collections.emptyList() : warningHistory);
        detailVo.setDiagnosisInfo(diagnosisInfo == null ? Collections.emptyList() : diagnosisInfo);
        return detailVo;
    }

    @Override
    public PatientDictVo getPatientDicts() {
        PatientDictVo dictVo = new PatientDictVo();

        List<DictOptionVo> wardOptions = patientInfoMapper.selectWardOptions();
        if (wardOptions == null) {
            wardOptions = new ArrayList<>();
        } else {
            wardOptions = new ArrayList<>(wardOptions);
        }
        wardOptions.add(0, buildOption("", "全部病区"));

        List<DictOptionVo> riskLevelOptions = new ArrayList<>();
        riskLevelOptions.add(buildOption("", "全部风险等级"));
        riskLevelOptions.add(buildOption("1", "低危"));
        riskLevelOptions.add(buildOption("2", "中危"));
        riskLevelOptions.add(buildOption("3", "中高危"));
        riskLevelOptions.add(buildOption("4", "高危"));

        List<DictOptionVo> patientStatusOptions = new ArrayList<>();
        patientStatusOptions.add(buildOption("", "全部患者状态"));
        patientStatusOptions.add(buildOption("1", "住院中"));
        patientStatusOptions.add(buildOption("2", "出院"));
        patientStatusOptions.add(buildOption("3", "居家随访"));

        dictVo.setWardOptions(wardOptions);
        dictVo.setRiskLevelOptions(riskLevelOptions);
        dictVo.setPatientStatusOptions(patientStatusOptions);
        return dictVo;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updatePatient(Long patientId, PatientUpdateDto updateDto) {
        // 1. 参数校验
        if (patientId == null || patientId <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "patientId 参数不合法");
        }
        if (updateDto == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "修改参数不能为空");
        }

        // 2. 查询原患者信息
        PatientInfoEntity oldPatient = patientInfoMapper.selectById(patientId);
        if (oldPatient == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "患者不存在");
        }

        // 3. 枚举字符串转数字
        Integer genderCode = parseGenderEnum(updateDto.getGender());
        Integer riskLevelCode = parseRiskLevelEnum(updateDto.getRiskLevel());
        Integer patientStatusCode = parsePatientStatusEnum(updateDto.getStatus());

        // 4. 构建更新实体（前端未传的字段保留原值）
        PatientInfoEntity updateEntity = new PatientInfoEntity();
        updateEntity.setPatientId(patientId);
        updateEntity.setPatientName(updateDto.getName() != null ? updateDto.getName() : oldPatient.getPatientName());
        updateEntity.setGender(genderCode != null ? genderCode : oldPatient.getGender());
        updateEntity.setAge(updateDto.getAge() != null ? updateDto.getAge() : oldPatient.getAge());
        updateEntity.setWardId(oldPatient.getWardId()); // 前端传的是名称，保留原ID
        updateEntity.setBedId(oldPatient.getBedId());   // 前端传的是床位号，保留原ID
        updateEntity.setDeviceId(oldPatient.getDeviceId()); // 前端传的是设备编号，保留原ID
        updateEntity.setInpatientNo(updateDto.getInpatientNo() != null ? updateDto.getInpatientNo() : oldPatient.getInpatientNo());
        updateEntity.setRiskLevel(riskLevelCode != null ? riskLevelCode : oldPatient.getRiskLevel());
        updateEntity.setPatientStatus(patientStatusCode != null ? patientStatusCode : oldPatient.getPatientStatus());
        updateEntity.setAdmissionTime(updateDto.getAdmissionTime() != null ? updateDto.getAdmissionTime() : oldPatient.getAdmissionTime());
        updateEntity.setDischargeTime(updateDto.getDischargeTime());
        updateEntity.setPrimaryDiagnosis(updateDto.getDiagnosis() != null ? updateDto.getDiagnosis() : oldPatient.getPrimaryDiagnosis());

        // 5. 更新主表
        int rows = patientInfoMapper.updateById(updateEntity);
        if (rows <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "更新患者信息失败");
        }

        // 6. 同步冗余表（按需更新，只更新有变化的字段）
        syncRedundantTables(patientId, updateEntity);

        log.info("患者信息更新成功，patientId={}, patientName={}", patientId, updateEntity.getPatientName());
    }

    /**
     * 同步冗余表中的患者信息。
     * 核心原则：只更新患者姓名、性别、年龄、住院号等快照字段。
     */
    private void syncRedundantTables(Long patientId, PatientInfoEntity patient) {
        String patientName = patient.getPatientName();
        Integer age = patient.getAge();
        String inpatientNo = patient.getInpatientNo();
        Integer genderCode = patient.getGender();

        // 性别转换：1 -> 男, 2 -> 女
        String genderStr = (genderCode != null && genderCode == 1) ? "男" : "女";

        // 6.1 同步 ecg_collection_record
        patientInfoMapper.updateCollectionRecordPatient(patientId, patientName, genderStr, age, inpatientNo);

        // 6.2 同步 ecg_ai_diagnosis
        patientInfoMapper.updateAiDiagnosisPatient(patientId, patientName, genderStr, age, inpatientNo);

        // 6.3 同步 ecg_diagnosis_report
        patientInfoMapper.updateDiagnosisReportPatient(patientId, patientName, genderStr, age, inpatientNo);

        // 6.4 同步 ecg_abnormal_warning
        patientInfoMapper.updateAbnormalWarningPatient(patientId, patientName, genderStr, age, inpatientNo);

        // 6.5 同步 ecg_real_time_monitor（只更新患者姓名）
        patientInfoMapper.updateRealTimeMonitorPatient(patientId, patientName);

        // 6.6 同步 patient_follow_up_record（只更新患者姓名）
        patientInfoMapper.updateFollowUpRecordPatient(patientId, patientName);

        // 6.7 同步 ecg_research_data
        patientInfoMapper.updateResearchDataPatient(patientId, patientName, genderCode, age, inpatientNo);
    }

    @Override
    public PatientDetailEchoVo getPatientDetailEcho(Long patientId) {
        if (patientId == null || patientId <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "patientId 参数不合法");
        }

        PatientDetailEchoVo echoVo = patientInfoMapper.selectPatientDetailEcho(patientId);
        if (echoVo == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "患者不存在");
        }
        return echoVo;
    }

    private PatientPageQueryDto normalizeQuery(PatientPageQueryDto queryDto) {
        PatientPageQueryDto query = queryDto == null ? new PatientPageQueryDto() : queryDto;
        query.setKeyword(trimToNull(query.getKeyword()));
        query.setWard(trimToNull(query.getWard()));
        query.setRiskLevel(trimToNull(query.getRiskLevel()));
        query.setPatientStatus(trimToNull(query.getPatientStatus()));
        return query;
    }

    private long normalizePageNum(Long pageNum) {
        if (pageNum == null || pageNum < 1) {
            return DEFAULT_PAGE_NUM;
        }
        return pageNum;
    }

    private long normalizePageSize(Long pageSize) {
        if (pageSize == null || pageSize < 1) {
            return DEFAULT_PAGE_SIZE;
        }
        if (pageSize > MAX_PAGE_SIZE) {
            return MAX_PAGE_SIZE;
        }
        return pageSize;
    }

    private Integer parseRiskLevel(String riskLevel) {
        if (!StringUtils.hasText(riskLevel)) {
            return null;
        }

        switch (riskLevel) {
            case "1":
            case "低危":
                return 1;
            case "2":
            case "中危":
                return 2;
            case "3":
            case "中高危":
                return 3;
            case "4":
            case "高危":
                return 4;
            default:
                throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "riskLevel 参数不合法");
        }
    }

    private Integer parsePatientStatus(String patientStatus) {
        if (!StringUtils.hasText(patientStatus)) {
            return null;
        }

        switch (patientStatus) {
            case "1":
            case "住院中":
            case "住院":
                return 1;
            case "2":
            case "出院":
                return 2;
            case "3":
            case "居家随访":
            case "居家":
                return 3;
            default:
                throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "patientStatus 参数不合法");
        }
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private Long defaultLong(Long value) {
        return value == null ? 0L : value;
    }

    private DictOptionVo buildOption(String value, String label) {
        return DictOptionVo.builder().value(value).label(label).build();
    }

    /**
     * 解析性别枚举字符串为数字。
     * MALE -> 1, FEMALE -> 2
     */
    private Integer parseGenderEnum(String gender) {
        if (!StringUtils.hasText(gender)) {
            return null;
        }
        switch (gender.toUpperCase()) {
            case "MALE":
                return 1;
            case "FEMALE":
                return 2;
            default:
                throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "gender 参数不合法，应为 MALE 或 FEMALE");
        }
    }

    /**
     * 解析风险等级枚举字符串为数字。
     * LOW -> 1, MEDIUM -> 2, MEDIUM_HIGH -> 3, HIGH -> 4
     */
    private Integer parseRiskLevelEnum(String riskLevel) {
        if (!StringUtils.hasText(riskLevel)) {
            return null;
        }
        switch (riskLevel.toUpperCase()) {
            case "LOW":
                return 1;
            case "MEDIUM":
                return 2;
            case "MEDIUM_HIGH":
                return 3;
            case "HIGH":
                return 4;
            default:
                throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "riskLevel 参数不合法，应为 LOW/MEDIUM/MEDIUM_HIGH/HIGH");
        }
    }

    /**
     * 解析患者状态枚举字符串为数字。
     * IN_HOSPITAL -> 1, DISCHARGED -> 2, HOME_FOLLOW -> 3
     */
    private Integer parsePatientStatusEnum(String patientStatus) {
        if (!StringUtils.hasText(patientStatus)) {
            return null;
        }
        switch (patientStatus.toUpperCase()) {
            case "IN_HOSPITAL":
                return 1;
            case "DISCHARGED":
                return 2;
            case "HOME_FOLLOW":
                return 3;
            default:
                throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "patientStatus 参数不合法，应为 IN_HOSPITAL/DISCHARGED/HOME_FOLLOW");
        }
    }
}
