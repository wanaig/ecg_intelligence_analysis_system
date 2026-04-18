package com.hnkjzy.ecg_collection.model.entity.monitor;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;

/**
 * 设备实体。
 */
@Data
@TableName("ecg_device")
public class EcgDeviceEntity {

    @TableId("device_id")
    private Long deviceId;

    @TableField("device_code")
    private String deviceCode;

    @TableField("device_name")
    private String deviceName;

    @TableField("device_type")
    private Integer deviceType;

    @TableField("device_model")
    private String deviceModel;

    @TableField("manufacturer")
    private String manufacturer;

    @TableField("supplier")
    private String supplier;

    @TableField("install_date")
    private LocalDate installDate;

    @TableField("bind_dept_id")
    private Long bindDeptId;

    @TableField("bind_dept_name")
    private String bindDeptName;

    @TableField("last_maintain_time")
    private LocalDate lastMaintainTime;

    @TableField("next_maintain_time")
    private LocalDate nextMaintainTime;

    @TableField("device_status")
    private Integer deviceStatus;

    @TableField("is_deleted")
    private Integer isDeleted;
}
