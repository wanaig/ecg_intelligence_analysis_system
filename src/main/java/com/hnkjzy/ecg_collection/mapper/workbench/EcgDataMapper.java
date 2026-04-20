package com.hnkjzy.ecg_collection.mapper.workbench;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnkjzy.ecg_collection.model.dto.workbench.EcgDataPageQueryDto;
import com.hnkjzy.ecg_collection.model.entity.workbench.EcgCollectionRecordEntity;
import com.hnkjzy.ecg_collection.model.vo.common.DictOptionVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.EcgDataDeviceSnapshotVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.EcgDataPageItemVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.EcgDataPatientSnapshotVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.EcgDataWaveformVo;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ECG data list mapper.
 */
public interface EcgDataMapper {

    IPage<EcgDataPageItemVo> selectEcgDataPage(Page<EcgDataPageItemVo> page,
                                               @Param("req") EcgDataPageQueryDto req,
                                               @Param("statusCode") Integer statusCode,
                                               @Param("startTime") LocalDateTime startTime,
                                               @Param("endTime") LocalDateTime endTime);

    EcgDataPatientSnapshotVo selectPatientSnapshot(@Param("patientName") String patientName,
                                                   @Param("inpatientNo") String inpatientNo);

    EcgDataDeviceSnapshotVo selectDeviceSnapshot(@Param("deviceNo") String deviceNo);

    Long selectMaxRecordIdInRangeForUpdate(@Param("maxAllowedId") Long maxAllowedId);

    int insertCollectionRecord(EcgCollectionRecordEntity entity);

    Long countEcgById(@Param("ecgId") Long ecgId);

    int updateEcgStatus(@Param("ecgId") Long ecgId,
                        @Param("aiStatus") Integer aiStatus,
                        @Param("reportStatus") Integer reportStatus,
                        @Param("displayStatus") Integer displayStatus);

    List<DictOptionVo> selectWardOptions();

    List<DictOptionVo> selectDeviceOptions();

    EcgDataWaveformVo selectWaveformMeta(@Param("ecgId") Long ecgId);
}
