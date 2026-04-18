package com.hnkjzy.ecg_collection.model.vo.warning;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 处理预警结果。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WarningHandleResultVo extends BaseVo {

    private Long alertId;
    private Integer alertStatus;
    private String alertStatusText;
    private LocalDateTime handleTime;
    private String handleRemark;
}
