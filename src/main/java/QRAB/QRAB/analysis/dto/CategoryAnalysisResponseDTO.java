package QRAB.QRAB.analysis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryAnalysisResponseDTO {
    private String parentCategoryName;
    private String categoryName;
    private int solvedQuizCount;
    private float categoryAccuracy;
}
