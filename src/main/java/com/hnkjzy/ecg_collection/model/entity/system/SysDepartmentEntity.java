package com.hnkjzy.ecg_collection.model.entity.system;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_department")
public class SysDepartmentEntity {

    @TableId("dept_id")
    private Long deptId;

    @TableField("parent_id")
    private Long parentId;

    @TableField("parent_name")
    private String parentName;

    @TableField("dept_name")
    private String deptName;

    @TableField("dept_code")
    private String deptCode;

    @TableField("dept_type")
    private Integer deptType;

    @TableField("sort")
    private Integer sort;

    @TableField("status")
    private Integer status;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField("is_deleted")
    private Integer isDeleted;
}
