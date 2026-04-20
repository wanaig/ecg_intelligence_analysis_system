package com.hnkjzy.ecg_collection.service.impl.workbench;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnkjzy.ecg_collection.common.exception.BusinessException;
import com.hnkjzy.ecg_collection.common.result.ResultCode;
import com.hnkjzy.ecg_collection.mapper.workbench.EcgDataMapper;
import com.hnkjzy.ecg_collection.mapper.workbench.WorkbenchMapper;
import com.hnkjzy.ecg_collection.model.dto.workbench.EcgDataPageQueryDto;
import com.hnkjzy.ecg_collection.model.dto.workbench.EcgDataStatusUpdateDto;
import com.hnkjzy.ecg_collection.model.entity.workbench.EcgCollectionRecordEntity;
import com.hnkjzy.ecg_collection.model.vo.common.DictOptionVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.EcgDataDeviceSnapshotVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.EcgDataDictVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.EcgDataPageItemVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.EcgDataPatientSnapshotVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.EcgDataStatusUpdateResultVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.EcgDataUploadResultVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.EcgDataWaveformPointVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.EcgDataWaveformVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.WorkbenchEcgDetailVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.WorkbenchPageListVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.WorkbenchWaveformPointVo;
import com.hnkjzy.ecg_collection.service.impl.BaseServiceImpl;
import com.hnkjzy.ecg_collection.service.workbench.EcgDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ECG data list service implementation.
 */
@Service
@RequiredArgsConstructor
public class EcgDataServiceImpl extends BaseServiceImpl implements EcgDataService {

    private static final long DEFAULT_PAGE_NUM = 1L;
    private static final long DEFAULT_PAGE_SIZE = 10L;
    private static final long MAX_PAGE_SIZE = 200L;

    private static final DateTimeFormatter ECG_NO_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final List<DateTimeFormatter> DATE_TIME_FORMATTERS = List.of(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
            DateTimeFormatter.ISO_LOCAL_DATE_TIME
    );

    private final EcgDataMapper ecgDataMapper;
    private final WorkbenchMapper workbenchMapper;

