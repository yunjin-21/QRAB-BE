package QRAB.QRAB.category.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryDeleteRequestDTO {
    private List<Long> categoryIds;

    public void validate() {
        if (categoryIds == null || categoryIds.isEmpty()) {
            throw new IllegalArgumentException("Category IDs cannot be null or empty");
        }
    }
}
