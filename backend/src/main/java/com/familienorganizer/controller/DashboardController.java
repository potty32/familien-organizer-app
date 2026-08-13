package com.familienorganizer.controller;

import com.familienorganizer.dto.DashboardResponse;
import com.familienorganizer.dto.PointTransactionResponse;
import com.familienorganizer.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/api/v1/dashboard")
    public DashboardResponse getDashboard() {
        return dashboardService.getDashboard();
    }

    @GetMapping("/api/v1/users/{id}/points")
    public List<PointTransactionResponse> getPointHistory(@PathVariable UUID id) {
        return dashboardService.getPointHistory(id);
    }
}
