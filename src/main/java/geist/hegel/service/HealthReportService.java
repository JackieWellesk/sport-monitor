package geist.hegel.service;

import geist.hegel.dto.HealthReportResponse;

public interface HealthReportService {

    HealthReportResponse generate(Long userId, int months);

}
