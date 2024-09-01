package QRAB.QRAB.category.dto;

import QRAB.QRAB.category.domain.Category;
import QRAB.QRAB.user.domain.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryUpdateDTO {
    private String email;
    private String categoryName;
    private Long categoryId;

    public Category toEntity(User user){
        Category category = Category.builder()
                .name(categoryName)
                .user(user)
                .build();
        return category;
    }
}
