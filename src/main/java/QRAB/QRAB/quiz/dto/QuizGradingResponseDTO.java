package QRAB.QRAB.quiz.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
public class QuizGradingResponseDTO {
    private Long quizSetId;
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
        private String explanation;
        private boolean isCorrect;

        public void setIsCorrect(boolean isCorrect) {
            this.isCorrect = isCorrect;
        }
        //Jackson이 correct로 직렬화하는 것 방지
        @JsonProperty("isCorrect")
        public boolean isCorrect() {
            return isCorrect;
        }

        public Boolean getCorrect() {
            return isCorrect;
        }

        public void setCorrect(Boolean isCorrect) {
            this.isCorrect = isCorrect;
        }
    }
}