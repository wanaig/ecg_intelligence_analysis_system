package com.hnkjzy.ecg_collection.mapper.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnkjzy.ecg_collection.mapper.BaseMapperX;
import com.hnkjzy.ecg_collection.model.dto.system.SystemDepartmentPageQueryDto;
import com.hnkjzy.ecg_collection.model.entity.system.SysDepartmentEntity;
import com.hnkjzy.ecg_collection.model.vo.system.SystemDepartmentDetailVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemDepartmentPageItemVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemDepartmentSaveResultVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemDepartmentTreeItemVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SystemDepartmentMapper extends BaseMapperX<SysDepartmentEntity> {

    IPage<SystemDepartmentPageItemVo> selectDepartmentPage(Page<SystemDepartmentPageItemVo> page,
                                                           @Param("req") SystemDepartmentPageQueryDto req,
                                                           @Param("statusCode") Integer statusCode);

    List<SystemDepartmentTreeItemVo> selectDepartmentTreeList();

    List<SystemDepartmentTreeItemVo> selectDepartmentTreeAll();

    Long countDepartmentName(@Param("deptName") String deptName,
                             @Param("parentDeptId") Long parentDeptId,
                             @Param("excludeDeptId") Long excludeDeptId);

    String selectDepartmentNameById(@Param("deptId") Long deptId);

    String selectDepartmentCodeById(@Param("deptId") Long deptId);

    Long selectMaxDepartmentIdInRangeForUpdate(@Param("maxAllowedId") Long maxAllowedId);

    Long selectMaxSortByParent(@Param("parentDeptId") Long parentDeptId);

    int insertDepartment(SysDepartmentEntity entity);

    int updateDepartmentById(SysDepartmentEntity entity);

    SystemDepartmentDetailVo selectDepartmentDetail(@Param("deptId") Long deptId);

    SystemDepartmentSaveResultVo selectDepartmentSaveResult(@Param("deptId") Long deptId);

    Long countChildDepartments(@Param("deptId") Long deptId);

    Long countUsersByDepartmentId(@Param("deptId") Long deptId);

    Long countBedsByDepartmentId(@Param("deptId") Long deptId);

    Long countDevicesByDepartmentId(@Param("deptId") Long deptId);

    int logicalDeleteDepartment(@Param("deptId") Long deptId);

    int updateDepartmentStatusById(@Param("deptId") Long deptId,
                                   @Param("status") Integer status);

    int updateDirectorUserByDeptId(@Param("deptId") Long deptId,
                                   @Param("deptDirector") String deptDirector,
                                   @Param("contactPhone") String contactPhone);
}
