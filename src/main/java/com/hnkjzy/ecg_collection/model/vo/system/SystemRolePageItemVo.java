package com.hnkjzy.ecg_collection.model.vo.system;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 瑙掕壊鍒嗛〉鍒楄〃椤广€?
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SystemRolePageItemVo extends BaseVo {

    private Long roleId;
    private String roleName;
    private String description;
    private Integer userCount;
    private LocalDateTime createTime;
    private Integer status;
    private String statusText;
}

