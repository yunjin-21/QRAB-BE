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
public class CategoryRequestDTO {
    private String email;
    private String categoryName; //카테고리 이름
    public Category toEntity(User user){
        Category category = Category.builder()
                .name(categoryName)
                .user(user)
                .build();
        return category;
    }

}
