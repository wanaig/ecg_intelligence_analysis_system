package com.hnkjzy.ecg_collection.service.impl.patient;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnkjzy.ecg_collection.common.exception.BusinessException;
import com.hnkjzy.ecg_collection.common.result.ResultCode;
import com.hnkjzy.ecg_collection.mapper.patient.PatientInfoMapper;
import com.hnkjzy.ecg_collection.model.dto.patient.PatientPageQueryDto;
import com.hnkjzy.ecg_collection.model.vo.common.DictOptionVo;
import com.hnkjzy.ecg_collection.model.vo.common.PageResultVo;
import com.hnkjzy.ecg_collection.model.vo.patient.PatientBasicInfoVo;
import com.hnkjzy.ecg_collection.model.vo.patient.PatientDashboardStatVo;
import com.hnkjzy.ecg_collection.model.vo.patient.PatientDetailVo;
import com.hnkjzy.ecg_collection.model.vo.patient.PatientDiagnosisVo;
import com.hnkjzy.ecg_collection.model.vo.patient.PatientDictVo;
import com.hnkjzy.ecg_collection.model.vo.patient.PatientEcgRecordVo;
import com.hnkjzy.ecg_collection.model.vo.patient.PatientPageItemVo;
import com.hnkjzy.ecg_collection.model.vo.patient.PatientWarningVo;
import com.hnkjzy.ecg_collection.service.impl.BaseServiceImpl;
import com.hnkjzy.ecg_collection.service.patient.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

/**
 * 患者查询服务实现。
 */
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
}
