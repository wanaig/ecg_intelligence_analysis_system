package com.hnkjzy.ecg_collection.model.dto.system;

import com.hnkjzy.ecg_collection.model.dto.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 瑙掕壊鍒嗛〉鏌ヨ璇锋眰銆?
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SystemRolePageQueryDto extends BaseDto {

    private String keyword;
    private Long pageNum;
    private Long pageSize;
}