    @Override
    public WorkbenchPageListVo<EcgDataPageItemVo> pageEcgData(EcgDataPageQueryDto queryDto) {
        EcgDataPageQueryDto query = normalizePageQuery(queryDto);
        Integer statusCode = parseStatusCode(query.getStatus(), false);
        DateRange range = resolveDateRange(query.getStartTime(), query.getEndTime());

        long pageNum = normalizePageNum(query.getPageNum());
        long pageSize = normalizePageSize(query.getPageSize());

        Page<EcgDataPageItemVo> page = new Page<>(pageNum, pageSize);
        IPage<EcgDataPageItemVo> pageData =
                ecgDataMapper.selectEcgDataPage(page, query, statusCode, range.getStartTime(), range.getEndTime());

        List<EcgDataPageItemVo> list = pageData.getRecords();
        if (list == null) {
            list = Collections.emptyList();
        }

        return WorkbenchPageListVo.<EcgDataPageItemVo>builder()
                .total(pageData.getTotal())
                .list(list)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EcgDataUploadResultVo uploadData(MultipartFile file, String patientName, String inpatientNo, String deviceNo) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "file is required");
        }
        if (!StringUtils.hasText(patientName)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "patientName parameter is invalid");
        }
        if (!StringUtils.hasText(inpatientNo)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "inpatientNo parameter is invalid");
        }
        if (!StringUtils.hasText(deviceNo)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "deviceNo parameter is invalid");
        }

        String normalizedPatientName = patientName.trim();
        String normalizedInpatientNo = inpatientNo.trim();
        String normalizedDeviceNo = deviceNo.trim();

        EcgDataPatientSnapshotVo patient = ecgDataMapper.selectPatientSnapshot(normalizedPatientName, normalizedInpatientNo);
        if (patient == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "patient not found");
        }

        EcgDataDeviceSnapshotVo device = ecgDataMapper.selectDeviceSnapshot(normalizedDeviceNo);
        if (device == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "device not found");
        }

        Long recordId = IdWorker.getId();
        LocalDateTime now = LocalDateTime.now();
        String ecgNo = buildEcgNo(recordId, now);
        String sourceFileUrl = buildSourceFileUrl(file, recordId);

        EcgCollectionRecordEntity entity = new EcgCollectionRecordEntity();
        entity.setRecordId(recordId);
        entity.setEcgNo(ecgNo);
        entity.setPatientId(patient.getPatientId());
        entity.setPatientName(patient.getPatientName());
        entity.setGender(patient.getGender());
        entity.setAge(patient.getAge());
        entity.setInpatientNo(patient.getInpatientNo());
        entity.setDeptId(patient.getWardId());
        entity.setDeptName(patient.getWardName());
        entity.setBedNo(patient.getBedNo());
        entity.setDeviceId(device.getDeviceId());
        entity.setDeviceName(device.getDeviceName());
        entity.setLeadCount(12);
        entity.setCollectionStartTime(now);
        entity.setCollectionEndTime(now);
        entity.setSamplingRate(250);
        entity.setEcgDataFileUrl(sourceFileUrl);
        entity.setCollectionDuration(0);
        entity.setCollectionType(4);
        entity.setUploadUserId(0L);
        entity.setUploadUserName("system");
        entity.setUploadTime(now);
        entity.setUploadSourceFileUrl(sourceFileUrl);
        entity.setRecordStatus(2);
        entity.setAiAnalysisStatus(0);
        entity.setReportStatus(0);
        entity.setDisplayStatus(0);
        entity.setAiConclusionShort(null);
        entity.setIsDeleted(0);

        ecgDataMapper.insertCollectionRecord(entity);

        EcgDataUploadResultVo resultVo = new EcgDataUploadResultVo();
        resultVo.setEcgId(recordId);
        resultVo.setEcgNo(ecgNo);
        resultVo.setStatus("待分析");
        resultVo.setAiQueueTriggered(true);
        resultVo.setUploadTime(now);
        return resultVo;
    }

    @Override
    public WorkbenchEcgDetailVo getEcgDetail(Long ecgId) {
        if (ecgId == null || ecgId <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "ecgId parameter is invalid");
        }

        WorkbenchEcgDetailVo detailVo = workbenchMapper.selectEcgDetail(ecgId);
        if (detailVo == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "ecg data not found");
        }

        Integer samplingRate = detailVo.getSamplingRate() == null ? 250 : detailVo.getSamplingRate();
        detailVo.setSamplingRate(samplingRate);
        detailVo.setWaveformPreview(buildPreview(ecgId, samplingRate));
        if (!StringUtils.hasText(detailVo.getAiConclusion()) && StringUtils.hasText(detailVo.getAiConclusionShort())) {
            detailVo.setAiConclusion(detailVo.getAiConclusionShort());
        }
        return detailVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EcgDataStatusUpdateResultVo updateStatus(EcgDataStatusUpdateDto updateDto) {
        if (updateDto == null || updateDto.getEcgId() == null || updateDto.getEcgId() <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "ecgId parameter is invalid");
        }

        Long count = ecgDataMapper.countEcgById(updateDto.getEcgId());
        if (count == null || count <= 0) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "ecg data not found");
        }

        StatusMapping mapping = parseTargetStatus(updateDto.getTargetStatus());
        ecgDataMapper.updateEcgStatus(updateDto.getEcgId(), mapping.getAiStatus(), mapping.getReportStatus(), mapping.getDisplayStatus());

        EcgDataStatusUpdateResultVo resultVo = new EcgDataStatusUpdateResultVo();
        resultVo.setEcgId(updateDto.getEcgId());
        resultVo.setTargetStatus(mapping.getStatusText());
        resultVo.setUpdated(true);
        return resultVo;
    }

    @Override
    public EcgDataDictVo getDicts() {
        EcgDataDictVo dictVo = new EcgDataDictVo();

        List<DictOptionVo> wardOptions = ecgDataMapper.selectWardOptions();
        if (wardOptions == null) {
            wardOptions = new ArrayList<>();
        } else {
            wardOptions = new ArrayList<>(wardOptions);
        }
        wardOptions.add(0, buildOption("", "全部病区"));

        List<DictOptionVo> deviceOptions = ecgDataMapper.selectDeviceOptions();
        if (deviceOptions == null) {
            deviceOptions = new ArrayList<>();
        } else {
            deviceOptions = new ArrayList<>(deviceOptions);
        }
        deviceOptions.add(0, buildOption("", "全部设备"));

        List<DictOptionVo> statusOptions = new ArrayList<>();
        statusOptions.add(buildOption("", "全部状态"));
        statusOptions.add(buildOption("待分析", "待分析"));
        statusOptions.add(buildOption("已分析", "已分析"));
        statusOptions.add(buildOption("已审核", "已审核"));

        dictVo.setWardOptions(wardOptions);
        dictVo.setDeviceOptions(deviceOptions);
        dictVo.setStatusOptions(statusOptions);
        return dictVo;
    }

    @Override
    public EcgDataWaveformVo getWaveform(Long ecgId) {
        if (ecgId == null || ecgId <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "ecgId parameter is invalid");
        }

        EcgDataWaveformVo waveformVo = ecgDataMapper.selectWaveformMeta(ecgId);
        if (waveformVo == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "ecg data not found");
        }

        int samplingRate = waveformVo.getSamplingRate() == null ? 250 : waveformVo.getSamplingRate();
        waveformVo.setSamplingRate(samplingRate);
        if (waveformVo.getLeadCount() == null) {
            waveformVo.setLeadCount(12);
        }
        waveformVo.setPoints(buildWaveform(ecgId, samplingRate));
        return waveformVo;
    }

    private EcgDataPageQueryDto normalizePageQuery(EcgDataPageQueryDto queryDto) {
        EcgDataPageQueryDto query = queryDto == null ? new EcgDataPageQueryDto() : queryDto;
        query.setKeyword(trimToNull(query.getKeyword()));
        query.setStatus(trimToNull(query.getStatus()));
        query.setStartTime(trimToNull(query.getStartTime()));
        query.setEndTime(trimToNull(query.getEndTime()));
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

    private DateRange resolveDateRange(String startTimeText, String endTimeText) {
        LocalDateTime startTime = parseDateTime(startTimeText, false);
        LocalDateTime endTime = parseDateTime(endTimeText, true);

        if (startTime == null && endTime == null) {
            return new DateRange(null, null);
        }
        if (startTime == null) {
            startTime = endTime.minusDays(1);
        }
        if (endTime == null) {
            endTime = LocalDateTime.now().plusSeconds(1);
        }
        if (startTime.isAfter(endTime)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "startTime cannot be later than endTime");
        }
        return new DateRange(startTime, endTime);
    }

    private Integer parseStatusCode(String status, boolean required) {
        if (!StringUtils.hasText(status)) {
            if (required) {
                throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "status parameter is invalid");
            }
            return null;
        }

        String value = status.trim();
        if ("0".equals(value) || "待分析".equals(value) || "pending".equalsIgnoreCase(value)) {
            return 0;
        }
        if ("1".equals(value) || "已分析".equals(value) || "analysed".equalsIgnoreCase(value) || "analyzed".equalsIgnoreCase(value)) {
            return 1;
        }
        if ("2".equals(value) || "已审核".equals(value) || "audited".equalsIgnoreCase(value)) {
            return 2;
        }
        throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "status parameter is invalid");
    }

    private StatusMapping parseTargetStatus(String targetStatus) {
        if (!StringUtils.hasText(targetStatus)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "targetStatus parameter is invalid");
        }

        String value = targetStatus.trim();
        if ("0".equals(value) || "待分析".equals(value) || "pending".equalsIgnoreCase(value)) {
            return new StatusMapping(0, 0, 0, "待分析");
        }
        if ("1".equals(value) || "已分析".equals(value) || "analysed".equalsIgnoreCase(value) || "analyzed".equalsIgnoreCase(value)) {
            return new StatusMapping(2, 0, 1, "已分析");
        }
        if ("2".equals(value) || "已审核".equals(value) || "audited".equalsIgnoreCase(value)) {
            return new StatusMapping(2, 2, 2, "已审核");
        }
        throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "targetStatus parameter is invalid");
    }

    private LocalDateTime parseDateTime(String value, boolean endExclusive) {
        if (!StringUtils.hasText(value)) {
            return null;
        }

        String trimValue = value.trim();
        if (trimValue.length() == 10) {
            try {
                LocalDate date = LocalDate.parse(trimValue, DateTimeFormatter.ISO_LOCAL_DATE);
                return endExclusive ? date.plusDays(1).atStartOfDay() : date.atStartOfDay();
            } catch (DateTimeParseException ex) {
                throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "time format is invalid");
            }
        }

        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                LocalDateTime dateTime = LocalDateTime.parse(trimValue, formatter);
                return endExclusive ? dateTime.plusSeconds(1) : dateTime;
            } catch (DateTimeParseException ignored) {
                // try next formatter
            }
        }
        throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "time format is invalid");
    }

    private String buildEcgNo(Long recordId, LocalDateTime now) {
        long suffix = Math.abs(recordId % 10000);
        return "ECG" + now.format(ECG_NO_TIME_FORMAT) + String.format("%04d", suffix);
    }

    private String buildSourceFileUrl(MultipartFile file, Long recordId) {
        String originalName = file.getOriginalFilename();
        if (!StringUtils.hasText(originalName)) {
            originalName = "ecg_data.dat";
        }
        String sanitized = originalName.replaceAll("[^A-Za-z0-9._-]", "_");
        return "/upload/ecg/" + recordId + "_" + sanitized;
    }

    private List<WorkbenchWaveformPointVo> buildPreview(Long ecgId, Integer samplingRate) {
        int pointCount = 120;
        double offset = ecgId == null ? 0 : (ecgId % 67);
        double sampleDivider = samplingRate == null || samplingRate <= 0 ? 10.0 : Math.max(samplingRate / 24.0, 6.0);

        List<WorkbenchWaveformPointVo> points = new ArrayList<>(pointCount);
        for (int i = 0; i < pointCount; i++) {
            double angle = (i + offset) / sampleDivider;
            double value = Math.sin(angle) * 1.05 + Math.sin(angle * 0.28) * 0.22;
            value = Math.round(value * 10000D) / 10000D;
            points.add(new WorkbenchWaveformPointVo(i, value));
        }
        return points;
    }

    private List<EcgDataWaveformPointVo> buildWaveform(Long ecgId, Integer samplingRate) {
        int pointCount = 300;
        double offset = ecgId == null ? 0 : (ecgId % 89);
        double sampleDivider = samplingRate == null || samplingRate <= 0 ? 12.0 : Math.max(samplingRate / 28.0, 7.0);

        List<EcgDataWaveformPointVo> points = new ArrayList<>(pointCount);
        for (int i = 0; i < pointCount; i++) {
            double angle = (i + offset) / sampleDivider;
            double value = Math.sin(angle) * 1.2 + Math.sin(angle * 0.24) * 0.35;
            value = Math.round(value * 10000D) / 10000D;
            points.add(new EcgDataWaveformPointVo(i, value));
        }
        return points;
    }

    private DictOptionVo buildOption(String value, String label) {
        return DictOptionVo.builder().value(value).label(label).build();
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private static final class DateRange {
        private final LocalDateTime startTime;
        private final LocalDateTime endTime;

        private DateRange(LocalDateTime startTime, LocalDateTime endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
        }

        public LocalDateTime getStartTime() {
            return startTime;
        }

        public LocalDateTime getEndTime() {
            return endTime;
        }
    }

    private static final class StatusMapping {
        private final Integer aiStatus;
        private final Integer reportStatus;
        private final Integer displayStatus;
        private final String statusText;

        private StatusMapping(Integer aiStatus, Integer reportStatus, Integer displayStatus, String statusText) {
            this.aiStatus = aiStatus;
            this.reportStatus = reportStatus;
            this.displayStatus = displayStatus;
            this.statusText = statusText;
        }

        public Integer getAiStatus() {
            return aiStatus;
        }

        public Integer getReportStatus() {
            return reportStatus;
        }

        public Integer getDisplayStatus() {
            return displayStatus;
        }

        public String getStatusText() {
            return statusText;
        }
    }
}
