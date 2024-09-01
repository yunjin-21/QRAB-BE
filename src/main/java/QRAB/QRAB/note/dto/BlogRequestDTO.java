package QRAB.QRAB.note.dto;

import QRAB.QRAB.category.domain.Category;
import QRAB.QRAB.user.domain.User;
import QRAB.QRAB.note.domain.Note;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BlogRequestDTO {
    private String email;
    private String url; //블로그 or 페이지 주소
    private Long categoryId; // 카테고리 ID

    public Note toEntity(User user, Category category){
        Note note = Note.builder()
                .url(url)
                .user(user)
                .category(category)
                .build();
        return note;
    }

}
