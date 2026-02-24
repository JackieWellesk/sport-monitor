package geist.hegel.controller;

import geist.hegel.dto.HealthReportResponse;
import geist.hegel.service.HealthReportService;
import geist.hegel.utils.UserUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/health-report")
public class HealthReportController {

    @Resource
    private HealthReportService healthReportService;

    /**
     * 示例：GET /api/health-report?userId=6&months=5
     */
    @GetMapping
    public HealthReportResponse getReport(
                                          @RequestParam(defaultValue = "5") int months) {
        return healthReportService.generate(UserUtils.getUserId(), months);
    }
}