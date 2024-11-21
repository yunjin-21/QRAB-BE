package QRAB.QRAB.analysis.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class DetailedAnalysisResponseDTO {
    private String userAnalysis;
    private StrongCategoriesDTO strongCategories;
    private List<WeakCategoryDTO> weakCategories;

    @Getter @Setter
    public static class StrongCategoriesDTO {
        private List<String> finalCategoryName;
    }

    @Getter @Setter
    public static class WeakCategoryDTO {
        private String finalCategoryName;
        private List<String> studyTips;
        private List<ReferenceDTO> references;
    }

    @Getter @Setter
    public static class ReferenceDTO {
        private String title;
        private String link;
    }
}