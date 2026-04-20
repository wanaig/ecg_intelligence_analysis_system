package com.hnkjzy.ecg_collection.controller.workbench;

import com.hnkjzy.ecg_collection.common.result.ApiResponse;
import com.hnkjzy.ecg_collection.controller.BaseController;
import com.hnkjzy.ecg_collection.model.dto.workbench.WorkbenchPageQueryDto;
import com.hnkjzy.ecg_collection.model.dto.workbench.WorkbenchTimeQueryDto;
import com.hnkjzy.ecg_collection.model.vo.workbench.WorkbenchAlertDetailVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.WorkbenchDictVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.WorkbenchEcgDetailVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.WorkbenchLatestEcgItemVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.WorkbenchOverviewStatVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.WorkbenchPageListVo;
import com.hnkjzy.ecg_collection.model.vo.workbench.WorkbenchPendingAlertItemVo;
import com.hnkjzy.ecg_collection.service.workbench.WorkbenchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Workbench APIs.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/workbench", "/workbench"})
public class WorkbenchController extends BaseController {

    private final WorkbenchService workbenchService;

    @GetMapping("/overview")
    public ApiResponse<WorkbenchOverviewStatVo> overview(WorkbenchTimeQueryDto queryDto) {
        return ApiResponse.success(workbenchService.getOverview(queryDto));
    }

    @PostMapping("/pending-alerts/page")
    public ApiResponse<WorkbenchPageListVo<WorkbenchPendingAlertItemVo>> pendingAlerts(
            @RequestBody(required = false) WorkbenchPageQueryDto queryDto) {
        return ApiResponse.success(workbenchService.pagePendingAlerts(queryDto));
    }

    @PostMapping("/latest-ecg/page")
    public ApiResponse<WorkbenchPageListVo<WorkbenchLatestEcgItemVo>> latestEcg(
            @RequestBody(required = false) WorkbenchPageQueryDto queryDto) {
        return ApiResponse.success(workbenchService.pageLatestEcg(queryDto));
    }

    @GetMapping("/alerts/{alertId}")
    public ApiResponse<WorkbenchAlertDetailVo> alertDetail(@PathVariable("alertId") Long alertId) {
        return ApiResponse.success(workbenchService.getAlertDetail(alertId));
    }

    @GetMapping("/ecg/{ecgId}")
    public ApiResponse<WorkbenchEcgDetailVo> ecgDetail(@PathVariable("ecgId") Long ecgId) {
        return ApiResponse.success(workbenchService.getEcgDetail(ecgId));
    }

    @GetMapping("/dicts")
    public ApiResponse<WorkbenchDictVo> dicts() {
        return ApiResponse.success(workbenchService.getDicts());
    }
}
