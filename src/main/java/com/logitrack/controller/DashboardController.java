package com.logitrack.controller;

import com.logitrack.common.ApiResponse;
import com.logitrack.dto.dashboard.DashboardResponseDTO;
import com.logitrack.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<DashboardResponseDTO>> getDashboard(
            @RequestParam(name = "vehicleId", required = false) Long vehicleId) {
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.buildDashboard(Optional.ofNullable(vehicleId))));
    }
}
