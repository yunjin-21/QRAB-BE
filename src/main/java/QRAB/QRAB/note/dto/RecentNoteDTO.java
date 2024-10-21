package QRAB.QRAB.note.dto;

import QRAB.QRAB.note.domain.Note;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class RecentNoteDTO {
    private Long noteId;
    private String title;
    private String categoryName; //현재 카테로 이름을 가져옴! (부모 or 자식 일 수 도 )
    private LocalDateTime createdAt;
    private String shortChatgptContent;
    private String longChatgptContent;

    public static RecentNoteDTO fromEntity(Note note){
        return new RecentNoteDTO(
                note.getId(),
                note.getTitle(),
                note.getCategory().getName(),
                note.getCreatedAt(),
                note.getChatgptContent().length() > 100 ? note.getChatgptContent().substring(0, 100) : note.getChatgptContent(),
                note.getChatgptContent().length() > 250 ? note.getChatgptContent().substring(0, 250) : note.getChatgptContent());
    }
}
