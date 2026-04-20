package com.hnkjzy.ecg_collection.model.vo.analysis;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AI diagnosis raw waveform response.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AiDiagnosisWaveformVo extends BaseVo {

    private Long ecgId;
    private String ecgNo;
    private Integer samplingRate;
    private Integer leadCount;
    private String rawDataFileUrl;
    private LocalDateTime collectStartTime;
    private LocalDateTime collectEndTime;
    private List<AiDiagnosisWaveformPointVo> waveform;
    private List<AiDiagnosisWaveformSegmentVo> segments;
    private List<AiDiagnosisWaveformAnnotationVo> annotations;
}
