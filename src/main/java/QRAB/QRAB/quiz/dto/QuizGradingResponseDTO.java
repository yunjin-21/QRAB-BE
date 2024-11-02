package QRAB.QRAB.quiz.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class QuizGradingResponseDTO {
    private String noteTitle;
    private int score;
    private int correctCount;
    private int totalQuestions;
    private LocalDateTime takenAt;
    private List<QuizResultDetailDTO> quizzes;

    @Getter
    @Setter
    public static class QuizResultDetailDTO {
        private Long quizId;
        private String difficulty;
        private String question;
        private List<String> choices;
        private int selectedAnswer;
        private int correctAnswer;
        private boolean isCorrect;
        private String explanation;

        public void setIsCorrect(boolean isCorrect) {
            this.isCorrect = isCorrect;
        }
    }
}