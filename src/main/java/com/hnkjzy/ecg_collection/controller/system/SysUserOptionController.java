package com.hnkjzy.ecg_collection.controller.system;

import com.hnkjzy.ecg_collection.common.result.ApiResponse;
import com.hnkjzy.ecg_collection.controller.BaseController;
import com.hnkjzy.ecg_collection.model.vo.system.SystemTestUserOptionVo;
import com.hnkjzy.ecg_collection.service.system.SystemUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 系统用户下拉扩展接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sys/user")
public class SysUserOptionController extends BaseController {

    private final SystemUserService systemUserService;

    @GetMapping("/testUserOptions")
    public ApiResponse<List<SystemTestUserOptionVo>> testUserOptions() {
        return ApiResponse.success(systemUserService.listTestUserOptions());
    }
}
