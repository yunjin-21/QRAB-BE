package QRAB.QRAB.analysis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryQuizGenerationDTO {
    private String categoryName;
    private int quizGenerationCount;
}
