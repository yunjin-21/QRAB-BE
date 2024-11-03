package QRAB.QRAB.friend.dto;

import QRAB.QRAB.category.domain.Category;
import QRAB.QRAB.note.domain.Note;
import QRAB.QRAB.user.domain.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AddFriendNoteRequestDTO {
    private String email;
    private Long categoryId;

    public Note toEntity(User user, Category category){
        Note note = Note.builder()
                .user(user)
                .category(category)
                .build();
        return note;
    }
}
