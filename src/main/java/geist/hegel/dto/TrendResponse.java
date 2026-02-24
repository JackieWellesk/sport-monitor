package geist.hegel.dto;

import lombok.Data;

import java.util.List;

@Data
public class TrendResponse {
    private List<TrendMonthAgg> monthly;   // 总体趋势
    private List<RunMonthAgg> running;     // 跑步趋势
    private TrendSummary summary;          // 可选：简要结论

    // getter/setter

    public static class TrendSummary {
        public boolean frequencyUp;
        public boolean durationUp;
        public boolean heartRateDown; // 跑步平均心率下降代表有氧改善（粗略）
        public String text;           // 一句话总结
    }
}
