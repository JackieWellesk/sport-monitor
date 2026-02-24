package geist.hegel.dto;

import lombok.Data;

@Data
public class FitnessEvaluation {
    private int totalScore;         // 0-100
    private int aerobicScore;       // 0-40
    private int consistencyScore;   // 0-30
    private int loadScore;          // 0-30
    private FitnessLevel level;
    private String comment;         // 解释性结论
}
