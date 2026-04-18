package com.hnkjzy.ecg_collection.model.dto.system;

import com.hnkjzy.ecg_collection.model.dto.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户分页查询请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SystemUserPageQueryDto extends BaseDto {

    private String keyword;
    private Long roleId;
    private Long deptId;
    private String status;
    private Long pageNum;
    private Long pageSize;
}
