package com.hnkjzy.ecg_collection.controller.warning;

import com.hnkjzy.ecg_collection.common.result.ApiResponse;
import com.hnkjzy.ecg_collection.controller.BaseController;
import com.hnkjzy.ecg_collection.model.dto.warning.WarningHandleDto;
import com.hnkjzy.ecg_collection.model.dto.warning.WarningPageQueryDto;
import com.hnkjzy.ecg_collection.model.vo.common.PageResultVo;
import com.hnkjzy.ecg_collection.model.vo.warning.WarningDetailVo;
import com.hnkjzy.ecg_collection.model.vo.warning.WarningDictVo;
import com.hnkjzy.ecg_collection.model.vo.warning.WarningGlobalStatVo;
import com.hnkjzy.ecg_collection.model.vo.warning.WarningHandleResultVo;
import com.hnkjzy.ecg_collection.model.vo.warning.WarningPageItemVo;
import com.hnkjzy.ecg_collection.service.warning.WarningService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 预警接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/warning")
public class WarningController extends BaseController {

    private final WarningService warningService;

    @GetMapping("/statistics")
    public ApiResponse<WarningGlobalStatVo> statistics() {
        return ApiResponse.success(warningService.getGlobalStat());
    }

    @PostMapping("/page")
    public ApiResponse<PageResultVo<WarningPageItemVo>> page(@RequestBody(required = false) WarningPageQueryDto queryDto) {
        return ApiResponse.success(warningService.pageWarningList(queryDto));
    }

    @GetMapping("/detail")
    public ApiResponse<WarningDetailVo> detail(@RequestParam("alertId") Long alertId) {
        return ApiResponse.success(warningService.getWarningDetail(alertId));
    }

    @PostMapping("/handle")
    public ApiResponse<WarningHandleResultVo> handle(@RequestBody WarningHandleDto handleDto) {
        return ApiResponse.success(warningService.handleWarning(handleDto));
    }

    @GetMapping("/dicts")
    public ApiResponse<WarningDictVo> dicts() {
        return ApiResponse.success(warningService.getWarningDicts());
    }
}
