package com.hnkjzy.ecg_collection.model.dto.workbench;

import com.hnkjzy.ecg_collection.model.dto.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ECG data status update request.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EcgDataStatusUpdateDto extends BaseDto {

    private Long ecgId;
    private String targetStatus;
}
