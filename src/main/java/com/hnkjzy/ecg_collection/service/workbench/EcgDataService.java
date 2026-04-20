package com.hnkjzy.ecg_collection.service.workbench;

import com.hnkjzy.ecg_collection.model.dto.workbench.EcgDataPageQueryDto;
import com.hnkjzy.ecg_collection.model.dto.workbench.EcgDataStatusUpdateDto;
import com.hnkjzy.ecg_collection.model.vo.workbench.EcgDataDictVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.EcgDataPageItemVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.EcgDataStatusUpdateResultVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.EcgDataUploadResultVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.EcgDataWaveformVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.WorkbenchEcgDetailVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.WorkbenchPageListVo;
import com.hnkjzy.ecg_collection.service.BaseService;
import org.springframework.web.multipart.MultipartFile;

/**
 * ECG data list service.
 */
public interface EcgDataService extends BaseService {

    WorkbenchPageListVo<EcgDataPageItemVo> pageEcgData(EcgDataPageQueryDto queryDto);

    EcgDataUploadResultVo uploadData(MultipartFile file, Long deviceId, Long patientId);

    WorkbenchEcgDetailVo getEcgDetail(Long ecgId);

    EcgDataStatusUpdateResultVo updateStatus(EcgDataStatusUpdateDto updateDto);

    EcgDataDictVo getDicts();

    EcgDataWaveformVo getWaveform(Long ecgId);
}
