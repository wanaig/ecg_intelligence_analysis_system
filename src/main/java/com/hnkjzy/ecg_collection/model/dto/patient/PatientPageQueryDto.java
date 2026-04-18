package com.hnkjzy.ecg_collection.model.dto.patient;

import com.hnkjzy.ecg_collection.model.dto.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 患者列表分页查询请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PatientPageQueryDto extends BaseDto {

    private String keyword;
    private String ward;
    private String riskLevel;
    private String patientStatus;
    private Long pageNum;
    private Long pageSize;
}
