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
public class CategoryChildRequestDTO {
    private String email;
    private String categoryName;
    private Long parentId; // 자식 카테고리의 경우 부모 카테고리의 id

    public Category toEntity(User user){
        Category category = Category.builder()
                .name(categoryName)
                .user(user)
                .build();
        return category;
    }
}
