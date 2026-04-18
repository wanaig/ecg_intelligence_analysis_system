package com.hnkjzy.ecg_collection.service.system;

import com.hnkjzy.ecg_collection.model.dto.system.SystemDepartmentCreateDto;
import com.hnkjzy.ecg_collection.model.dto.system.SystemDepartmentPageQueryDto;
import com.hnkjzy.ecg_collection.model.dto.system.SystemDepartmentUpdateDto;
import com.hnkjzy.ecg_collection.model.vo.common.PageResultVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemDepartmentDeleteResultVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemDepartmentDetailVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemDepartmentDictVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemDepartmentPageItemVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemDepartmentSaveResultVo;
import com.hnkjzy.ecg_collection.service.BaseService;

public interface SystemDepartmentService extends BaseService {

    PageResultVo<SystemDepartmentPageItemVo> pageDepartments(SystemDepartmentPageQueryDto queryDto);

    SystemDepartmentDictVo getDepartmentDicts();

    SystemDepartmentSaveResultVo createDepartment(SystemDepartmentCreateDto createDto);

    SystemDepartmentDetailVo getDepartmentDetail(Long deptId);

    SystemDepartmentSaveResultVo updateDepartment(SystemDepartmentUpdateDto updateDto);

    SystemDepartmentDeleteResultVo deleteDepartment(Long deptId, Boolean force);
}
