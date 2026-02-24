package geist.hegel.health;


import geist.hegel.dto.*;
import geist.hegel.entity.SportRecord;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class FitnessEvaluator {

    public FitnessEvaluation evaluate(TrendResponse trend, List<SportRecord> records) {
        FitnessEvaluation ev = new FitnessEvaluation();

        int aerobic = calcAerobic(trend);
        int consistency = calcConsistency(trend);
        int load = calcLoad(trend);

        int total = aerobic + consistency + load;

        ev.setAerobicScore(aerobic);
        ev.setConsistencyScore(consistency);
        ev.setLoadScore(load);
        ev.setTotalScore(total);
        ev.setLevel(levelOf(total));
        ev.setComment(buildComment(ev, trend));

        return ev;
    }

    // 有氧：看跑步平均距离 & 平均心率（粗评）
    private int calcAerobic(TrendResponse trend) {
        if (trend.getRunning() == null || trend.getRunning().isEmpty()) return 10;

        RunMonthAgg last = trend.getRunning().get(trend.getRunning().size() - 1);
        double hr = last.getAvgHeartRate() == null ? 0 : last.getAvgHeartRate();
        BigDecimal dist = last.getAvgDistance() == null ? BigDecimal.ZERO : last.getAvgDistance();

        int score = 0;
        // 距离贡献（0-20）
        if (dist.doubleValue() >= 10) score += 20;
        else if (dist.doubleValue() >= 8) score += 16;
        else if (dist.doubleValue() >= 5) score += 12;
        else score += 8;

        // 心率贡献（0-20）——越低越好（注意：这里只是“同强度下”粗略假设）
        if (hr > 0 && hr <= 130) score += 20;
        else if (hr <= 140) score += 16;
        else if (hr <= 150) score += 12;
        else score += 8;

        return clamp(score, 0, 40);
    }

    // 规律性：看末月运动次数
    private int calcConsistency(TrendResponse trend) {
        if (trend.getMonthly() == null || trend.getMonthly().isEmpty()) return 5;
        TrendMonthAgg last = trend.getMonthly().get(trend.getMonthly().size() - 1);
        int sessions = last.getTotalSessions() == null ? 0 : last.getTotalSessions();

        // 近似：按月次数 -> 每周次数（粗估：月/4）
        double perWeek = sessions / 4.0;

        if (perWeek >= 4) return 30;
        if (perWeek >= 3) return 25;
        if (perWeek >= 2) return 15;
        return 5;
    }

    // 负荷：看末月总分钟
    private int calcLoad(TrendResponse trend) {
        if (trend.getMonthly() == null || trend.getMonthly().isEmpty()) return 10;
        TrendMonthAgg last = trend.getMonthly().get(trend.getMonthly().size() - 1);
        int minutes = last.getTotalMinutes() == null ? 0 : last.getTotalMinutes();

        if (minutes >= 800) return 30;
        if (minutes >= 600) return 25;
        if (minutes >= 400) return 18;
        return 10;
    }

    private FitnessLevel levelOf(int score) {
        if (score >= 85) return FitnessLevel.EXCELLENT;
        if (score >= 70) return FitnessLevel.GOOD;
        if (score >= 50) return FitnessLevel.NORMAL;
        return FitnessLevel.NEED_IMPROVE;
    }

    private String buildComment(FitnessEvaluation ev, TrendResponse trend) {

        String levelText;

        switch (ev.getLevel()) {
            case EXCELLENT:
                levelText = "优秀";
                break;
            case GOOD:
                levelText = "良好";
                break;
            case NORMAL:
                levelText = "一般";
                break;
            case NEED_IMPROVE:
                levelText = "需改进";
                break;
            default:
                levelText = "未知";
        }

        String base = "体能综合评级：" + levelText + "，总分 " + ev.getTotalScore() + "/100。";

        if (trend.getSummary() != null) {
            base += " 趋势：" + trend.getSummary().text;
        }

        return base;
    }

    private int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }
}