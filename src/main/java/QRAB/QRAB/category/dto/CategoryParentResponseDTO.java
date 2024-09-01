package QRAB.QRAB.category.dto;

import QRAB.QRAB.category.domain.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryParentResponseDTO {
    private Long id;
    private String name;

    public static CategoryParentResponseDTO fromEntity(Category category){
        if(category.getParentCategory() == null) {
            return new CategoryParentResponseDTO(category.getId(), category.getName());
        }
        return null;
    }
}
