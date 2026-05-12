package com.clinicwise.backend.controller;

import com.clinicwise.backend.api.enums.DashboardDateRangePreset;
import com.clinicwise.backend.api.response.ApiResponse;
import com.clinicwise.backend.dto.response.DashboardResponse;
import com.clinicwise.backend.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {
    private DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ApiResponse<DashboardResponse> getDashboardData(@RequestParam(name = "date_range_preset") DashboardDateRangePreset dateRangePreset) {
        return dashboardService.getDashboardData(dateRangePreset);
    }
}
