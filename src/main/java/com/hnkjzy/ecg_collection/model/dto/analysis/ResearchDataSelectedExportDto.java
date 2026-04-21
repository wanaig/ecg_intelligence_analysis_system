package com.hnkjzy.ecg_collection.model.dto.analysis;

import com.hnkjzy.ecg_collection.model.dto.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Selected research records export request.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ResearchDataSelectedExportDto extends BaseDto {

    private List<Long> researchIdList;
}
