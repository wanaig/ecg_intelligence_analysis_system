package com.hnkjzy.ecg_collection.model.entity.monitor;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 设备质控记录实体。
 */
@Data
@TableName("ecg_device_quality_control")
public class EcgDeviceQualityControlEntity {

    @TableId("qc_id")
    private Long qcId;

    @TableField("device_id")
    private Long deviceId;

    @TableField("device_name")
    private String deviceName;

    @TableField("dept_id")
    private Long deptId;

    @TableField("dept_name")
    private String deptName;

    @TableField("test_time")
    private LocalDateTime testTime;

    @TableField("test_type")
    private String testType;

    @TableField("test_user_id")
    private Long testUserId;

    @TableField("test_user_name")
    private String testUserName;

    @TableField("device_status")
    private String deviceStatus;

    @TableField("test_result")
    private String testResult;

    @TableField("remark")
    private String remark;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField("is_deleted")
    private Integer isDeleted;
}
