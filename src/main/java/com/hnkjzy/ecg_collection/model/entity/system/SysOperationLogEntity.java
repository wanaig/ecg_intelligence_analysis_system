package com.hnkjzy.ecg_collection.model.entity.system;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统操作日志实体。
 */
@Data
@TableName("sys_operation_log")
public class SysOperationLogEntity {

    @TableId("log_id")
    private Long logId;

    @TableField("user_id")
    private Long userId;

    @TableField("real_name")
    private String realName;

    @TableField("module")
    private String module;

    @TableField("operation_type")
    private String operationType;

    @TableField("operation_content")
    private String operationContent;

    @TableField("request_ip")
    private String requestIp;

    @TableField("operation_time")
    private LocalDateTime operationTime;

    @TableField("is_deleted")
    private Integer isDeleted;
}
