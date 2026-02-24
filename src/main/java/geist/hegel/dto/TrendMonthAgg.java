package geist.hegel.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TrendMonthAgg {
    private String month;           // yyyy-MM
    private Integer totalSessions;
    private Integer totalMinutes;
    private Integer totalCalories;
    private BigDecimal totalDistance;

    // getter/setter
}
