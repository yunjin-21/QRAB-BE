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
    private String categoryName;
    private String parentCategoryName;

    public static NoteResponseDTO fromEntity(Note note){
        return new NoteResponseDTO(
                note.getId(),
                note.getTitle(),
                note.getChatgptContent().length() > 10 ? note.getChatgptContent().substring(0, 10) : note.getChatgptContent(),
                note.getCategory().getName(),
                note.getCategory().getParentCategory() != null ? note.getCategory().getParentCategory().getName() : "");
    }
}
