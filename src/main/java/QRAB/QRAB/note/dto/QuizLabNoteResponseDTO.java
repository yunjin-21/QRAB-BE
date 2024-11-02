package QRAB.QRAB.note.dto;

import QRAB.QRAB.note.domain.Note;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QuizLabNoteResponseDTO {
    private Long noteId;
    private String title;
    private String chatgptContent;
    private String categoryName;
    private String parentCategoryName;
    private String fileOrUrl;
    private int quizGenerationCount; // 퀴즈 생성 횟수 추가

    public static QuizLabNoteResponseDTO fromEntity(Note note) {
        return new QuizLabNoteResponseDTO(
                note.getId(),
                note.getTitle(),
                note.getChatgptContent().length() > 100 ? note.getChatgptContent().substring(0, 100) : note.getChatgptContent(),
                note.getCategory().getName(),
                note.getCategory().getParentCategory() != null ? note.getCategory().getParentCategory().getName() : "",
                note.getUrl() != null ? note.getUrl() : note.getFile(),
                note.getQuizGenerationCount() // 퀴즈 생성 횟수 가져오기
        );
    }
}

