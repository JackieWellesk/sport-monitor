package geist.hegel.health;

import geist.hegel.dto.RunMonthAgg;
import geist.hegel.dto.TrendMonthAgg;
import geist.hegel.dto.TrendResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TrendAnalyzer {

    public TrendResponse build(List<TrendMonthAgg> monthAggs, List<RunMonthAgg> runAggs) {
        TrendResponse resp = new TrendResponse();
        resp.setMonthly(monthAggs);
        resp.setRunning(runAggs);

        TrendResponse.TrendSummary s = new TrendResponse.TrendSummary();
        // 简单趋势判定：比较首月 vs 末月
        if (monthAggs != null && monthAggs.size() >= 2) {
            TrendMonthAgg first = monthAggs.get(0);
            TrendMonthAgg last = monthAggs.get(monthAggs.size() - 1);

            s.frequencyUp = safeInt(last.getTotalSessions()) > safeInt(first.getTotalSessions());
            s.durationUp = safeInt(last.getTotalMinutes()) > safeInt(first.getTotalMinutes());
        }
        if (runAggs != null && runAggs.size() >= 2) {
            RunMonthAgg first = runAggs.get(0);
            RunMonthAgg last = runAggs.get(runAggs.size() - 1);
            s.heartRateDown = safeDouble(last.getAvgHeartRate()) < safeDouble(first.getAvgHeartRate());
        }
        s.text = buildSummaryText(s);
        resp.setSummary(s);
        return resp;
    }

    private String buildSummaryText(TrendResponse.TrendSummary s) {
        StringBuilder sb = new StringBuilder();
        sb.append(s.frequencyUp ? "运动频率提升；" : "运动频率稳定/偏低；");
        sb.append(s.durationUp ? "运动时长增加；" : "运动时长稳定/偏低；");
        sb.append(s.heartRateDown ? "跑步心率下降（有氧改善迹象）。" : "跑步心率未明显下降。");
        return sb.toString();
    }

    private int safeInt(Integer v) { return v == null ? 0 : v; }
    private double safeDouble(Double v) { return v == null ? 0d : v; }
}