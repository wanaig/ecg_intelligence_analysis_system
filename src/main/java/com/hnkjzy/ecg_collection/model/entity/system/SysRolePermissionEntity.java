package com.hnkjzy.ecg_collection.model.entity.system;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 瑙掕壊鏉冮檺鍏宠仈瀹炰綋銆?
 */
@Data
@TableName("sys_role_permission")
public class SysRolePermissionEntity {

    @TableId("id")
    private Long id;

    @TableField("role_id")
    private Long roleId;

    @TableField("permission_code")
    private String permissionCode;

    @TableField("permission_name")
    private String permissionName;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("is_deleted")
    private Integer isDeleted;
}

