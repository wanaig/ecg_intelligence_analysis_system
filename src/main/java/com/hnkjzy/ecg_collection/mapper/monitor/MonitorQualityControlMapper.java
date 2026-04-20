package com.hnkjzy.ecg_collection.mapper.monitor;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnkjzy.ecg_collection.mapper.BaseMapperX;
import com.hnkjzy.ecg_collection.model.dto.monitor.MonitorQualityControlPageQueryDto;
import com.hnkjzy.ecg_collection.model.entity.monitor.EcgDeviceQualityControlEntity;
import com.hnkjzy.ecg_collection.model.vo.common.DictOptionVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorQualityControlDetailVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorQualityControlDeviceInfoVo;
import com.hnkjzy.ecg_collection.model.vo.monitor.MonitorQualityControlPageItemVo;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 实时监护-质控管理 Mapper。
 */
public interface MonitorQualityControlMapper extends BaseMapperX<EcgDeviceQualityControlEntity> {

    IPage<MonitorQualityControlPageItemVo> selectQualityControlPage(Page<MonitorQualityControlPageItemVo> page,
                                                                    @Param("req") MonitorQualityControlPageQueryDto req,
                                                                    @Param("testResultText") String testResultText,
                                                                    @Param("testTypeText") String testTypeText,
                                                                    @Param("deviceStatusText") String deviceStatusText,
                                                                    @Param("startTime") LocalDateTime startTime,
                                                                    @Param("endTime") LocalDateTime endTime);

    List<DictOptionVo> selectNormalDeviceOptions();

    MonitorQualityControlDeviceInfoVo selectDeviceInfoById(@Param("deviceId") Long deviceId);

    String selectUserNameById(@Param("userId") Long userId);

    Long selectMaxQcIdInRangeForUpdate(@Param("maxAllowedId") Long maxAllowedId);

    int insertQualityControl(EcgDeviceQualityControlEntity entity);

    int updateQualityControlById(EcgDeviceQualityControlEntity entity);

    int logicalDeleteQualityControl(@Param("qcId") Long qcId);

    MonitorQualityControlDetailVo selectQualityControlDetail(@Param("qcId") Long qcId);
}
