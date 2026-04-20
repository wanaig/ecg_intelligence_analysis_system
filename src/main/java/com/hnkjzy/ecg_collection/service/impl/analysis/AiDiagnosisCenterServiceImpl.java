package com.hnkjzy.ecg_collection.service.impl.analysis;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnkjzy.ecg_collection.common.exception.BusinessException;
import com.hnkjzy.ecg_collection.common.result.ResultCode;
import com.hnkjzy.ecg_collection.mapper.analysis.AiDiagnosisCenterMapper;
import com.hnkjzy.ecg_collection.mapper.system.SystemUserMapper;
import com.hnkjzy.ecg_collection.model.dto.analysis.AiDiagnosisAuditSubmitDto;
import com.hnkjzy.ecg_collection.model.dto.analysis.AiDiagnosisPageQueryDto;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisAbnormalPointVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisAuditBaseVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisAuditSubmitResultVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisDetailVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisOverviewVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisPageItemVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisPageResultVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisStatusDictVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisWaveformAnnotationVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisWaveformPointVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisWaveformSegmentVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.AiDiagnosisWaveformVo;
import com.hnkjzy.ecg_collection.model.vo.common.DictOptionVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemUserOperatorVo;
import com.hnkjzy.ecg_collection.service.analysis.AiDiagnosisCenterService;
import com.hnkjzy.ecg_collection.service.impl.BaseServiceImpl;
import com.hnkjzy.ecg_collection.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * AI diagnosis center service implementation.
 */
@Service
@RequiredArgsConstructor
public class AiDiagnosisCenterServiceImpl extends BaseServiceImpl implements AiDiagnosisCenterService {

    private static final long DEFAULT_PAGE_NUM = 1L;
    private static final long DEFAULT_PAGE_SIZE = 10L;
    private static final long MAX_PAGE_SIZE = 200L;
    private static final int DEFAULT_SAMPLING_RATE = 250;
    private static final int DEFAULT_LEAD_COUNT = 12;
    private static final int WAVEFORM_POINT_COUNT = 1200;

    private static final DateTimeFormatter REPORT_NO_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private static final List<String> AUDIT_PERMISSION_CODES = List.of(
            "report:audit",
            "diagnosis:write",
            "ai:diagnosis:audit",
            "analysis:ai-diagnosis:audit"
    );

    private final AiDiagnosisCenterMapper aiDiagnosisCenterMapper;
    private final SystemUserMapper systemUserMapper;
    private final JwtUtil jwtUtil;

    @Override
    public AiDiagnosisOverviewVo getOverview() {
        AiDiagnosisOverviewVo overviewVo = aiDiagnosisCenterMapper.selectOverview();
        if (overviewVo == null) {
            overviewVo = new AiDiagnosisOverviewVo();
        }
        overviewVo.setTotalCount(defaultLong(overviewVo.getTotalCount()));
        overviewVo.setPendingAuditCount(defaultLong(overviewVo.getPendingAuditCount()));
        overviewVo.setAuditedCount(defaultLong(overviewVo.getAuditedCount()));
        overviewVo.setAvgConfidence(defaultDecimal(overviewVo.getAvgConfidence()));
        return overviewVo;
    }

    @Override
    public AiDiagnosisPageResultVo pageDiagnoses(AiDiagnosisPageQueryDto queryDto) {
        AiDiagnosisPageQueryDto query = normalizePageQuery(queryDto);
        Integer statusCode = parseStatusCode(query.getStatus(), false);

        long pageNum = normalizePageNum(query.getPageNum());
        long pageSize = normalizePageSize(query.getPageSize());

        Page<AiDiagnosisPageItemVo> page = new Page<>(pageNum, pageSize);
        IPage<AiDiagnosisPageItemVo> pageData = aiDiagnosisCenterMapper.selectDiagnosisPage(page, query, statusCode);

        List<AiDiagnosisPageItemVo> records = pageData.getRecords();
        if (records == null) {
            records = Collections.emptyList();
        }

        return AiDiagnosisPageResultVo.builder()
                .total(pageData.getTotal())
                .pages(pageData.getPages())
                .list(records)
                .build();
    }

    @Override
    public AiDiagnosisStatusDictVo getStatusDicts() {
        AiDiagnosisStatusDictVo dictVo = new AiDiagnosisStatusDictVo();

        List<DictOptionVo> statusList = new ArrayList<>();
        statusList.add(buildOption("", "全部状态"));
        statusList.add(buildOption("待审核", "待审核"));
        statusList.add(buildOption("已审核", "已审核"));

        dictVo.setStatusList(statusList);
        return dictVo;
    }

