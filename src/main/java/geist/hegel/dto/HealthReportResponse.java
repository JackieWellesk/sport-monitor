package geist.hegel.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class HealthReportResponse {
    private Long userId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime rangeStart;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime rangeEnd;

    private TrendResponse trend;
    private FitnessEvaluation evaluation;
    private List<SuggestionItem> suggestions;

    public HealthReportResponse(Long userId, LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                TrendResponse trend, FitnessEvaluation evaluation, List<SuggestionItem> suggestions) {
        this.userId = userId;
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
        this.trend = trend;
        this.evaluation = evaluation;
        this.suggestions = suggestions;
    }
}