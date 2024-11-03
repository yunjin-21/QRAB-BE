package QRAB.QRAB.note.dto;

import QRAB.QRAB.note.domain.Note;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FriendNoteResponseDTO {
    private Long noteId;
    private String title;
    private String categoryName;
    private String parentCategoryName;
    private String fileOrUrl;

    public static FriendNoteResponseDTO fromEntity(Note note) {
        return new FriendNoteResponseDTO(
                note.getId(),
                note.getTitle(),
                note.getCategory().getName(),
                note.getCategory().getParentCategory() != null ? note.getCategory().getParentCategory().getName() : "",
                note.getUrl() != null ? note.getUrl() : note.getFile()
        );
    }
}
