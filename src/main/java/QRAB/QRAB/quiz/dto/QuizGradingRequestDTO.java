package QRAB.QRAB.quiz.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuizGradingRequestDTO {
    private Long quizSetId;
    private List<AnswerDTO> answers;

    @Getter
    @Setter
    public static class AnswerDTO {
        private Long quizId;
        private int selectedAnswer;
    }
}
