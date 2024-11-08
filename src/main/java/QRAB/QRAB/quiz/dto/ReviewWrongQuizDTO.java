package QRAB.QRAB.quiz.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReviewWrongQuizDTO {
    private String noteTitle;
    private List<QuizDetail> quizzes;

    public ReviewWrongQuizDTO(String noteTitle, List<QuizDetail> quizzes) {
        this.noteTitle = noteTitle;
        this.quizzes = quizzes;
    }

    @Getter
    @Setter
    public static class QuizDetail {
        private Long quizId;
        private Long quizSetId;
        private String question;
        private List<String> choices;
        private String difficulty;

        public QuizDetail(Long quizId, Long quizSetId, String question, List<String> choices, String difficulty) {
            this.quizId = quizId;
            this.quizSetId = quizSetId;
            this.question = question;
            this.choices = choices;
            this.difficulty = difficulty;
        }
    }
}

