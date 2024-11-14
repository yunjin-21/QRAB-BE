package QRAB.QRAB.analysis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyAnalysisResponseDTO {
    private int learningDays;
    private int solvedQuizCount;
    private float averageAccuracy;
    private List<CategoryAnalysisResponseDTO> categories;
}

