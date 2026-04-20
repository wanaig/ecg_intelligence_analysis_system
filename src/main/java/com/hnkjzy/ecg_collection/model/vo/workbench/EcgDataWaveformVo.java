package com.hnkjzy.ecg_collection.model.vo.workbench;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Raw ECG waveform response.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EcgDataWaveformVo extends BaseVo {

    private Long ecgId;
    private String ecgNo;
    private Integer samplingRate;
    private Integer leadCount;
    private String rawDataFileUrl;
    private LocalDateTime collectStartTime;
    private LocalDateTime collectEndTime;
    private List<EcgDataWaveformPointVo> points;
}
