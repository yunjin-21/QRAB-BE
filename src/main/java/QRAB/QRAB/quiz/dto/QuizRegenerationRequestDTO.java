package QRAB.QRAB.quiz.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizRegenerationRequestDTO {
    private Long noteId;
    private int totalQuestions;
    private String quizType; // "new" 또는 "review"
}
