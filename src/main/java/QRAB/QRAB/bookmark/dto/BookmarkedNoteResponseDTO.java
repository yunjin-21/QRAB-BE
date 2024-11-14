package QRAB.QRAB.bookmark.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookmarkedNoteResponseDTO {
    private Long noteId;
    private String title;
    private Long totalBookmarkedQuizzes;
    private int quizGenerationCount;
}