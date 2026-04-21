package com.hnkjzy.ecg_collection.mapper.analysis;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnkjzy.ecg_collection.model.dto.analysis.ResearchDataPageQueryDto;
import com.hnkjzy.ecg_collection.model.vo.analysis.ResearchDataExportRowVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.ResearchDataPageItemVo;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Research data management mapper.
 */
public interface ResearchDataManagerMapper {

    IPage<ResearchDataPageItemVo> selectResearchPage(Page<ResearchDataPageItemVo> page,
                                                     @Param("req") ResearchDataPageQueryDto req,
                                                     @Param("startTime") LocalDateTime startTime,
                                                     @Param("endTime") LocalDateTime endTime);

    List<ResearchDataExportRowVo> selectExportRowsByResearchIds(@Param("researchIdList") List<Long> researchIdList);

    List<ResearchDataExportRowVo> selectExportRowsByFilter(@Param("req") ResearchDataPageQueryDto req,
                                                           @Param("startTime") LocalDateTime startTime,
                                                           @Param("endTime") LocalDateTime endTime);

    int markExportedByResearchIds(@Param("researchIdList") List<Long> researchIdList);
}
