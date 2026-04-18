package com.hnkjzy.ecg_collection.model.entity.warning;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 异常预警实体。
 */
@Data
@TableName("ecg_abnormal_warning")
public class EcgAbnormalWarningEntity {

    @TableId("warning_id")
    private Long warningId;

    @TableField("handle_status")
    private Integer handleStatus;

    @TableField("is_deleted")
    private Integer isDeleted;
}
