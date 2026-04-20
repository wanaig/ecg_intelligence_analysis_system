package com.hnkjzy.ecg_collection.model.entity.workbench;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * ECG collection record entity.
 */
@Data
@TableName("ecg_collection_record")
public class EcgCollectionRecordEntity {

    @TableId("record_id")
    private Long recordId;

    @TableField("ecg_no")
    private String ecgNo;

    @TableField("patient_id")
    private Long patientId;

    @TableField("patient_name")
    private String patientName;

    @TableField("gender")
    private String gender;

    @TableField("age")
    private Integer age;

    @TableField("inpatient_no")
    private String inpatientNo;

    @TableField("dept_id")
    private Long deptId;

    @TableField("dept_name")
    private String deptName;

    @TableField("bed_no")
    private String bedNo;

    @TableField("device_id")
    private Long deviceId;

    @TableField("device_name")
    private String deviceName;

    @TableField("lead_count")
    private Integer leadCount;

    @TableField("collection_start_time")
    private LocalDateTime collectionStartTime;

    @TableField("collection_end_time")
    private LocalDateTime collectionEndTime;

    @TableField("sampling_rate")
    private Integer samplingRate;

    @TableField("ecg_data_file_url")
    private String ecgDataFileUrl;

    @TableField("collection_duration")
    private Integer collectionDuration;

    @TableField("collection_type")
    private Integer collectionType;

    @TableField("upload_user_id")
    private Long uploadUserId;

    @TableField("upload_user_name")
    private String uploadUserName;

    @TableField("upload_time")
    private LocalDateTime uploadTime;

    @TableField("upload_source_file_url")
    private String uploadSourceFileUrl;

    @TableField("record_status")
    private Integer recordStatus;

    @TableField("ai_analysis_status")
    private Integer aiAnalysisStatus;

    @TableField("report_status")
    private Integer reportStatus;

    @TableField("display_status")
    private Integer displayStatus;

    @TableField("ai_conclusion_short")
    private String aiConclusionShort;

    @TableField("is_deleted")
    private Integer isDeleted;
}
