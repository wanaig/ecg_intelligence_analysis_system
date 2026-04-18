package com.hnkjzy.ecg_collection.model.entity.analysis;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 统计结果实体。
 */
@Data
@TableName("ecg_statistics_result")
public class EcgStatisticsResultEntity {

    @TableId("stat_id")
    private Long statId;

    @TableField("stat_date")
    private LocalDate statDate;

    @TableField("stat_type")
    private Integer statType;

    @TableField("stat_dimension")
    private Integer statDimension;

    @TableField("dept_id")
    private Long deptId;

    @TableField("dept_name")
    private String deptName;

    @TableField("stat_value1")
    private Long statValue1;

    @TableField("stat_value2")
    private Long statValue2;

    @TableField("stat_value3")
    private Long statValue3;

    @TableField("stat_value4")
    private BigDecimal statValue4;

    @TableField("stat_extra")
    private String statExtra;

    @TableField("create_time")
    private LocalDateTime createTime;
}
