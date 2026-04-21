package com.hnkjzy.ecg_collection.service.analysis;

import com.hnkjzy.ecg_collection.model.dto.analysis.ResearchDataPageQueryDto;
import com.hnkjzy.ecg_collection.model.dto.analysis.ResearchDataSelectedExportDto;
import com.hnkjzy.ecg_collection.model.vo.analysis.ResearchDataExportFileVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.ResearchDataPageResultVo;
import com.hnkjzy.ecg_collection.service.BaseService;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Research data management service.
 */
public interface ResearchDataManagerService extends BaseService {

    ResearchDataPageResultVo pageResearchData(ResearchDataPageQueryDto queryDto);

    ResearchDataExportFileVo exportSelectedMaskedData(ResearchDataSelectedExportDto exportDto, HttpServletRequest request);

    ResearchDataExportFileVo exportFilteredMaskedData(ResearchDataPageQueryDto queryDto, HttpServletRequest request);
}
