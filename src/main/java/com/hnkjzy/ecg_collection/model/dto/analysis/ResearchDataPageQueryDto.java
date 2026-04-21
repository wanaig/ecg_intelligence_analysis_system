package com.hnkjzy.ecg_collection.model.dto.analysis;

import com.hnkjzy.ecg_collection.model.dto.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Research data page query.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ResearchDataPageQueryDto extends BaseDto {

    private String patientKeyword;
    private String emrKeyword;
    private String ecgKeyword;
    private Long deptId;
    private String startTime;
    private String endTime;
    private Long pageNum;
    private Long pageSize;
}
