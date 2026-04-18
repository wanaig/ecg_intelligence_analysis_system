package com.hnkjzy.ecg_collection.mapper.warning;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnkjzy.ecg_collection.mapper.BaseMapperX;
import com.hnkjzy.ecg_collection.model.dto.warning.WarningPageQueryDto;
import com.hnkjzy.ecg_collection.model.entity.warning.EcgAbnormalWarningEntity;
import com.hnkjzy.ecg_collection.model.vo.warning.WarningDetailVo;
import com.hnkjzy.ecg_collection.model.vo.warning.WarningGlobalStatVo;
import com.hnkjzy.ecg_collection.model.vo.warning.WarningPageItemVo;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * 预警查询 Mapper。
 */
public interface WarningMapper extends BaseMapperX<EcgAbnormalWarningEntity> {

    WarningGlobalStatVo selectGlobalStat();

    IPage<WarningPageItemVo> selectWarningPage(Page<WarningPageItemVo> page,
                                               @Param("req") WarningPageQueryDto req,
                                               @Param("alertLevelCode") Integer alertLevelCode,
                                               @Param("alertStatusCode") Integer alertStatusCode,
                                               @Param("startTime") LocalDateTime startTime,
                                               @Param("endTime") LocalDateTime endTime);

    WarningDetailVo selectWarningDetail(@Param("alertId") Long alertId);

    int updateHandleWarning(@Param("alertId") Long alertId,
                            @Param("handleUserName") String handleUserName,
                            @Param("handleRemark") String handleRemark);
}
