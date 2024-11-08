package QRAB.QRAB.quiz.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecentWrongQuizDTO {
    private Long quizId;
    private String question;
    private List<String> choices;
    private int selectedAnswer;
    private int correctAnswer;
    private boolean isCorrect;
}

