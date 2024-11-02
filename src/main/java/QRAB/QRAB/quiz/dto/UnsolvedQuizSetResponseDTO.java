package QRAB.QRAB.quiz.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnsolvedQuizSetResponseDTO {
    private Long quizSetId;
    private Long noteId;
    private String noteTitle;
    private LocalDateTime createdAt;
    private String solvedAt;
    private String status;
    private String categoryName;
    private String parentCategoryName;

    public UnsolvedQuizSetResponseDTO(Long quizSetId, Long noteId, String noteTitle,
                                      LocalDateTime createdAt, String solvedAt, String status, String categoryName, String parentCategoryName) {
        this.quizSetId = quizSetId;
        this.noteId = noteId;
        this.noteTitle = noteTitle;
        this.createdAt = createdAt;
        this.solvedAt = solvedAt;
        this.status = status;
        this.categoryName = categoryName;
        this.parentCategoryName = parentCategoryName;
    }

}

