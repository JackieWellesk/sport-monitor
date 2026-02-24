package geist.hegel.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RunMonthAgg {
    private String month;           // yyyy-MM
    private BigDecimal avgDistance;
    private Double avgDuration;
    private Double avgHeartRate;

    // getter/setter
}