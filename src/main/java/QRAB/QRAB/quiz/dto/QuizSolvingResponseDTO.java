package QRAB.QRAB.quiz.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class QuizSolvingResponseDTO {

    private String noteTitle;
    private List<QuizDTO> quizzes;

    @Getter
    @AllArgsConstructor
    public static class QuizDTO {
        private Long quizId;
        private Long quizSetId;
        private String question;
        private List<String> choices;
        private String difficulty;
    }
}

