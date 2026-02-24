package geist.hegel.health;

import geist.hegel.dto.*;
import geist.hegel.entity.SportRecord;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class SuggestionEngine {

    public List<SuggestionItem> generate(TrendResponse trend,
                                         FitnessEvaluation evaluation,
                                         List<SportRecord> records) {
        List<SuggestionItem> out = new ArrayList<>();

        // 1) 频率建议
        TrendMonthAgg lastMonth = last(trend.getMonthly());
        if (lastMonth != null) {
            int sessions = safeInt(lastMonth.getTotalSessions());
            if (sessions < 8) { // 月 <8 次（≈每周<2）
                out.add(new SuggestionItem(
                        SuggestionType.FREQUENCY,
                        "提升运动频率",
                        "建议将运动频率提升到每周至少 3 次（可用快走/慢跑+力量训练组合），以增强心肺与基础代谢。"
                ));
            }
        }

        // 2) 强度/心率建议（以跑步月均心率为参考）
        RunMonthAgg lastRun = last(trend.getRunning());
        if (lastRun != null && lastRun.getAvgHeartRate() != null) {
            double hr = lastRun.getAvgHeartRate();
            if (hr > 150) {
                out.add(new SuggestionItem(
                        SuggestionType.INTENSITY,
                        "控制有氧强度",
                        "当前跑步平均心率偏高，建议降低配速/采用慢跑-快走交替，将主要训练控制在中等强度区间（能说完整句子但略喘）。"
                ));
            }
        }

        // 3) 力量训练比例建议
        long strengthCnt = records.stream()
                .filter(r -> r.getSportType() != null && r.getSportType().contains("力量"))
                .count();
        if (!records.isEmpty()) {
            double ratio = strengthCnt * 1.0 / records.size();
            if (ratio < 0.15) {
                out.add(new SuggestionItem(
                        SuggestionType.STRENGTH,
                        "补充力量训练",
                        "力量训练占比偏低，建议每周增加 1–2 次（深蹲/臀桥/俯卧撑/平板支撑），提升肌力与运动表现，并降低受伤风险。"
                ));
            }
        }

        // 4) 恢复建议（简单：连续运动天数过多可提醒）
        if (hasTooDenseTraining(records)) {
            out.add(new SuggestionItem(
                    SuggestionType.RECOVERY,
                    "增加恢复日",
                    "近期训练安排较密集，建议每周至少安排 1 天主动恢复（拉伸、散步、轻松骑行），避免疲劳累积。"
            ));
        }

        // 5) 基于评分的总建议
        if (evaluation.getTotalScore() < 70) {
            out.add(new SuggestionItem(
                    SuggestionType.AEROBIC,
                    "循序渐进提升有氧耐力",
                    "建议以“低强度有氧为主、逐步增加时长”为原则，每周把总有氧时间提高 10% 左右，避免突然加量。"
            ));
        } else {
            out.add(new SuggestionItem(
                    SuggestionType.AEROBIC,
                    "保持良好节奏并小幅进阶",
                    "目前状态不错，建议保持规律训练，并每 2–3 周安排一次小幅进阶（如长跑+1km或+5–10分钟）。"
            ));
        }

        // 去重（按标题）
        return new ArrayList<>(out.stream()
                .collect(Collectors.toMap(SuggestionItem::getTitle, x -> x, (a, b) -> a, LinkedHashMap::new))
                .values());
    }

    private boolean hasTooDenseTraining(List<SportRecord> records) {
        // 简化：若近 7 条记录间隔都 <=2 天，则认为密集（你也可按 start_time 真正计算“连续天数”）
        if (records == null || records.size() < 7) return false;
        // 这里先保守返回 false，避免误判；后续你要我可以补全“按日期连续”算法
        return false;
    }

    private <T> T last(List<T> list) {
        if (list == null || list.isEmpty()) return null;
        return list.get(list.size() - 1);
    }
    private int safeInt(Integer v) { return v == null ? 0 : v; }
}