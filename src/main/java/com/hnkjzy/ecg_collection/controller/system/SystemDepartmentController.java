package com.hnkjzy.ecg_collection.controller.system;

import com.hnkjzy.ecg_collection.common.result.ApiResponse;
import com.hnkjzy.ecg_collection.controller.BaseController;
import com.hnkjzy.ecg_collection.model.dto.system.SystemDepartmentCreateDto;
import com.hnkjzy.ecg_collection.model.dto.system.SystemDepartmentPageQueryDto;
import com.hnkjzy.ecg_collection.model.dto.system.SystemDepartmentUpdateDto;
import com.hnkjzy.ecg_collection.model.vo.common.PageResultVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemDepartmentDeleteResultVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemDepartmentDetailVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemDepartmentDictVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemDepartmentPageItemVo;
import com.hnkjzy.ecg_collection.model.vo.system.SystemDepartmentSaveResultVo;
import com.hnkjzy.ecg_collection.service.system.SystemDepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/system/department")
public class SystemDepartmentController extends BaseController {

    private final SystemDepartmentService systemDepartmentService;

    @PostMapping("/page")
    public ApiResponse<PageResultVo<SystemDepartmentPageItemVo>> page(@RequestBody(required = false) SystemDepartmentPageQueryDto queryDto) {
        return ApiResponse.success(systemDepartmentService.pageDepartments(queryDto));
    }

    @GetMapping("/dicts")
    public ApiResponse<SystemDepartmentDictVo> dicts() {
        return ApiResponse.success(systemDepartmentService.getDepartmentDicts());
    }

    @PostMapping
    public ApiResponse<SystemDepartmentSaveResultVo> create(@RequestBody SystemDepartmentCreateDto createDto) {
        return ApiResponse.success(systemDepartmentService.createDepartment(createDto));
    }

    @GetMapping("/detail")
    public ApiResponse<SystemDepartmentDetailVo> detail(@RequestParam("deptId") Long deptId) {
        return ApiResponse.success(systemDepartmentService.getDepartmentDetail(deptId));
    }

    @PutMapping
    public ApiResponse<SystemDepartmentSaveResultVo> update(@RequestBody SystemDepartmentUpdateDto updateDto) {
        return ApiResponse.success(systemDepartmentService.updateDepartment(updateDto));
    }

    @DeleteMapping
    public ApiResponse<SystemDepartmentDeleteResultVo> delete(@RequestParam("deptId") Long deptId,
                                                               @RequestParam(value = "force", required = false) Boolean force) {
        return ApiResponse.success(systemDepartmentService.deleteDepartment(deptId, force));
    }
}
