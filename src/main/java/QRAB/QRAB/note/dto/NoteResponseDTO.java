package QRAB.QRAB.note.dto;

import QRAB.QRAB.note.domain.Note;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NoteResponseDTO {
    private Long noteId;
    private String title;
    private String chatgptContent;
    private String categoryName; //자식
    private String parentCategoryName; //부모
    private String fileOrUrl;

    public static NoteResponseDTO fromEntity(Note note) {
        return new NoteResponseDTO(
                note.getId(),
                note.getTitle(),
                note.getChatgptContent().length() > 100 ? note.getChatgptContent().substring(0, 100) : note.getChatgptContent(),
                note.getCategory().getName(),
                note.getCategory().getParentCategory() != null ? note.getCategory().getParentCategory().getName() : "",
                note.getUrl() != null ? note.getUrl() : note.getFile()
        );
    }
}