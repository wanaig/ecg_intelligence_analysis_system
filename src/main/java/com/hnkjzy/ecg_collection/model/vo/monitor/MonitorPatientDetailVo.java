package com.hnkjzy.ecg_collection.model.vo.monitor;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 单患者实时监护详情。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorPatientDetailVo extends BaseVo {

    private Long patientId;
    private String patientName;
    private String inpatientNo;
    private String wardName;
    private String bedNo;
    private String wardBed;
    private String primaryDiagnosis;

    private Integer currentHeartRate;
    private Integer monitorStatus;
    private String monitorStatusText;
    private Integer warningLevel;
    private String warningLevelText;
    private LocalDateTime updateTime;

    private List<MonitorEcgTrendPointVo> ecgTrend;
    private List<MonitorWarningHistoryVo> warningHistory;
    private List<MonitorContinuousRecordVo> continuousRecords;
}