    @Override
    public AiDiagnosisDetailVo getDiagnosisDetail(String diagnosisId) {
        String diagnosisKey = trimToNull(diagnosisId);
        if (diagnosisKey == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "diagnosisId 参数不合法");
        }

        AiDiagnosisDetailVo detailVo = aiDiagnosisCenterMapper.selectDiagnosisDetail(diagnosisKey);
        if (detailVo == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "AI 诊断记录不存在");
        }

        detailVo.setAbnormalCount(defaultInteger(detailVo.getAbnormalCount()));
        detailVo.setConfidence(defaultDecimal(detailVo.getConfidence()));
        if (!StringUtils.hasText(detailVo.getStatus())) {
            detailVo.setStatus(reportStatusToText(detailVo.getReportStatus()));
        }
        detailVo.setAbnormalPointList(buildAbnormalPointList(detailVo.getAbnormalType(), detailVo.getAbnormalCount(), detailVo.getConfidence()));
        return detailVo;
    }

    @Override
    public AiDiagnosisWaveformVo getWaveform(Long ecgId) {
        if (ecgId == null || ecgId <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "ecgId 参数不合法");
        }

        AiDiagnosisWaveformVo waveformVo = aiDiagnosisCenterMapper.selectWaveformMeta(ecgId);
        if (waveformVo == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "心电记录不存在");
        }

        int samplingRate = waveformVo.getSamplingRate() == null ? DEFAULT_SAMPLING_RATE : waveformVo.getSamplingRate();
        int leadCount = waveformVo.getLeadCount() == null ? DEFAULT_LEAD_COUNT : waveformVo.getLeadCount();
        waveformVo.setSamplingRate(samplingRate);
        waveformVo.setLeadCount(leadCount);

        List<AiDiagnosisWaveformPointVo> waveform = buildWaveform(ecgId, samplingRate, leadCount);
        waveformVo.setWaveform(waveform);
        waveformVo.setSegments(buildWaveSegments(waveform.size()));
        waveformVo.setAnnotations(buildWaveAnnotations(waveform.size(), ecgId));
        return waveformVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AiDiagnosisAuditSubmitResultVo submitAudit(AiDiagnosisAuditSubmitDto submitDto, HttpServletRequest request) {
        AiDiagnosisAuditSubmitDto submit = normalizeAuditSubmit(submitDto);

        SystemUserOperatorVo operator = resolveCurrentOperator(request);
        validateAuditPermission(operator);

        AiDiagnosisAuditBaseVo auditBase = aiDiagnosisCenterMapper.selectAuditBase(submit.getDiagnosisId());
        if (auditBase == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "AI 诊断记录不存在");
        }

        Integer status = auditBase.getReportStatus();
        if (status != null && (status == 2 || status == 3 || status == 4)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "该诊断记录已审核，不能重复提交");
        }

        LocalDateTime now = LocalDateTime.now();
        Long reportId = auditBase.getReportId();
        String reportNo = auditBase.getReportNo();
        String operatorName = resolveOperatorName(operator);
        String auditOpinion = "审核通过";

        if (reportId == null) {
            reportId = IdWorker.getId();
            reportNo = buildReportNo(now, reportId);
            aiDiagnosisCenterMapper.insertAuditReport(
                    reportId,
                    reportNo,
                    auditBase.getEcgId(),
                    auditBase.getAiDiagnosisId(),
                    auditBase.getPatientId(),
                    auditBase.getPatientName(),
                    auditBase.getGender(),
                    auditBase.getAge(),
                    auditBase.getHospitalNo(),
                    auditBase.getCollectionTime(),
                    auditBase.getAiConclusion(),
                    submit.getDoctorConclusion(),
                    operator.getUserId(),
                    operatorName,
                    now,
                    auditOpinion,
                    2
            );
        } else {
            aiDiagnosisCenterMapper.updateReportAudit(
                    reportId,
                    submit.getDoctorConclusion(),
                    operator.getUserId(),
                    operatorName,
                    now,
                    auditOpinion,
                    2
            );
        }

        aiDiagnosisCenterMapper.updateCollectionReportStatus(auditBase.getEcgId(), 2, 2);

        AiDiagnosisAuditSubmitResultVo resultVo = new AiDiagnosisAuditSubmitResultVo();
        resultVo.setDiagnosisId(auditBase.getDiagnosisId());
        resultVo.setStatus("已审核");
        resultVo.setAuditedBy(operatorName);
        resultVo.setAuditedTime(now);
        resultVo.setReportDraftId(reportId);
        resultVo.setReportNo(reportNo);
        return resultVo;
    }

    private AiDiagnosisPageQueryDto normalizePageQuery(AiDiagnosisPageQueryDto queryDto) {
        AiDiagnosisPageQueryDto query = queryDto == null ? new AiDiagnosisPageQueryDto() : queryDto;
        query.setEcgNo(trimToNull(query.getEcgNo()));
        query.setPatientName(trimToNull(query.getPatientName()));
        query.setStatus(trimToNull(query.getStatus()));
        return query;
    }

    private AiDiagnosisAuditSubmitDto normalizeAuditSubmit(AiDiagnosisAuditSubmitDto submitDto) {
        if (submitDto == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "请求参数不能为空");
        }

        String diagnosisId = trimToNull(submitDto.getDiagnosisId());
        String doctorConclusion = trimToNull(submitDto.getDoctorConclusion());

        if (diagnosisId == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "diagnosisId 参数不合法");
        }
        if (doctorConclusion == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "doctorConclusion 参数不合法");
        }
        if (doctorConclusion.length() > 2000) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "doctorConclusion 长度不能超过 2000");
        }

        submitDto.setDiagnosisId(diagnosisId);
        submitDto.setDoctorConclusion(doctorConclusion);
        return submitDto;
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

    private Integer parseStatusCode(String status, boolean required) {
        if (!StringUtils.hasText(status)) {
            if (required) {
                throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "status 参数不合法");
            }
            return null;
        }

        String value = status.trim();
        if ("1".equals(value) || "待审核".equals(value) || "pending".equalsIgnoreCase(value)) {
            return 1;
        }
        if ("2".equals(value) || "已审核".equals(value) || "audited".equalsIgnoreCase(value)) {
            return 2;
        }
        throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "status 参数不合法");
    }

    private List<AiDiagnosisAbnormalPointVo> buildAbnormalPointList(String abnormalType, Integer abnormalCount, BigDecimal confidence) {
        int count = abnormalCount == null ? 0 : Math.max(abnormalCount, 0);
        if (count == 0) {
            return Collections.emptyList();
        }

        String[] types = splitAbnormalTypes(abnormalType);
        List<AiDiagnosisAbnormalPointVo> pointList = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            String type = types[i % types.length];
            int pointIndex = 120 + i * 180;
            String level = resolveAbnormalLevel(i, confidence);
            String description = type + " 波段出现形态异常，建议医生重点复核。";
            pointList.add(new AiDiagnosisAbnormalPointVo(pointIndex, type, description, level));
        }
        return pointList;
    }

    private String[] splitAbnormalTypes(String abnormalType) {
        if (!StringUtils.hasText(abnormalType)) {
            return new String[]{"异常节律"};
        }
        String normalized = abnormalType.trim();
        String[] items = normalized.split("[,，;；\\s]+");
        List<String> valid = new ArrayList<>();
        for (String item : items) {
            if (StringUtils.hasText(item)) {
                valid.add(item.trim());
            }
        }
        if (valid.isEmpty()) {
            return new String[]{"异常节律"};
        }
        return valid.toArray(new String[0]);
    }

    private String resolveAbnormalLevel(int index, BigDecimal confidence) {
        BigDecimal value = defaultDecimal(confidence);
        if (index == 0 && value.compareTo(new BigDecimal("90")) < 0) {
            return "高危";
        }
        if (index == 0) {
            return "中危";
        }
        return "低危";
    }

    private List<AiDiagnosisWaveformPointVo> buildWaveform(Long ecgId, Integer samplingRate, Integer leadCount) {
        double offset = ecgId == null ? 0 : (ecgId % 113);
        double samplingDivider = samplingRate == null || samplingRate <= 0 ? 8.0 : Math.max(samplingRate / 30.0, 8.0);
        double leadFactor = leadCount == null || leadCount <= 0 ? 1.0 : Math.min(leadCount / 12.0, 1.4);

        List<AiDiagnosisWaveformPointVo> points = new ArrayList<>(WAVEFORM_POINT_COUNT);
        for (int i = 0; i < WAVEFORM_POINT_COUNT; i++) {
            double angle = (i + offset) / samplingDivider;
            double value = Math.sin(angle) * 0.18 + Math.sin(angle * 0.21) * 0.04;

            int cyclePoint = i % 180;
            if (cyclePoint == 34) {
                value += 0.95;
            } else if (cyclePoint == 35) {
                value -= 0.25;
            } else if (cyclePoint == 72) {
                value += 0.30;
            }

            value = value * leadFactor;
            value = Math.round(value * 100000D) / 100000D;
            points.add(new AiDiagnosisWaveformPointVo(i, value));
        }
        return points;
    }

    private List<AiDiagnosisWaveformSegmentVo> buildWaveSegments(int pointCount) {
        List<AiDiagnosisWaveformSegmentVo> segments = new ArrayList<>();
        for (int start = 0; start < pointCount; start += 180) {
            addSegment(segments, "P", start + 20, start + 33, pointCount);
            addSegment(segments, "QRS", start + 34, start + 46, pointCount);
            addSegment(segments, "T", start + 70, start + 95, pointCount);
        }
        return segments;
    }

    private void addSegment(List<AiDiagnosisWaveformSegmentVo> segments,
                            String type,
                            int start,
                            int end,
                            int pointCount) {
        if (start >= pointCount) {
            return;
        }
        int segmentEnd = Math.min(end, pointCount - 1);
        segments.add(new AiDiagnosisWaveformSegmentVo(type, start, segmentEnd));
    }

    private List<AiDiagnosisWaveformAnnotationVo> buildWaveAnnotations(int pointCount, Long ecgId) {
        int seed = ecgId == null ? 0 : (int) Math.abs(ecgId % 37);
        List<AiDiagnosisWaveformAnnotationVo> annotations = new ArrayList<>();

        addAnnotation(annotations, 180 + seed, "室早疑似", "中危", pointCount);
        addAnnotation(annotations, 540 + seed, "ST段压低", "中危", pointCount);
        addAnnotation(annotations, 900 + seed, "房颤可疑", "高危", pointCount);
        return annotations;
    }

    private void addAnnotation(List<AiDiagnosisWaveformAnnotationVo> annotations,
                               int pointIndex,
                               String label,
                               String level,
                               int pointCount) {
        if (pointIndex >= pointCount) {
            return;
        }
        annotations.add(new AiDiagnosisWaveformAnnotationVo(pointIndex, label, level));
    }

    private String reportStatusToText(Integer reportStatus) {
        if (reportStatus == null || reportStatus == 1) {
            return "待审核";
        }
        if (reportStatus == 2 || reportStatus == 3 || reportStatus == 4) {
            return "已审核";
        }
        return "待审核";
    }

    private DictOptionVo buildOption(String value, String label) {
        return DictOptionVo.builder().value(value).label(label).build();
    }

    private String buildReportNo(LocalDateTime now, Long reportId) {
        long suffix = Math.abs(reportId % 10000);
        return "REP" + now.format(REPORT_NO_TIME_FORMAT) + String.format("%04d", suffix);
    }

    private String resolveOperatorName(SystemUserOperatorVo operator) {
        if (operator == null) {
            return "系统审核医生";
        }
        if (StringUtils.hasText(operator.getRealName())) {
            return operator.getRealName().trim();
        }
        if (StringUtils.hasText(operator.getUserName())) {
            return operator.getUserName().trim();
        }
        return "系统审核医生";
    }

    private SystemUserOperatorVo resolveCurrentOperator(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        String authorization = request.getHeader("Authorization");
        if (!StringUtils.hasText(authorization)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        Claims claims;
        try {
            claims = jwtUtil.parseToken(authorization);
        } catch (Exception ex) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        Long userId = extractUserId(claims);
        String subject = claims == null ? null : claims.getSubject();

        SystemUserOperatorVo operator = null;
        if (userId != null && userId > 0) {
            operator = systemUserMapper.selectOperatorById(userId);
        }
        if (operator == null && StringUtils.hasText(subject)) {
            String value = subject.trim();
            if (value.matches("^\\d+$")) {
                operator = systemUserMapper.selectOperatorById(Long.parseLong(value));
            } else {
                operator = systemUserMapper.selectOperatorByUserName(value);
            }
        }

        if (operator == null || operator.getStatus() == null || operator.getStatus() != 1) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return operator;
    }

    private Long extractUserId(Claims claims) {
        if (claims == null) {
            return null;
        }

        Object value = claims.get("userId");
        if (value == null) {
            value = claims.get("uid");
        }
        if (value == null) {
            value = claims.get("id");
        }
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value instanceof String && ((String) value).trim().matches("^\\d+$")) {
            return Long.parseLong(((String) value).trim());
        }
        return null;
    }

    private void validateAuditPermission(SystemUserOperatorVo operator) {
        if (operator.getRoleId() == null) {
            throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "无 AI 诊断审核权限");
        }
        Long count = systemUserMapper.countRolePermission(operator.getRoleId(), AUDIT_PERMISSION_CODES);
        if (count == null || count <= 0) {
            throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "无 AI 诊断审核权限");
        }
    }

    private Long defaultLong(Long value) {
        return value == null ? 0L : value;
    }

    private Integer defaultInteger(Integer value) {
        return value == null ? 0 : value;
    }

    private BigDecimal defaultDecimal(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}
