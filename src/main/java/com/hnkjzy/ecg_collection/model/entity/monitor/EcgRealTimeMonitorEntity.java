package com.hnkjzy.ecg_collection.model.entity.monitor;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 实时监护实体。
 */
@Data
@TableName("ecg_real_time_monitor")
public class EcgRealTimeMonitorEntity {

    @TableId("monitor_id")
    private Long monitorId;

    @TableField("patient_id")
    private Long patientId;

    @TableField("is_deleted")
    private Integer isDeleted;
}
