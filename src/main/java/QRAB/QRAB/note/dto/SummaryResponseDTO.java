package QRAB.QRAB.note.dto;

import QRAB.QRAB.note.domain.Note;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SummaryResponseDTO {
    private Long noteId;
    private String title;
    private String chatgptContent;
    private String categoryName;
    private String parentCategoryName;

    public static SummaryResponseDTO fromEntity(Note note){
        return new SummaryResponseDTO(note.getId(), note.getTitle(), note.getChatgptContent(), note.getCategory().getName() , note.getCategory().getParentCategory() != null ? note.getCategory().getParentCategory().getName() : "");
    }
}
