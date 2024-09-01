package QRAB.QRAB.category.dto;

import QRAB.QRAB.category.domain.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryResponseDTO {
    private Long id;
    private String name;
    public static CategoryResponseDTO fromEntity(Category category){
        return new CategoryResponseDTO(category.getId(), category.getName());
    }
}
