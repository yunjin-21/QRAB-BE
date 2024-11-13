package QRAB.QRAB.analysis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeakCategoryResponseDTO {
    private LowestAccuracyCategory lowestAccuracyCategory; // 가장 낮은 정답률 카테고리 정보
    private List<CategoryQuizGenerationDTO> categoryQuizGenerations; // 카테고리별 퀴즈 생성 횟수

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LowestAccuracyCategory {
        private String categoryName;
        private float categoryAccuracy;
    }
}