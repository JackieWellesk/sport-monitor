package geist.hegel.service.impl;


import geist.hegel.dao.SportRecordMapper;
import geist.hegel.dto.*;
import geist.hegel.entity.SportRecord;
import geist.hegel.health.FitnessEvaluator;
import geist.hegel.health.SuggestionEngine;
import geist.hegel.health.TrendAnalyzer;
import geist.hegel.service.HealthReportService;
import geist.hegel.utils.DateRangeUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HealthReportServiceImpl implements HealthReportService {

    private final SportRecordMapper sportRecordMapper;
    private final TrendAnalyzer trendAnalyzer;
    private final FitnessEvaluator fitnessEvaluator;
    private final SuggestionEngine suggestionEngine;

    public HealthReportServiceImpl(SportRecordMapper sportRecordMapper,
                                   TrendAnalyzer trendAnalyzer,
                                   FitnessEvaluator fitnessEvaluator,
                                   SuggestionEngine suggestionEngine) {
        this.sportRecordMapper = sportRecordMapper;
        this.trendAnalyzer = trendAnalyzer;
        this.fitnessEvaluator = fitnessEvaluator;
        this.suggestionEngine = suggestionEngine;
    }

    @Override
    public HealthReportResponse generate(Long userId, int months) {
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = DateRangeUtil.monthsAgoStart(months);

        // 1) 拉取原始明细（建议引擎可能需要）
        List<SportRecord> records =
                sportRecordMapper.selectByUserAndTimeRange(userId, start, end);

        // 2) 趋势分析（按月聚合）
        List<TrendMonthAgg> monthAggs = sportRecordMapper.selectMonthAgg(userId, start, end);
        List<RunMonthAgg> runAggs = sportRecordMapper.selectRunMonthAgg(userId, start, end);

        TrendResponse trend = trendAnalyzer.build(monthAggs, runAggs);

        // 3) 体能评估（给分+等级）
        FitnessEvaluation evaluation = fitnessEvaluator.evaluate(trend, records);

        // 4) 改进建议（规则引擎）
        List<SuggestionItem> suggestions = suggestionEngine.generate(trend, evaluation, records);

        return new HealthReportResponse(userId, start, end, trend, evaluation, suggestions);
    }
}