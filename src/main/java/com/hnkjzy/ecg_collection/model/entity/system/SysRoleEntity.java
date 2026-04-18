package com.hnkjzy.ecg_collection.model.entity.system;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 绯荤粺瑙掕壊瀹炰綋銆?
 */
@Data
@TableName("sys_role")
public class SysRoleEntity {

    @TableId("role_id")
    private Long roleId;

    @TableField("role_name")
    private String roleName;

    @TableField("description")
    private String description;

    @TableField("user_count")
    private Integer userCount;

    @TableField("status")
    private Integer status;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField("is_deleted")
    private Integer isDeleted;
}

