package QRAB.QRAB.category.dto;

import QRAB.QRAB.category.domain.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryChildResponseDTO {
    private Long id;
    private String name;

    public static CategoryChildResponseDTO fromEntity(Category category){
        if(category.getParentCategory() != null) {
            return new CategoryChildResponseDTO(category.getId(), category.getName());
        }
        return null;
    }
}
