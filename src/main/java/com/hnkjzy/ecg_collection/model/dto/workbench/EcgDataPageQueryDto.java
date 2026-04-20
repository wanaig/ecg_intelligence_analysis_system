package com.hnkjzy.ecg_collection.model.dto.workbench;

import com.hnkjzy.ecg_collection.model.dto.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ECG data page query.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EcgDataPageQueryDto extends BaseDto {

    private String keyword;
    private Long wardId;
    private String status;
    private String startTime;
    private String endTime;
    private Long pageNum;
    private Long pageSize;
}
