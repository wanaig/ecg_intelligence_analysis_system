package com.hnkjzy.ecg_collection.model.vo.analysis;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * AI diagnosis dashboard trend data.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AiDiagnosisTrendVo extends BaseVo {

    private List<String> dateList;
    private List<Long> pendingAuditList;
    private List<Long> passList;
    private List<Long> rejectList;
}
